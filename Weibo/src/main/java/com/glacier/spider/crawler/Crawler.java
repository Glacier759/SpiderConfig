package com.glacier.spider.crawler;

import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pageprocessor.UserPageProcessor;
import com.glacier.spider.login.GetAccounts;
import com.glacier.spider.login.LoginCN;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

/**
 * Created by glacier on 14-12-17.
 */
public class Crawler {

    private static Logger logger = Logger.getLogger(Crawler.class.getName());
    private DefaultHttpClient httpClient;

    public Crawler() {

    }

    public void start() {
        LoginCN loginCN = new LoginCN();
        httpClient = loginCN.login(GetAccounts.accounts("weibo"));

        Downloader.setClient(httpClient);
        Document document = Downloader.document("http://weibo.cn/slrui", Downloader.HTTP_GET);
        UserPageProcessor userPageProcessor = new UserPageProcessor();
        //Document fansDocument = Downloader.document(userPageProcessor.getURL(document).get("资料"), Downloader.HTTP_GET);
        userPageProcessor.getWeibo(document);
    }
}
