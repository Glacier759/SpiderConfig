package com.glacier.spider.crawler.pageprocessor;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by glacier on 14-12-17.
 */
public class UserPageProcessor {

    private static Logger logger = Logger.getLogger(UserPageProcessor.class.getName());

    public void getFansList( Document document ) {
        try {
            logger.info("[解析] 正在获取粉丝列表...");

            Elements fansOuter = document.select("div[class=c]").select("table");
            for ( Element fansTable:fansOuter ) {
                Element fansTD = fansTable.select("td[valign=top]").last();
                Element fansA = fansTD.select("a[href]").first();
                String fansName = fansA.text();
                String fansLink = fansA.attr("abs:href");
                System.out.println(fansName + "\t" + fansLink);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }
}
