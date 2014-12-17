package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.crawler.downloader.Downloader;
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

/**
 * Created by glacier on 14-12-17.
 */
public class UserPageProcessor {

    private static Logger logger = Logger.getLogger(UserPageProcessor.class.getName());

    public void getFansMap( Document document ) {
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
                    System.out.println(fansName + "\t" + fansLink);
                }
            }while ((document = next(document)) != null);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public HashMap<String,String> getFollowMap( Document document ) {
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
                    System.out.println(followName + "\t" + followLink);
                }
            }while ((document = next(document)) != null);
            return followMap;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    public void getUserInfo( Document document ) {
        try {
            logger.info("[解析] 正在获取用户资料...");
            String headImage, weiboLevel, tag="";
            boolean isVip;
            Elements divClassCs = document.select("div[class=c]");
            for ( Element divClassC:divClassCs ) {
                if ( divClassC.html().contains("alt=\"头像\"") ) {
                    headImage = divClassC.select("img").attr("abs:src");
                    System.out.println("头像 = " + headImage);
                }
                else if ( divClassC.html().contains("微博等级") && divClassC.html().contains("/urank") ) {
                    Elements levelEles = divClassC.select("a[href]");
                    for ( Element levelEle:levelEles ) {
                        if ( levelEle.attr("abs:href").contains("urank") ) {
                            weiboLevel = levelEle.text();
                            System.out.println("微博等级 = " + weiboLevel);
                        }
                    }
                    if ( divClassC.html().contains("会员等级：未开通") )
                        isVip = false;
                    else
                        isVip = true;
                    System.out.println("isVip = " + isVip);
                }
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
                                    System.out.println("标签 = " + tag);
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
                            System.out.println("标签 ~ " + tag);
                        }
                        divHTML = divHTML.substring(0, divHTML.indexOf("标签:"));
                    }
                    System.out.println(divHTML);
                }
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public void getWeibo( Document document ) {
        try {
            logger.info("[解析] 正在获取用户微博...");
            List<WeiboStruct> weiboList = new ArrayList<WeiboStruct>();

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
                                if (eleText.contains("原图")) {
                                    imageURL = imageEle.attr("href");
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
                    weiboDate = fromDateText.substring(0, fromDateText.indexOf("来自"));
                    weiboFrom = fromDateText.substring(fromDateText.indexOf("来自"));
                    struct.setWeiboDate(weiboDate);
                    struct.setWeiboFrom(weiboFrom);

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
                    System.out.println(struct.getWeiboText());
                    weiboList.add(struct);
                }
                count ++;   //当前页标
            }while( (document = next(document)) != null );
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
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

    public HashMap<String,String> getURL( Document document ) {
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
