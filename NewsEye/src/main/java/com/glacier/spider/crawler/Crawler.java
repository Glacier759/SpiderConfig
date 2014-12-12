package com.glacier.spider.crawler;

import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.downloader.Downloader;
import org.jsoup.nodes.Document;

/**
 * Created by glacier on 14-12-12.
 */
public class Crawler {

    Configure.Config config;

    public Crawler(Configure.Config config) {
        this.config = config;
    }

    public void start() {
        for (Configure.NewspaperClass newspaper:config.newspaperList) {
            Document newspaperDoc = Downloader.newspaperDocument(newspaper.paper_starturl, newspaper.paper_encode);

        }
    }
}
