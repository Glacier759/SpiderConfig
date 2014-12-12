package com.glacier.spider.configure;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

/**
 * Created by glacier on 14-12-12.
 */
public class ParseConfigure extends Configure {

    private static Logger logger = Logger.getLogger(ParseConfigure.class.getName());
    private Document xmlDoc;
    private Element root;

    public ParseConfigure(String configXML) {
        try {
            SAXReader saxReader = new SAXReader();
            xmlDoc = saxReader.read(new ByteArrayInputStream(configXML.getBytes()));
            root = xmlDoc.getRootElement();

            List<Element> classList = root.elements("class");
            logger.info("[类别总数] " + classList.size());
            int newspaperCount = 0;
            for (Iterator<Element> iterator = classList.listIterator(); iterator.hasNext();  ) {
                Element newspaper = iterator.next();
                newspaperCount += newspaper.elements("newspaper").size();
            }
            logger.info("[报刊总数] " + newspaperCount);

            for (Iterator<Element> iterator = classList.listIterator(); iterator.hasNext(); ) {
                try {
                    Element newspaper = iterator.next();
                    Config configObj = new Config();
                    List<Element> newspaperList = newspaper.elements("newspaper");

                    for ( Iterator<Element> newsIterator = classList.iterator(); newsIterator.hasNext(); ) {
                        try {
                            Element newsInfo = newsIterator.next();
                            NewspaperClass newsObj = new NewspaperClass();
                            newsObj.paper_name = newsInfo.attributeValue("paper_name");
                            newsObj.paper_starturl = newsInfo.attributeValue("paper_starturl");
                            newsObj.paper_encode = newsInfo.attributeValue("paper_encode");
                            configObj.newspaperList.add(newsObj);
                        }catch (Exception e) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            e.printStackTrace(new PrintStream(baos));
                            logger.error(baos.toString());
                        }
                    }

                    Element pageEle = newspaper.element("page");
                    configObj.page.page_url_outer = pageEle.element("page_url_outer").getText();
                    configObj.page.page_url_node = pageEle.element("page_url_outer").attributeValue("page_url_node");
                    configObj.page.page_url_tag = pageEle.element("page_url_tag").getText();
                    Element pageAttrEle = pageEle.element("page_url_attr");
                    configObj.page.attr_value = pageAttrEle.attributeValue("attr_value");
                    configObj.page.page_url_draw = pageAttrEle.attributeValue("page_url_draw");
                    Element pageDrawEle = pageAttrEle.element("page_draw");
                    configObj.page.page_url_sub = pageDrawEle.element("page_url_sub").getText();
                    configObj.page.page_url_front = pageDrawEle.element("page_url_front").getText();
                    configObj.page.page_url_rear = pageDrawEle.element("page_url_rear").getText();
                    configObj.page.page_url_have = pageDrawEle.element("page_url_have").getText();

                    Element newsEle = newspaper.element("news");
                    configObj.news.news_url_outer = newsEle.element("news_url_outer").getText();
                    configObj.news.news_url_node = newsEle.element("news_url_outer").attributeValue("news_url_node");
                    configObj.news.news_url_tag = newsEle.element("news_url_tag").getText();
                    Element newsAttrEle = newsEle.element("news_url_attr");
                    configObj.news.attr_value = newsAttrEle.attributeValue("attr_value");
                    configObj.news.news_url_draw = newsAttrEle.attributeValue("news_url_draw");
                    Element newsDrawEle = newsAttrEle.element("news_draw");
                    configObj.news.news_url_sub = newsDrawEle.element("news_url_sub").getText();
                    configObj.news.news_url_front = newsDrawEle.element("news_url_front").getText();
                    configObj.news.news_url_rear = newsDrawEle.element("news_url_rear").getText();
                    configObj.news.news_url_have = newsDrawEle.element("news_url_have").getText();

                    Element contentEle = newspaper.element("content");
                    Element titleEle = contentEle.element("title");
                    configObj.content.title_outer = titleEle.element("title_outer").getText();
                    configObj.content.title_tag = titleEle.element("title_tag").getText();
                    configObj.content.title_draw = titleEle.element("title_tag").attributeValue("title_draw");
                    configObj.content.title_attr = titleEle.element("title_attr").getText();
                    configObj.content.time_format = contentEle.element("time_format").getText();
                    Element textEle = contentEle.element("text");
                    configObj.content.content_outer = textEle.element("content_outer").getText();
                    configObj.content.content_tag = textEle.element("content_tag").getText();
                    Element imageEle = contentEle.element("image");
                    configObj.content.img_outer = imageEle.element("img_outer").getText();
                    configObj.content.img_tag = imageEle.element("img_tag").getText();

                    configList.add(configObj);
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
    }
}
