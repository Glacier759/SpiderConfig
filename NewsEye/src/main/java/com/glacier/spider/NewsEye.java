package com.glacier.spider;

import com.glacier.spider.configure.GetConfigure;
import com.glacier.spider.crawler.pipeline.SaveFormat;
import com.glacier.spider.crawler.scheduler.BloomFilter;
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
    public static String USERNAME, FILENAME;

    public static void main(String[] args) {
        try {

            if ( args.length < 1 ) {
                System.out.println("缺少必要参数");
                System.out.println("args - 用户id");
                System.exit(1);
            }
            else {
                Integer id = 0;
                String idArg = args[0];
                if ( isInteger(idArg) )
                    id = Integer.parseInt(idArg);
                else {
                    System.out.println("输入有误");
                    System.exit(1);
                }
                USERNAME = GetConfigure.getUsername(id);
                logger.info("[程序开始执行]");

                ParseConfigure configure = new ParseConfigure(GetConfigure.getConfigure(id));
                for (ParseConfigure.Config configObj : configure.configList) {
                    Crawler crawler = new Crawler(configObj);
                    Crawler.bloomFilter = GetConfigure.getBloomFilter(id);
                    logger.info("[BloomFilter] 初始化成功");

                    crawler.start(NewsEye.USERNAME);

                    GetConfigure.setBloomFilter(id, Crawler.bloomFilter);
                    logger.info("[BloomFilter] 保存成功");
                }
                logger.info("[压缩] 数据正在压缩打包...");
                if (SaveFormat.compressFile()) {
                    logger.info("[压缩] 数据压缩打包成功 保存至 " + NewsEye.FILENAME);
                    GetConfigure.setFileName(id);
                }
                else {
                    logger.info("[压缩] 数据压缩打包出现异常");
                }

                logger.info("[程序执行完毕]");
            }
        }catch (Exception e){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

}
