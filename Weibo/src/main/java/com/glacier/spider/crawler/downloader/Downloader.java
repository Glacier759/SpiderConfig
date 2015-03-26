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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-12-17.
 * @author Glacier<OurHom.759@gmail.com>
 */
public class Downloader {

    private static Logger logger = Logger.getLogger(Downloader.class.getName());
    private static DefaultHttpClient httpClient;
    public static String HTTP_GET = "get";
    public static String HTTP_POST = "post";

    /**
     * 设置Downloader模块所需的HttpClient
     * @param client 经过登陆操作返回的HttpClient
     * */
    public static void setClient(DefaultHttpClient client) {
        httpClient = client;
    }

    /**
     * 对相应地址使用相应方法返回抓取得到的dom树
     * @param url 需要获取的地址
     * @param method 访问该地址需要使用的HTTP请求方法
     * @return 返回获取得到的Document文档树
     * */
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
                httpGet.setHeader("RA-Sid", "6BB2C802-20140627-183131-586b2e-69394f");

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
                httpPost.setHeader("RA-Sid", "6BB2C802-20140627-183131-586b2e-69394f");
                
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
            document.setBaseUri(url);   //设置document的来源地址

            return document;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    /**
     * 使用微博搜索功能检索关键字，返回的文档树检索首页，包含有所有包含该关键字的相关微博
     * @param key 需要检索的关键字
     * @return 经过微博搜索该关键字得到的document文档树
     * */
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

    /**
     * 使用微博搜索功能检索用户，返回用户的个人首页文档树，用户名与关键字严格匹配
     * @param user 需要检索的用户昵称
     * @return 检索用户的个人首页document文档树
     * */
    public static Document userDocument(String user) {
        try {
            String search = "http://weibo.cn/search/user/?keyword=" + user + "&page=1";
            Document search_doc = document(search, Downloader.HTTP_GET);
            do {
                try {
                    Elements elements = search_doc.select("table");     //遍历检索的到的所有带有相关关键字的用户
                    for (Element element : elements) {
                        String username = element.select("td[valign=top]").last().select("a[href]").first().text();     //获取用户昵称
                        if (username.equals(user)) {                                                                    //匹配用户昵称，要求完全一致
                            String user_link = element.select("td[valign=top]").first().select("a[href]").attr("abs:href");     //获得匹配成功用户的首页地址
                            return Downloader.document(user_link, Downloader.HTTP_GET);                                         //返回用户首页document文档树
                        }
                    }
                }catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    logger.error(baos.toString());
                }
            }while ( (search_doc = next(search_doc)) != null );
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    /**
     * 使用微博搜索功能检索话题关键字，返回参与当前话题讨论的微博检索首页文档树
     * @param topic 需要检索的话题
     * @return 检索得到的参与该话题的微博搜索首页document文档树
     * */
    public static Document topicDocument(String topic) {
//        try {
//            HttpGet httpGet = new HttpGet("http://m.weibo.cn/p/index?containerid=100808558dc0694893f9249cbbf30b3c821934");
//
//            httpGet.setHeader("Host", "huati.weibo.cn");
//            httpGet.setHeader("Connection", "keep-alive");
//            httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//            httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
//            httpGet.setHeader("Accept-Language", "zh-CN,zh,q=0.8");
//            httpGet.setHeader("RA-Ver", "2.8.6");
//            httpGet.setHeader("RA-Sid", "6BB2C802-20140627-183131-586b2e-69394f");
//
//            HttpResponse response = httpClient.execute(httpGet);
//            System.out.println(response.getStatusLine());
//            String html = EntityUtils.toString(response.getEntity());
//            System.out.println(html);
//
//        }catch (Exception e) {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            e.printStackTrace(new PrintStream(baos));
//            logger.error(baos.toString());
//        }
        return null;
    }

    /**
     * 为保证文档树不产生乱码情况
     * @param entity HTTP请求后得到的HttpEntity
     * @param encode 需要指定的最终文字编码
     * @return 返回按照指定编码转码后的页面源码
     * */
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

    /**
     * 对于搜索结果不能在一页进行展示的情况，利用该方法得到下一页的文档树
     * @param document 存在该情况的当前document文档树
     * @return 执行翻页操作后的document文档树
     * */
    private static Document next( Document document ) {
        try {
            Elements pageListTags = document.select("div[id=pagelist]").select("a");
            Element nextEle = pageListTags.first();
            if ( pageListTags == null || nextEle == null ) {
                return null;
            }
            if ( nextEle.text().equals("下页") ) {
                return Downloader.document(nextEle.attr("abs:href"), Downloader.HTTP_GET);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }
}
