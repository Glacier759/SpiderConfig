package com.glacier.spider.crawler;

import com.glacier.spider.crawler.scheduler.BloomFilter;
import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pageprocessor.ContentPageProcessor;
import com.glacier.spider.crawler.pageprocessor.NewsPageProcessor;
import com.glacier.spider.crawler.pageprocessor.PagePageProcessor;
import com.glacier.spider.crawler.scheduler.RedisScheduler;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by glacier on 14-12-12.
 */
public class Crawler {

    public static Configure.Config config;
    public static RedisScheduler redisScheduler;
    public static BloomFilter bloomFilter;

    private static Logger logger = Logger.getLogger(Crawler.class.getName());

    public Crawler(Configure.Config configure) {
        config = configure;
        logger.info("[初始化] 配置文件初始化完成");
    }

    public void start() {
        logger.info("[初始化] 抓取模块启动");
        PagePageProcessor pagePageProcessor = new PagePageProcessor(config);
        NewsPageProcessor newsPageProcessor = new NewsPageProcessor(config);
        ContentPageProcessor contentPageProcessor = new ContentPageProcessor(config);

        redisScheduler = new RedisScheduler("test");
        List<Thread> contentThreadList = new ArrayList<Thread>();

        logger.info("[初始化] 正在预抓取报刊正文地址");
        for (Configure.NewspaperClass newspaper:config.newspaperList) {
            Document newspaperDoc = Downloader.newspaperDocument(newspaper.paper_starturl, newspaper.paper_encode);
            HashMap<String,String> pageMap = pagePageProcessor.parsePage(newspaperDoc);

            for ( String pageLink:pageMap.keySet() ) {
                Document newsDoc = Downloader.document(pageLink, newspaper.paper_encode);
                HashSet<String> newsSet = newsPageProcessor.parseNews(newsDoc);
                /**
                 * 在此将上一步获得的newsLink集合全部加入缓存队列(附加了编码版面名称 以'&'分割)
                 * 不影响当前线程的正常执行，之后采用多线程的方式进行数据获取
                 * */


                for ( String newsLink:newsSet ) {
                    String value = newsLink + "," + newspaper.paper_encode + "," + pageMap.get(pageLink) + "," + newspaper.paper_name;
                    redisScheduler.put(value);
                }
                logger.debug("[Thread] 一个新的线程被创建");
                Thread obj = new Thread(contentPageProcessor);
                contentThreadList.add(obj);
                obj.start();
            }
        }
        for (Thread obj:contentThreadList) {
            try {
                obj.join();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
