package com.glacier.spider.crawler;

import com.glacier.spider.configure.GetConfigure;
import com.glacier.spider.crawler.scheduler.BloomFilter;
import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pageprocessor.ContentPageProcessor;
import com.glacier.spider.crawler.pageprocessor.NewsPageProcessor;
import com.glacier.spider.crawler.pageprocessor.PagePageProcessor;
import com.glacier.spider.crawler.scheduler.RedisScheduler;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 初始化布隆过滤器，如果程序运行当天已经运行过，则本地会产生一个"filter.record"文件用于存储抓取进度
     * 若存在该文件则直接使用对象流的方式将其读入
     * 若不存在该文件，则实例化一个BloomFilter对象
     * @return 是否初始化成功
     * */
//    private static boolean initBloomFilter() {    //布隆过滤器初始化
//        try {
//            //File filterFile = new File("filter.record");    //布隆过滤器以对象流保存在文件，后期可以考虑存入数据库
//
//            if ( filterFile.exists() ) {
//                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filterFile));
//                Crawler.bloomFilter = (BloomFilter)ois.readObject();
//                SimpleDateFormat format = new SimpleDateFormat("dd");
//                String today = format.format(new Date());
//                if ( !today.equals(Crawler.bloomFilter.getRecodeDate()) )
//                    Crawler.bloomFilter = new BloomFilter(100000);
//            }
//            else {
//                Crawler.bloomFilter = new BloomFilter(100000);
//            }
//            logger.info("[BloomFilter] 初始化成功");
//            return true;
//        }catch (Exception e) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            e.printStackTrace(new PrintStream(baos));
//            logger.error(baos.toString());
//        }
//        return false;
//    }

    /**
     * 结束阶段抓取后保存当前布隆过滤器的状态，以对象流的方式存入"filter.record"文件中
     * @return 是否储存成功
     * */
//    private static boolean saveBloomFilter() {
//        try {
//            File filterFile = new File("filter.record");
//            if ( filterFile.exists() )
//                filterFile.delete();
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filterFile));
//            oos.writeObject(Crawler.bloomFilter);
//            logger.info("[BloomFilter] 保存成功");
//            return true;
//        }catch (Exception e) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            e.printStackTrace(new PrintStream(baos));
//            logger.error(baos.toString());
//        }
//        return false;
//    }

    public void start(String username) {
        logger.info("[初始化] 抓取模块启动 - " + username);
        PagePageProcessor pagePageProcessor = new PagePageProcessor(config);
        NewsPageProcessor newsPageProcessor = new NewsPageProcessor(config);
        ContentPageProcessor contentPageProcessor = new ContentPageProcessor(config);

        redisScheduler = new RedisScheduler(username);
        List<Thread> contentThreadList = new ArrayList<Thread>();

        logger.info("[初始化] 正在预抓取报刊正文地址");
        for (Configure.NewspaperClass newspaper:config.newspaperList) {
            Document newspaperDoc = Downloader.newspaperDocument(newspaper.paper_starturl, newspaper.paper_encode);
            HashMap<String,String> pageMap = pagePageProcessor.parsePage(newspaperDoc);

            for ( String pageLink:pageMap.keySet() ) {
                Document newsDoc = Downloader.document(pageLink, newspaper.paper_encode);
                HashSet<String> newsSet = newsPageProcessor.parseNews(newsDoc);
                /**
                 * 在此将上一步获得的newsLink集合全部加入缓存队列(附加了编码版面名称 以','分割)
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
