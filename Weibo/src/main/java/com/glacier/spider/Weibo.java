package com.glacier.spider;

import com.glacier.spider.crawler.Crawler;
import org.apache.log4j.Logger;

/**
 * Created by glacier on 14-12-13.
 */
public class Weibo {

    private static Logger logger = Logger.getLogger(Weibo.class.getName());

    public static void main(String[] agrs) {

        Crawler crawler = new Crawler();
        crawler.start();
    }
}
