package com.glacier.spider.crawler.pageprocessor;


import com.glacier.spider.configure.Configure;
import com.glacier.spider.crawler.Crawler;
import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pipeline.SaveFormat;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by glacier on 14-12-13.
 */
public class ContentPageProcessor extends PageProcessor implements Runnable {

    private static Logger logger = Logger.getLogger(ContentPageProcessor.class.getName());

    public ContentPageProcessor(Configure.Config config) {
        super(config);
    }

    public void parseContent(Document document) {
        try {
            Elements titleEle = null;
            titleEle = document.select(config.content.title_outer).select(config.content.title_tag);

            String title = null;
            if ( config.content.title_draw.equals("text") ) {
                title = titleEle.text();
            }
            else if ( config.content.title_draw.equals("attr") ) {
                title = titleEle.attr(config.content.title_attr);
            }

            String publishDate = getPublishDate(document, config.content.time_format);

            Elements contentEles = document.select(config.content.content_outer).select(config.content.content_tag);
            String content = "";
            for ( Element contentEle:contentEles ) {
                content += contentEle.text() + "\n";
            }

            List<String> imgList = new ArrayList<String>();
            Elements imgEles = document.select(config.content.img_outer).select(config.content.img_tag);
            for ( Element imgEle:imgEles ) {
                String imgSrc = imgEle.attr("abs:src");
                if ( (imgSrc.contains("jpg") || imgSrc.contains("png")) && !imgList.contains(imgSrc) )
                    imgList.add(imgSrc);
            }

            SaveFormat.title = title;
            SaveFormat.publishDate = publishDate;
            SaveFormat.body = content;
            SaveFormat.img = imgList;

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private String getPublishDate( Document document, String rulePublishDate ) {
        String publishDateStr = null;
        try {
            SimpleDateFormat dateFormat = null;
            String url = document.baseUri();
            if ( rulePublishDate.equals("xxxx-xx/xx") ) {
                dateFormat = new SimpleDateFormat("yyyy-MM/dd");
                String now = dateFormat.format(new Date());
                now = now.substring(0, 8);
                publishDateStr = url.substring(url.indexOf(now), url.indexOf(now)+10);
            }
            else if ( rulePublishDate.equals("xxxxxxxx") ) {
                dateFormat = new SimpleDateFormat("yyyyMMdd");
                String now = dateFormat.format(new Date());
                now = now.substring(0, 6);
                publishDateStr = url.substring(url.indexOf(now), url.indexOf(now)+8);
            }
            else if ( rulePublishDate.equals("xxxx年xx月xx日") ) {
                dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                //Document doc = getDocument(url, encode);
                publishDateStr = document.select("span[class=blue2]").last().text();
                publishDateStr = publishDateStr.substring(0, publishDateStr.indexOf("星期")-1);
            }
            if ( publishDateStr == null )
                return null;
            Date publishDate = dateFormat.parse(publishDateStr);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            publishDateStr = dateFormat.format(publishDate);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return publishDateStr;
    }


    public void run() {
        //synchronized (Crawler.bloomFilter) {
            while(Crawler.redisScheduler.length() > 0) {
                try {
                    String value = Crawler.redisScheduler.get();
                    String[] valueArry = value.split(",");
                    String newsLink = valueArry[0];
                    String encode = valueArry[1];
                    String pageName = valueArry[2];
                    String paperName = valueArry[3];
                    if (!Crawler.bloomFilter.contains(newsLink)) {
                        logger.info("[即将获取] " + newsLink + "\tThread-" + Thread.currentThread().getName());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        SaveFormat.encode = "UTF-8";
                        SaveFormat.language = "中文";
                        SaveFormat.crawlDate = dateFormat.format(new Date());
                        SaveFormat.newspaper = paperName;
                        SaveFormat.source = newsLink;
                        SaveFormat.page = pageName;

                        Document contentDoc = Downloader.document(newsLink, encode);
                        contentDoc.setBaseUri(newsLink);
                        parseContent(contentDoc);

                        String saveFile = SaveFormat.save();
                        logger.info("[获取完毕] 存储至" + saveFile);
                        Crawler.bloomFilter.add(newsLink);
                    } else {
                        logger.info("[BloomFilter] 已存在 " + newsLink);
                    }
                    Crawler.redisScheduler.remove(value);
                }catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    logger.error(baos.toString());
                }
            }
        //}
    }
}
