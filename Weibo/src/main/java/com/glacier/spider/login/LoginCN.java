package com.glacier.spider.login;

import com.glacier.spider.crawler.pipeline.Accounts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-12-17.
 */
public class LoginCN {

    private String loginURL = "http://login.weibo.cn/login/";

    private static Logger logger = Logger.getLogger(LoginCN.class.getName());

    public DefaultHttpClient login(Accounts accounts) {
        return login(accounts.getUsername(), accounts.getPassword());
    }

    public DefaultHttpClient login(String username, String password) {
        try {
            logger.info("[Login] '" + username + "' 正在登陆...");
            Document document = Jsoup.connect(loginURL)
                    .userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();

            String loginBackURL = document.select("input[name=backURL]").attr("value");
            String loginBackTitle = document.select("input[name=backTitle]").attr("value");
            String loginVK = document.select("input[name=vk]").attr("value");
            String loginSubmit = document.select("input[name=submit]").attr("value");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("mobile", username));
            nvps.add(new BasicNameValuePair("password_" + loginVK.substring(0, 4), password));
            nvps.add(new BasicNameValuePair("remember", "on"));
            nvps.add(new BasicNameValuePair("backURL", loginBackURL));
            nvps.add(new BasicNameValuePair("backTitle", loginBackTitle));
            nvps.add(new BasicNameValuePair("tryCount", ""));
            nvps.add(new BasicNameValuePair("vk", loginVK));
            nvps.add(new BasicNameValuePair("submit", loginSubmit));

            HttpPost httpPost = new HttpPost(loginURL);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            DefaultHttpClient httpClient =MyHttpConnectionManager.getNewHttpClient();
            HttpResponse response = httpClient.execute(httpPost);

            String locationURL = null;
            while( response.getFirstHeader("Location") != null) {
                locationURL = response.getFirstHeader("Location").getValue();
                logger.debug("[login] " + response.getStatusLine() + "\t" + locationURL);
                HttpGet httpGet = new HttpGet(locationURL);
                MyHttpConnectionManager.setHandleRedirect(httpClient, false);
                response = httpClient.execute(httpGet);
                MyHttpConnectionManager.setHandleRedirect(httpClient, true);
            }

            HttpGet httpGet = new HttpGet(locationURL);
            MyHttpConnectionManager.setHandleRedirect(httpClient, false);
            response = httpClient.execute(httpGet);
            MyHttpConnectionManager.setHandleRedirect(httpClient, true);
            EntityUtils.toString(response.getEntity());
            logger.info("[login] '" + username + "' 登陆成功!");

            return httpClient;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }
}
