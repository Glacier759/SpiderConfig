package com.glacier.spider.login;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.omg.CORBA.NameValuePair;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

/**
 * Created by glacier on 14-12-13.
 */
public class Login {

    private static Logger logger = Logger.getLogger(Login.class.getName());

    private DefaultHttpClient client = new DefaultHttpClient();
    private Integer servertime = null;
    private String nonce = null;
    private String pubkey = null;
    private String rsakv = null;

    public static void main(String[] args) {
        System.out.println("Hello Sina");
        Login login = new Login();
        //login.getLoginSidT();
        login.preLogin();
    }


    public void getLoginSidT() {
        try {
            HttpGet httpGet = new HttpGet("http://www.weibo.com");
            HttpResponse response = client.execute(httpGet);
            Header[] headers = response.getAllHeaders();

            for ( int i = 0; i < headers.length; i ++ ) {
                System.out.println("首页的预制信息--header[" + i + "]--" + headers[i]);
            }

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public void preLogin() {
        try {
            HttpGet httpGet = new HttpGet(
                    "http://login.sina.com.cn/sso/prelogin.php?entry=account&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.15)&_="
                            + (new Date()).getTime());
            HttpResponse response = client.execute(httpGet);
            String json = EntityUtils.toString(response.getEntity());
            System.out.println(json);

            json = json.substring(json.indexOf("(")+1, json.lastIndexOf(")"));
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);
            servertime = (Integer)jsonObject.get("servertime");
            nonce = (String)jsonObject.get("nonce");
            pubkey = (String)jsonObject.get("pubkey");
            rsakv = (String)jsonObject.get("rsakv");

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public void login() {
        try {

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }
}
