package com.glacier.spider.crawler;

import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pageprocessor.SearchPageProcessor;
import com.glacier.spider.crawler.pageprocessor.UserPageProcessor;
import com.glacier.spider.crawler.pipeline.UserInfo;
import com.glacier.spider.crawler.pipeline.UserStruct;
import com.glacier.spider.login.GetAccounts;
import com.glacier.spider.login.LoginCN;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

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
        httpClient = loginCN.login(GetAccounts.accounts("weibo"));  //使用数据库中weibo账号进行登陆，返回维护后的httpclient

        Downloader.setClient(httpClient);       //为Downloader模块设置HttpClient
//        Document document = Downloader.document("http://weibo.cn/3217179555", Downloader.HTTP_GET);     //给予起始路径
//        //Document document = Downloader.document("http://weibo.cn/musicmusicmusic", Downloader.HTTP_GET);
//        UserPageProcessor userPageProcessor = new UserPageProcessor();
//        UserInfo userInfo = userPageProcessor.getUserInfo(document);
//        System.out.println(userInfo);
//        userPageProcessor.getFollowMap(document);
//        userPageProcessor.getFansMap(document);
//        userPageProcessor.getWeibo(document);
//
//        UserStruct userStruct = userPageProcessor.getUserStruct();
//        userStruct.save4xml();


        SearchPageProcessor searchPageProcessor = new SearchPageProcessor();
        searchPageProcessor.getSearchList("汽车", true, true);
        searchPageProcessor.getSearchList("轿车", true, true);
        searchPageProcessor.getSearchList("越野车", true, true);
        searchPageProcessor.getSearchList("客车", true, true);

    }
}
