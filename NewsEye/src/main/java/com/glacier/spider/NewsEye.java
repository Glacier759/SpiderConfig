package com.glacier.spider;

import org.apache.log4j.Logger;

/**
 * Created by glacier on 14-12-12.
 */
public class NewsEye {

    private static Logger logger = Logger.getLogger(NewsEye.class.getName());

    public static void main(String[] args) {
        try {
            logger.info("info");
            logger.debug("debug");
            logger.error("error");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
