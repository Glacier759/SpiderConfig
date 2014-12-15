package main;

import com.glacier.spider.login.MyHttpConnectionManager;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import utils.sina.RegexPaserUtil;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
        //login.getPassword("Rlx0825leehom");
        //login.getUsername("l_ee_hom@msn.cn");
        login.login();
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

    public String getUsername(String username) {
        try {
            String encoded = Base64.encode(URLEncoder.encode(username, "UTF-8").getBytes());
            System.out.println(encoded);
            return encoded;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPassword(String password) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine jsEngine = manager.getEngineByName("javascript");
            String passwordJS = FileUtils.readFileToString(new File("password4Sina.js"));

            jsEngine.eval(passwordJS);
            Invocable invoke = (Invocable) jsEngine;

            String pass = (String) invoke.invokeFunction("getPassEncoding", new Object[] {
                    pubkey, servertime, nonce, password
            });

            System.out.println(pass);

            return pass;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void preLogin() {
        try {
            HttpGet httpGet = new HttpGet(
                    "http://login.sina.com.cn/sso/prelogin.php?entry=account&callback=sinaSSOController.preloginCallBack&su="+getUsername("l_ee_hom@msn.cn")+"&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.18)&_="
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

            client = MyHttpConnectionManager.getNewHttpClient();

            HttpPost httpPost = new HttpPost("http://login.sina.com.cn/sso/login.php?client=sologin.js(v1.4.18)");

            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.setHeader("Referer", "http://weibo.com/");
            httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpPost.setHeader("Cache-Control", "max-age=0");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Host", "login.sina.com.cn");
            httpPost.setHeader("Origin", "http://weibo.com");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("entry", "weibo"));
            nvps.add(new BasicNameValuePair("gateway", "1"));
            nvps.add(new BasicNameValuePair("from", ""));
            nvps.add(new BasicNameValuePair("savestate", "7"));
            nvps.add(new BasicNameValuePair("useticket", "1"));
            nvps.add(new BasicNameValuePair("pagerefer", ""));
            nvps.add(new BasicNameValuePair("vsnf", "1"));
            nvps.add(new BasicNameValuePair("su", getUsername("l_ee_hom@msn.cn")));     //base64加密
            nvps.add(new BasicNameValuePair("service", "miniblog"));
            nvps.add(new BasicNameValuePair("servertime", servertime.toString()));
            nvps.add(new BasicNameValuePair("nonce", nonce));
            nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
            nvps.add(new BasicNameValuePair("rsakv", rsakv));
            nvps.add(new BasicNameValuePair("sp", getPassword("Rlx0825leehom")));
            nvps.add(new BasicNameValuePair("sf", "1366*768"));
            nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
            nvps.add(new BasicNameValuePair("prelt", "106"));
            nvps.add(new BasicNameValuePair("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
            nvps.add(new BasicNameValuePair("returntype", "META"));

            UrlEncodedFormEntity params = new UrlEncodedFormEntity(nvps, "UTF-8");
            httpPost.setEntity(params);

            HttpResponse response = client.execute(httpPost);
            printHeaders(response.getAllHeaders());
            HttpEntity entity = response.getEntity();

            String content = getGZIPContent(entity);
            System.out.println("第一次取得的content---\n" + content);

            String location = getRedirectLocation(content);
            System.out.println("location url------" + location);

            String cookies = "";

            // 取得第一次时的cookie,最后与第二次获得的cookie去做与操作
            if (location.contains("retcode=0")) {
                String temp = "";
                int count = 1;
                org.apache.http.Header[] h = response.getAllHeaders();
                String SSOLoginState = "", cookies_sus;
                for (int i = 0; i < h.length; i++) {
                    if (h[i].getName().contains("Set-Cookie")) {
                        // System.out.println("第一次---header[" + i + "]------" +
                        // h[i]);
                        temp = h[i].getValue();
                        // 此处为要获取SSOLoginState,此值等LT的值
                        if (temp.contains("LT=")) {
                            String begin = "LT=";
                            String end = ";";

                            RegexPaserUtil regexPaserUtil = new RegexPaserUtil(
                                    begin, end, RegexPaserUtil.TEXTEGEXANDNRT);
                            regexPaserUtil.reset(temp);

                            SSOLoginState = regexPaserUtil.getText();
                        } else if (temp.contains("SUS=")) {
                            // 处理SUS值
                            String begin = "SUS=";
                            String end = ";";

                            RegexPaserUtil regexPaserUtil = new RegexPaserUtil(
                                    begin, end, RegexPaserUtil.TEXTEGEXANDNRT);
                            regexPaserUtil.reset(temp);

                            cookies_sus = regexPaserUtil.getText();
                        }
                        // cookie是键值对，此处的";"一定要加上
                        if (count == 1) {
                            cookies = cookies + temp;
                        } else {
                            cookies = cookies + ";" + temp;
                        }
                        count++;
                    }
                }
                cookies = cookies.replace("path=/;", "").replace("Httponly", "")
                        .replaceAll("domain=.sina.com.cn;", "").replace("httponly",
                                "").replace("path=/", "").replace(
                                "domain=.sina.com.cn", "").replaceAll(
                                "expires.*?;", "").replace(
                                "domain=login.sina.com.cn", "").replace(" ", "")
                        + ";wvr=5; un=" + "l_ee_hom@msn.cn";
                //cookies = cookies + ";myuid=" + loginPojo.getUid();// 添加的myUid是为解决验证码
                //cookies = cookies
                //        + ";SinaRot_wb_r_topic=39;UV5PAGE=usr513_90; UV5=usr319_182;";// 添加的SinaRot_wb_r_topic为解决话题内容content的抓取
                cookies = cookies + ";SSOLoginState=" + SSOLoginState;
                //cookies = cookies + ";" + login_sid_t;
                //cookies = cookies + ";" + UUG;

                System.out.println("cookies - " + cookies);
            }

            String ajaxlogin = "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&sudaref=weibo.com";
            HttpGet httpGet = new HttpGet(ajaxlogin);
            httpGet.setHeader("Cookie", cookies);

            MyHttpConnectionManager.setHandleRedirect(client, false);
            response = client.execute(httpGet);
            printHeaders(response.getAllHeaders());
            MyHttpConnectionManager.setHandleRedirect(client, true);


            Header[] headers = response.getAllHeaders();
            for ( Header header:headers ) {
                if ( header.getName().contains("Set-Cookie") ) {
                    String temp = header.getValue();

                    System.out.println(temp);

                    if ( temp.contains("YF-Ugrow-G0=") ) {
                        String begin = "YF-Ugrow-G0=";
                        String end = ";";

                        RegexPaserUtil regexPaserUtil = new RegexPaserUtil(begin, end, RegexPaserUtil.TEXTEGEXANDNRT);
                        regexPaserUtil.reset(temp);

                        String ugrow = regexPaserUtil.getText();
                        System.out.println(ugrow);
                        cookies += ";YF-Ugrow-G0=" + ugrow;
                    }
                }
            }
            System.out.println("cookie --- " + cookies);

            entity = response.getEntity();
            content = getContent(entity, "GBK");
            System.out.println("第二次取得的content---\n" + content);

            String home = "http://weibo.com/?wrv=5&lf=reg";
            httpGet = new HttpGet(home);
            httpGet.setHeader("Cookie", cookies);
            //MyHttpConnectionManager.setHandleRedirect(client, false);
            response = client.execute(httpGet);
            //MyHttpConnectionManager.setHandleRedirect(client, true);
            System.out.println("http status --- " + response.getStatusLine());
            printHeaders(response.getAllHeaders());
            entity = response.getEntity();
            content = getContent(entity, "GBK");
            System.out.println("第三次取得的content---\n" + content);




            String myHome = "http://weibo.com/u/2314283235/home?wvr=5&lf=reg";
            httpGet = new HttpGet(myHome);
            httpGet.setHeader("Cookie", cookies);
            response = client.execute(httpGet);
            System.out.println("http status --- " + response.getStatusLine());
            printHeaders(response.getAllHeaders());
            entity = response.getEntity();
            content = getContent(entity, "UTF-8");
            System.out.println("第四次取得的content---\n" + content);

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private String getRedirectLocation(String content) {
        String regex = "location\\.replace\\([',\"](.*?)[',\"]\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        String location = null;
        if ( matcher.find() ) {
            location = matcher.group(1);
        }
        return location;
    }

    private String getGZIPContent(HttpEntity entity) {
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(entity.getContent()), "GBK"));
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

    private String getContent(HttpEntity entity, String encode) {
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

    private void printHeaders(Header[] headers) {
        for ( int i = 0; i < headers.length; i ++ ) {
            System.out.println("header[" + i + "] -- " + headers[i]);
        }
    }

    private String getCookie(Header[] headers, String cookies) {
        for ( Header header:headers ) {
            if ( header.getName().contains("Set-Cookie") ) {
                String temp = header.getValue();
                String[] tempArry = temp.split(";");

                cookies += ";" + tempArry[0];
            }
        }
        cookies = cookies.substring(1);
        return cookies;
    }
}
