package com.glacier.spider;

import com.glacier.spider.bloomfilter.BloomFilter;
import org.apache.log4j.Logger;

/**
 * Created by glacier on 14-12-12.
 */
public class NewsEye {

    private static Logger logger = Logger.getLogger(NewsEye.class.getName());

    public static void main(String[] args) {
        try {
            logger.info("[开始执行]");
            BloomFilter bloomFilter = new BloomFilter(1000);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
