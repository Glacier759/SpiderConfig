package com.glacier.spider.crawler.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by glacier on 14-12-17.
 */
public class Downloader {

    private static Logger logger = Logger.getLogger(Downloader.class.getName());
    private static DefaultHttpClient httpClient;
    public static String HTTP_GET = "get";
    public static String HTTP_POST = "post";

    public static void setClient(DefaultHttpClient client) {
        httpClient = client;
    }

    public static Document document(String url, String method) {
        try {
            HttpResponse response = null;
            if ( method.equals("get") ) {
                HttpGet httpGet = new HttpGet(url);
                response = httpClient.execute(httpGet);
            }
            else if ( method.equals("post") ) {
                HttpPost httpPost = new HttpPost(url);
                response = httpClient.execute(httpPost);
            }
            HttpEntity entity = response.getEntity();
            Document document = Jsoup.parse(EntityUtils.toString(entity));
            document.setBaseUri(url);
            return document;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }
}
