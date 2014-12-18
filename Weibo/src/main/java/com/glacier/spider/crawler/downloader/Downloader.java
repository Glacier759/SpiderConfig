package com.glacier.spider.crawler.downloader;

import javafx.geometry.VPos;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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

                httpGet.setHeader("Host", "weibo.cn");
                httpGet.setHeader("Connection", "keep-alive");
                httpGet.setHeader("Cache-Control", "max-age=0");
                httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
                httpGet.setHeader("Referer", "http://weibo.cn/");
                httpGet.setHeader("Accept-Language", "zh-CN,zh,q=0.8");
                httpGet.setHeader("RA-Ver", "2.8.5");
                httpGet.setHeader("RA-Sid", "RA-Sid: 6BB2C802-20140627-183131-586b2e-69394f");

                httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                httpGet.getParams().setParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 60000);
                httpGet.getParams().setBooleanParameter("http.tcp.nodelay", true);
                httpGet.getParams().setParameter("http.connection.stalecheck", false);
                httpGet.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
                response = httpClient.execute(httpGet);
            }
            else if ( method.equals("post") ) {
                HttpPost httpPost = new HttpPost(url);

                httpPost.setHeader("Host", "weibo.cn");
                httpPost.setHeader("Connection", "keep-alive");
                httpPost.setHeader("Cache-Control", "max-age=0");
                httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
                httpPost.setHeader("Referer", "http://weibo.cn/");
                httpPost.setHeader("Accept-Language", "zh-CN,zh,q=0.8");
                httpPost.setHeader("RA-Ver", "2.8.5");
                httpPost.setHeader("RA-Sid", "RA-Sid: 6BB2C802-20140627-183131-586b2e-69394f");
                
                httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
                httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                httpPost.getParams().setParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE, 60000);
                httpPost.getParams().setBooleanParameter("http.tcp.nodelay", true);
                httpPost.getParams().setParameter("http.connection.stalecheck", false);
                httpPost.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
                response = httpClient.execute(httpPost);
            }
            HttpEntity entity = response.getEntity();
            Document document = Jsoup.parse(getContent(entity, "UTF-8"));
            document.setBaseUri(url);

            return document;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    public static Document searchDocument(String key) {
        try {
            String search = "http://weibo.cn/search/mblog?hideSearchFrame=&keyword=" + key + "&page=1";
            return document(search, Downloader.HTTP_GET);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    private static String getContent(HttpEntity entity, String encode) {
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), encode));
            buffer = new StringBuffer();
            String temp = null;
            while( (temp = reader.readLine()) != null ) {
                buffer.append(temp);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
