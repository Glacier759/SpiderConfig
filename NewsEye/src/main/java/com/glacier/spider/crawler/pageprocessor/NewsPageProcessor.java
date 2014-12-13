package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.configure.Configure;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;

/**
 * Created by glacier on 14-12-13.
 */
public class NewsPageProcessor extends PageProcessor {

    private static Logger logger = Logger.getLogger(NewsPageProcessor.class.getName());

    public NewsPageProcessor(Configure.Config config) {
        super(config);
    }

    public HashSet<String> parseNews(Document document) {
        HashSet<String> newsSet = new HashSet<String>();
        try {
            Elements newsEles = null;
            if ( config.news.news_url_node.equals("elements") ) {
                newsEles = document.select(config.news.news_url_outer)
                                    .select(config.news.news_url_tag);
            }
            else if ( config.news.news_url_node.equals("element") ) {
                newsEles = document.select(config.news.news_url_outer).first()
                                    .select(config.news.news_url_tag);
            }
            for ( Element newsEle:newsEles ) {
                try {
                    String newsLink = null;
                    if ( config.news.news_url_draw.equals("fullstring") ) {
                        newsLink = newsEle.attr(config.news.attr_value);
                    }
                    else if ( config.news.news_url_draw.equals("substring") ) {
                        newsLink = newsEle.attr(config.news.attr_value);
                        String[] indexArry = config.news.news_url_sub.split(",");
                        newsLink = newsLink.substring(Integer.parseInt(indexArry[0]), Integer.parseInt(indexArry[1]));
                    }
                    if ( config.news.news_url_front != null && config.news.news_url_front.length() > 0 ) {
                        newsLink = config.news.news_url_front + newsLink;
                    }
                    if ( config.news.news_url_rear != null && config.news.news_url_rear.length() > 0 ) {
                        newsLink = newsLink + config.news.news_url_rear;
                    }
                    if ( config.news.news_url_have != null && config.news.news_url_have.length() > 0 ) {
                        if ( !newsLink.contains(config.page.page_url_have) ) {
                            continue;
                        }
                    }
                    if ( newsLink.contains("?div") )
                        newsLink = newsLink.substring(0, newsLink.indexOf('?'));
                    if ( newsSet.contains(newsLink) )
                        continue;
                    newsSet.add(newsLink);
                }catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    logger.error(baos.toString());
                }
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return newsSet;
    }
}
