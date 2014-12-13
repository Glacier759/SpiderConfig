package com.glacier.spider;

import com.glacier.spider.bloomfilter.BloomFilter;
import com.glacier.spider.configure.Configure;
import com.glacier.spider.configure.ParseConfigure;
import com.glacier.spider.crawler.Crawler;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by glacier on 14-12-12.
 */
public class NewsEye {

    private static Logger logger = Logger.getLogger(NewsEye.class.getName());


    public static void main(String[] args) {
        try {
            logger.info("[程序开始执行]");

            if ( initBloomFilter() )
                logger.info("[BloomFilter] 初始化成功");

            String xml = FileUtils.readFileToString(new File("config.xml"));
            ParseConfigure configure = new ParseConfigure(xml);
            for (ParseConfigure.Config configObj:configure.configList) {
                Crawler crawler = new Crawler(configObj);
                crawler.start();
            }

            if ( saveBloomFilter() )
                logger.info("[BloomFilter] 保存成功");
            logger.info("[程序执行完毕]");
        }catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    /**
     * 初始化布隆过滤器，如果程序运行当天已经运行过，则本地会产生一个"filter.record"文件用于存储抓取进度
     * 若存在该文件则直接使用对象流的方式将其读入
     * 若不存在该文件，则实例化一个BloomFilter对象
     * @return 是否初始化成功
     * */
    private static boolean initBloomFilter() {    //布隆过滤器初始化
        try {
            File filterFile = new File("filter.record");    //布隆过滤器以对象流保存在文件，后期可以考虑存入数据库
            if ( filterFile.exists() ) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filterFile));
                Crawler.bloomFilter = (BloomFilter)ois.readObject();
                SimpleDateFormat format = new SimpleDateFormat("dd");
                String today = format.format(new Date());
                if ( !today.equals(Crawler.bloomFilter.getRecodeDate()) )
                    Crawler.bloomFilter = new BloomFilter(100000);
            }
            else {
                Crawler.bloomFilter = new BloomFilter(100000);
            }
            return true;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return false;
    }

    /**
     * 结束阶段抓取后保存当前布隆过滤器的状态，以对象流的方式存入"filter.record"文件中
     * @return 是否储存成功
     * */
    private static boolean saveBloomFilter() {
        try {
            File filterFile = new File("filter.record");
            if ( filterFile.exists() )
                filterFile.delete();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filterFile));
            oos.writeObject(Crawler.bloomFilter);
            return true;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return false;
    }
}
