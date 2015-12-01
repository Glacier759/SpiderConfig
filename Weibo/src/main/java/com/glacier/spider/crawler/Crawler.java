package com.glacier.spider.crawler;

import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pageprocessor.SearchPageProcessor;
import com.glacier.spider.crawler.pageprocessor.UserPageProcessor;
import com.glacier.spider.crawler.pipeline.Accounts;
import com.glacier.spider.crawler.pipeline.SearchAns;
import com.glacier.spider.crawler.pipeline.UserInfo;
import com.glacier.spider.crawler.pipeline.UserStruct;
import com.glacier.spider.login.GetAccounts;
import com.glacier.spider.login.LoginCN;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;

/**
 * Created by glacier on 14-12-17.
 */
public class Crawler {

    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private DefaultHttpClient httpClient;
    public static Configure config;

    public Crawler( Configure conf ) {
        config = conf;
        logger.info("[初始化] 配置文件加载完成");
    }

    /**
     * 启动爬虫
     * */
    public void start() {
        LoginCN loginCN = new LoginCN();            //实例化登陆模块，采用weibo.cn为微博登陆入口
        Accounts accounts = GetAccounts.accounts("weibo");
        httpClient = loginCN.login(accounts);  //使用数据库中weibo账号进行登陆，返回维护后的httpclient
        if ( httpClient == null ) {
            logger.warn("[登陆] - '" + accounts.getUsername() + "' 登录失败");
            System.exit(1);
        }
        Downloader.setClient(httpClient);       //为Downloader模块设置HttpClient

        switch(Configure.getInstance().getCrawl_type()) {
            case 0: break;
            case 1:
                runWithSearchPageProcessor();
                break;
            case 2:
                runWithUserPageProcessor();
                break;
            case 3:
                runWithSearchPageProcessor();
                runWithUserPageProcessor();
                break;
        }

    }

    private void runWithUserPageProcessor() {
        UserPageProcessor userPageProcessor = new UserPageProcessor();
        for ( String start_url : Configure.getInstance().getUser_list() ) {
            try {
                URL startURL = new URL(start_url);
                if ( startURL.getHost().equals("weibo.com") ) {
                    startURL = new URL(start_url.replace("weibo.com", "weibo.cn"));
                }
                logger.info("[任务] 开始任务 - " + startURL.toString());
                Document document = Downloader.document(startURL.toString(), Downloader.HTTP_GET);
                if (Configure.getInstance().isUser_info()) {
                    userPageProcessor.getUserInfo(document);
                }
                if (Configure.getInstance().isFollow_list()) {
                    userPageProcessor.getFollowMap(document);
                }
                if (Configure.getInstance().isFans_list()) {
                    userPageProcessor.getFansMap(document);
                }
                if (Configure.getInstance().isWeibo_list()) {
                    userPageProcessor.getWeibo(document);
                }
                userPageProcessor.save4xml();
                userPageProcessor.clear();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void runWithSearchPageProcessor() {
        SearchPageProcessor searchPageProcessor = new SearchPageProcessor();
        for ( String keywords : Configure.getInstance().getKeywords_list() ) {
            searchPageProcessor.getSearchList(keywords, Configure.getInstance().isUser_info(), true);
            searchPageProcessor.clear();
        }
    }
}
