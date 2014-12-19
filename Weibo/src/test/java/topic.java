import com.glacier.spider.login.GetAccounts;
import com.glacier.spider.login.LoginCN;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-12-19.
 */
public class topic {
    private List<String> TempList = null;
    private HttpResponse response = null;
    private static HttpClient httpclient = new DefaultHttpClient();

    public static void main(String[] agrs) {
        LoginCN loginCN = new LoginCN();
        httpclient = loginCN.login(GetAccounts.accounts("weibo"));

        topic top = new topic();
        try {
            top.getTopic(loginCN.getHomePage(), "王力宏");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getTopic( String NewLoginHTML, String Topic ) throws Exception {
        TempList = new ArrayList<String>();
        Elements ClassNATags = Jsoup.parse(NewLoginHTML).select("div[class=n]").select("a[href]");
        String SearchURL = "";
        for ( Element Tag:ClassNATags ) {
            if ( Tag.text().indexOf("搜索") >= 0 ) {
                SearchURL = Tag.attr("href");
                break;
            }
        }
        HttpGet httpget = new HttpGet(SearchURL);
        response = httpclient.execute(httpget);
        String SearchPage = EntityUtils.toString(response.getEntity());
        String SearchPostUrl = "http://weibo.cn" + Jsoup.parse(SearchPage).select("form").attr("action");
        System.out.println(SearchPostUrl);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("keyword", Topic));
        nvps.add(new BasicNameValuePair("smblog", "搜微博"));

        HttpPost httpost = new HttpPost(SearchPostUrl);
        httpost.setEntity(new UrlEncodedFormEntity( nvps, "UTF-8" ));
        response = httpclient.execute(httpost);
        String TopicHTML = EntityUtils.toString(response.getEntity());
        ExtractInfo( TopicHTML );
    }

    public void ExtractInfo( String TopicHTML ) throws Exception {
        Document TopicDoc = Jsoup.parse(TopicHTML);
        Elements TagSpans = TopicDoc.select("span[class=ctt]");
        for ( Element TagSpan:TagSpans ) {
            String UserName = TagSpan.firstElementSibling().text();
            String WeiboText = TagSpan.text();
            Element TagSpanParent = TagSpan.parent();
            String WeiboFrom = TagSpanParent.select("span[class=ct]").text();
            if ( WeiboFrom.length() == 0) {
                WeiboFrom = TagSpanParent.parent().select("span[class=ct]").text();
            }
            TempList.add(UserName+WeiboText+"\r\n"+WeiboFrom);
            System.out.println(UserName+WeiboText);
            System.out.println(WeiboFrom);
            System.out.println();
        }

        Element NextTagA= TopicDoc.getElementById("pagelist").select("a[href]").first();
        System.out.println(NextTagA.parent().text());
        if ( NextTagA.text().compareTo("下页") != 0 ) {
            return;
        }
        String NextPage = "http://weibo.cn" + NextTagA.attr("href");
        System.out.println("NextPage = " + NextPage);
        HttpGet httpget = new HttpGet(NextPage);
        response = httpclient.execute(httpget);
        TopicHTML = EntityUtils.toString(response.getEntity());
        ExtractInfo( TopicHTML );
    }

    public void saveTopic( String Topic ) throws Exception {
        for ( String TopicInfo:TempList ) {
            FileUtils.writeStringToFile(new File("Topic-" + Topic), TopicInfo + "\r\n\r\n", "UTF-8", true);
        }
    }
}

