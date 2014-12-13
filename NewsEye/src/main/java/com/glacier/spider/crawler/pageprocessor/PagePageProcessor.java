package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.configure.Configure;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Created by glacier on 14-12-12.
 */
public class PagePageProcessor extends PageProcessor{

    private static Logger logger = Logger.getLogger(PagePageProcessor.class.getName());

    public PagePageProcessor(Configure.Config config) {
        super(config);
    }

    /**
     * 解析版面信息
     * @param document 版面地址所在网页DOM文档树(报刊信息所在首页)
     * @return 返回包含有版面地址-版面名称的HashMap
     * */
    public HashMap<String,String> parsePage(Document document) {
        HashMap<String,String> pageMap = new HashMap<String, String>();
        try {
            /**
             * pageEles为包含有版面链接信息的element集合
             * */
            Elements pageEles = null;
            if (config.page.page_url_node.equals("elements")) {
                pageEles = document.select(config.page.page_url_outer)
                                    .select(config.page.page_url_tag);
            }
            else {
                pageEles = document.select(config.page.page_url_outer).first()
                                    .select(config.page.page_url_tag);
            }
            /**
             * pageEle为单个版面信息链接的element
             * */
            for ( Element pageEle:pageEles ) {
                try {
                    String pageLink = null;
                    /**
                     * pageLink为需要提取的版面链接
                     * */

                    /**
                     * 如果draw字段为fullstring则提取对应attr_value的全部值
                     *
                     * 如果draw字段为substring表示仅需要其中的某个子串
                     * 提取出对应attr_value属性全部值，截取sub字段中对应的坐标之间的子串
                     * */
                    if ( config.page.page_url_draw.equals("fullstring") ) {
                        pageLink = pageEle.attr(config.page.attr_value);
                    }
                    else if (config.page.page_url_draw.equals("substring")) {
                        pageLink = pageEle.attr(config.page.attr_value);
                        String[] indexArry = config.page.page_url_sub.split(",");
                        pageLink = pageLink.substring(Integer.parseInt(indexArry[0]), Integer.parseInt(indexArry[1]));
                    }
                    /**
                     * front表示pageLink需要添加的前缀子串
                     * rear表示pageLink需要添加的后缀子串
                     * have表示pageLink必须包含的子串
                     * */
                    if (config.page.page_url_front != null && config.page.page_url_front.length() > 0) {
                        pageLink = config.page.page_url_front + pageLink;
                    }
                    if (config.page.page_url_rear != null && config.page.page_url_rear.length() > 0) {
                        pageLink = pageLink + config.page.page_url_rear;
                    }
                    if (config.page.page_url_have != null && config.page.page_url_have.length() > 0) {
                        if (!pageLink.contains(config.page.page_url_have)) {
                            continue;
                        }
                    }
                    /**
                     * 将最终获得的pageLink版面链接加入集合中
                     * key为pageLink，value为对应的版面名称(即pageEle对应的正文部分)
                     * */
                    pageMap.put(pageLink, pageEle.text());
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
        return pageMap;
    }
}
