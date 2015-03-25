package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pipeline.UserStruct;
import com.glacier.spider.crawler.pipeline.WeiboStruct;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by glacier on 14-12-17.
 */
public class UserPageProcessor {

    private static Logger logger = Logger.getLogger(UserPageProcessor.class.getName());
    private UserStruct userStruct = new UserStruct();

    public void getFansMap( Document document ) {
        Document fans_document = Downloader.document(this.getURL(document).get("粉丝"), Downloader.HTTP_GET);
        get_fans_map(fans_document);
    }

    public void getFollowMap( Document document ) {
        Document follow_document = Downloader.document(this.getURL(document).get("关注"), Downloader.HTTP_GET);
        get_follow_map(follow_document);
    }

    public void getUserInfo( Document document ) {

        Element element = document.select("div[class=tip2]").first();

        Pattern pattern = Pattern.compile("微博\\[(\\d+)\\]");
        Matcher matcher = pattern.matcher(element.toString());
        if ( matcher.find() ) { userStruct.weibo_count = matcher.group(1);  }

        pattern = Pattern.compile("关注\\[(\\d+)\\]");
        matcher = pattern.matcher(element.toString());
        if ( matcher.find() ) { userStruct.follow_count = matcher.group(1); }

        pattern = Pattern.compile("粉丝\\[(\\d+)\\]");
        matcher = pattern.matcher(element.toString());
        if ( matcher.find() ) { userStruct.fans_count = matcher.group(1);   }

        userStruct.user_url = document.baseUri();
        //Document user_document = Downloader.document(this.getURL(document).get("资料"), Downloader.HTTP_GET);
        //get_user_info(user_document);
    }

    public UserStruct getUserStruct() {
        return userStruct;
    }

