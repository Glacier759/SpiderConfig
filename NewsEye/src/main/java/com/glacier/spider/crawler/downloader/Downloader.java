package com.glacier.spider.crawler.downloader;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by glacier on 14-12-12.
 */
public class Downloader {

    private static Logger logger = Logger.getLogger(Downloader.class.getName());

    public static Document document(String url, String encode) {
        Document document = null;
        try {
            if ( encode.equals("utf-8") || encode.equals("utf8") ) {
                document = Jsoup.connect(url)
                        .timeout(50000)
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36")
                        .get();
            }
            else if ( encode.equals("gbk") || encode.equals("GBK") ) {
                try {
                    URL link = new URL(url);
                    URLConnection connection = link.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ( (line = bufferedReader.readLine()) != null ) {
                        buffer.append(line + "\n");
                    }
                    document = Jsoup.parse(buffer.toString());
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
        return document;
    }

    public static Document newspaperDocument(String url, String encode) {
        Document document = null;
        try {
            document = document(getTrueLink(url, encode), encode);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return document;
    }

    private static String getTrueLink(String url, String encode) {
        String trueLink = null;
        try {
            Document document = document(url, encode);
            if ( document.toString().contains("location.replace") ) {
                String html = document.toString();
                url = html.substring(html.indexOf("(\"")+2, html.lastIndexOf("\""));
                return getTrueLink(url, encode);
            }
            if ( url.contains("paper.chinaso.com") ) {
                Element jumpEle = document.select("div[class=newspaper_con]").first();
                url = jumpEle.select("a[href]").attr("abs:href");
                return getTrueLink(url, encode);
            }
            Elements metaEles = document.select("meta[http-equiv=REFRESH]");
            if ( metaEles.size() == 0 ) {
                return url;
            }
            else {
                String content = metaEles.attr("content");
                int firstIndex = content.toUpperCase().indexOf("URL") + 4;
                String link = content.substring(firstIndex);
                if ( link.indexOf('\\') >= 0 )
                    link = link.replace('\\', '/');
                if ( link.contains("http://") )
                    return link;
                String sub = url.substring(url.lastIndexOf('/'));
                if ( sub.length() != 1 )
                    return getTrueLink(url.substring(0, url.lastIndexOf('/')+1) + link, encode );
                else
                    return getTrueLink(url + link, encode);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return trueLink;
    }
}
