package com.glacier.spider;

import com.glacier.spider.crawler.Crawler;
import org.apache.log4j.Logger;

/**
 * Created by glacier on 14-12-13.
 * @author Glacier<OurHon.759@gmail.com>
 */

public class Weibo {

    private static Logger logger = Logger.getLogger(Weibo.class.getName());

    public static void main(String[] agrs) {

        Crawler crawler = new Crawler(null);    //实例化微博爬虫
        crawler.start();                    //开始微博爬虫
    }
}