    private HashMap<String,String> get_fans_map( Document document ) {
        try {
            logger.info("[解析] 正在获取粉丝列表...");

            HashMap<String,String> fansMap = new HashMap<String, String>();
            do {
                Elements fansOuter = document.select("table");
                for (Element fansTable : fansOuter) {
                    Element fansTD = fansTable.select("td[valign=top]").last();
                    Element fansA = fansTD.select("a[href]").first();
                    String fansName = fansA.text();
                    String fansLink = fansA.attr("abs:href");
                    fansMap.put(fansName, fansLink);
                    //System.out.println(fansName + "\t" + fansLink);
                }
            }while ((document = next(document)) != null);
            userStruct.fansMap = fansMap;
            return fansMap;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    private HashMap<String,String> get_follow_map( Document document ) {
        try {
            logger.info("[解析] 正在获取关注列表...");

            HashMap<String,String> followMap = new HashMap<String, String>();
            do {
                Elements followOuter = document.select("table");
                for (Element followTable : followOuter) {
                    Element followTD = followTable.select("td[valign=top]").last();
                    Element followA = followTD.select("a[href]").first();
                    String followName = followA.text();
                    String followLink = followA.attr("abs:href");
                    followMap.put(followName, followLink);
                    //System.out.println(followName + "\t" + followLink);
                }
            }while ((document = next(document)) != null);
            //weiboStruct.setFollowMap(followMap);
            userStruct.followMap = followMap;
            return followMap;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    private void get_user_info( Document document ) {
        try {
            logger.info("[解析] 正在获取用户资料...");
            String userID = document.baseUri().replace("http://weibo.cn/","").replace("/info","");
            userStruct.user_id = userID;
            String headImage, weiboLevel, tag="";
            boolean isVip;
            Elements divClassCs = document.select("div[class=c]");
            for ( Element divClassC:divClassCs ) {
                if ( divClassC.html().contains("alt=\"头像\"") ) {
                    headImage = divClassC.select("img").attr("abs:src");
                    //System.out.println("头像 = " + headImage);
                    userStruct.basicInfo.put("head_image", headImage);
                }
                else if ( divClassC.html().contains("微博等级") && divClassC.html().contains("/urank") ) {
                    Elements levelEles = divClassC.select("a[href]");
                    for ( Element levelEle:levelEles ) {
                        if ( levelEle.attr("abs:href").contains("urank") ) {
                            weiboLevel = levelEle.text();
                            //System.out.println("微博等级 = " + weiboLevel);
                            userStruct.basicInfo.put("weibo_level", weiboLevel);
                        }
                    }
                    if ( divClassC.html().contains("会员等级：未开通") )
                        isVip = false;
                    else
                        isVip = true;
                    //System.out.println("isVip = " + isVip);
                    if ( isVip )
                        userStruct.basicInfo.put("is_vip", "true");
                    else
                        userStruct.basicInfo.put("is_vip", "false");
                }//微博等级、是否会员完
                else if ( divClassC.html().contains("昵称:") && divClassC.html().contains("性别:") ) {
                    String divHTML = divClassC.html();
                    if ( divHTML.contains("<br />") ) {
                        divHTML = divHTML.replaceAll("<br />", "");
                    }
                    if ( divHTML.contains("标签:") ) {
                        Elements aTagEles = divClassC.select("a[href]");
                        if ( aTagEles.html().contains("更多") ) {
                            Element tagEle = aTagEles.last();
                            Document tagDoc = Downloader.document(tagEle.attr("abs:href"), Downloader.HTTP_GET);
                            Elements tagEles = tagDoc.select("div[class=c]");
                            for ( Element tempEle:tagEles ) {
                                if ( tempEle.html().contains("的标签:") ) {
                                    tagEles = tempEle.select("a[href]");
                                    for ( Element element:tagEles ) {
                                        tag += "," + element.text();
                                    }
                                    tag = tag.substring(1);
                                    //System.out.println("标签 = " + tag);
                                    userStruct.basicInfo.put("tag", tag);
                                    break;
                                }
                            }
                        } //如果有更多的标签
                        else {
                            Elements tagEles = divClassCs.select("a[href]");
                            for ( Element tagEle:tagEles ) {
                                tag += "," + tagEle.text();
                            }
                            tag = tag.substring(1);
                            //System.out.println("标签 ~ " + tag);
                            userStruct.basicInfo.put("tag", tag);
                        }
                        divHTML = divHTML.substring(0, divHTML.indexOf("标签:"));
                    }
                    String[] infoArry = divHTML.split("\n");
                    for ( String info:infoArry ) {
                        try {
                            if (info.length() > 0) {
                                String key = info.substring(0, info.indexOf(':'));
                                String value = info.substring(info.indexOf(':') + 1);
                                //System.out.println(key + " = " + value);
                                userStruct.basicInfo.put(key, value);
                            }
                        }catch (Exception e) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            e.printStackTrace(new PrintStream(baos));
                            logger.error(baos.toString());
                        }
                    }
                }//基本信息完
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public List<WeiboStruct> getWeibo( Document document ) {
        try {
            logger.info("[解析] 正在获取用户微博...");

            Element maxPageEle = document.select("input[type=hidden]").first();
            String maxPage;
            if ( maxPageEle != null )
                maxPage = maxPageEle.attr("value");
            else
                maxPage = "1";

            int count = 1;

            do {
                logger.info("[解析] 正在获取第 " + count + " 页 / " + maxPage + " 页");
                Elements weiboDivs = document.select("div[class=c]").select("div[id]");
                logger.info("[解析] 当前页解析得到 " + weiboDivs.size() + " 条微博");
                for (Element weiboDiv : weiboDivs) {
                    WeiboStruct struct = new WeiboStruct();
                    struct.setWeiboID(weiboDiv.attr("id"));
                    Element weiboText = weiboDiv.select("span[class=ctt]").first();
                    if (weiboText == null)
                        continue;
                    struct.setWeiboText(weiboText.text());
                    struct.setSenderURL(document.baseUri());
                    struct.setWeiboSender(document.title().substring(0, document.title().lastIndexOf("的")));

                    String imageURL = "";
                    String likeCount = "", forwardCount = "", commentCount = "";
                    String weiboFrom = "", weiboDate;

                    Elements imageEles = weiboDiv.select("a[href]");
                    if (imageEles.size() > 0) {
                        for (Element imageEle : imageEles) {
                            try {
                                String eleText = imageEle.text();
                                if (eleText.contains("图片")) {
                                    imageURL = imageEle.attr("href");
                                    Document image_document = Downloader.document(imageURL, Downloader.HTTP_GET);
                                    imageURL = image_document.select("img[alt]").first().attr("src");
                                } else if (eleText.contains("赞")) {
                                    likeCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
                                } else if (eleText.contains("转发")) {
                                    forwardCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
                                } else if (eleText.contains("评论")) {
                                    commentCount = eleText.substring(eleText.indexOf('[') + 1, eleText.indexOf(']'));
                                }
                            } catch (StringIndexOutOfBoundsException e) {
                                logger.error("出现StringIndexOutOfBoundsException，可以弃之不理\tline: 317");
                            }
                        }
                        struct.setWeiboImage(imageURL);
                        struct.setWeiboLikeCount(likeCount);
                        struct.setWeiboForwardCount(forwardCount);
                        struct.setWeiboCommentCount(commentCount);
                    }

                    Element fromDate = weiboDiv.select("span[class=ct]").first();
                    String fromDateText = fromDate.text();
                    if ( fromDateText.contains("来自") ) {
                        weiboDate = fromDateText.substring(0, fromDateText.indexOf("来自"));
                        weiboFrom = fromDateText.substring(fromDateText.indexOf("来自"));
                        struct.setWeiboDate(weiboDate);
                        struct.setWeiboFrom(weiboFrom);
                    }
                    else {
                        struct.setWeiboDate(fromDateText);
                        struct.setWeiboFrom("来自火星");
                    }

                    Element weiboForward = weiboDiv.select("span[class=cmt]").first();
                    if (weiboForward != null) {
                        Element forwardReason = weiboDiv.select("span[class=cmt]").last().parent();
                        String forwardReasonText = forwardReason.text();
                        struct.setWeiboForward(weiboForward.text());
                        struct.setForwardReason(forwardReasonText.substring(0, forwardReasonText.indexOf("赞[")));
                    } else {
                        struct.setWeiboForward("");
                        struct.setForwardReason("");
                    }
                    //System.out.println(struct.toString());
                    userStruct.weiboList.add(struct);
                }
                count ++;   //当前页标
            }while( (document = next(document)) != null );
            return userStruct.weiboList;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    private Document next( Document document ) {
        try {
            Elements pageListTags = document.select("div[id=pagelist]").select("a");
            Element nextEle = pageListTags.first();
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

    private HashMap<String,String> getURL( Document document ) {
        try {
            HashMap<String,String> urlMap = new HashMap<String, String>();
            Elements tagEles = document.select("a[href]");
            for ( Element tagEle:tagEles ) {
                String tagText = tagEle.text();
                String tagHref = tagEle.attr("abs:href");

                if ( tagText.equals("资料") )
                    urlMap.put("资料", tagHref);
                else if ( tagText.contains("关注[") )
                    urlMap.put("关注", tagHref);
                else if ( tagText.contains("粉丝[") )
                    urlMap.put("粉丝", tagHref);
            }
            return urlMap;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

}
