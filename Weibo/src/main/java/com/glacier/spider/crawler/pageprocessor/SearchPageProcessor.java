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
import java.util.List;

/**
 * Created by glacier on 14-12-18.
 */
public class SearchPageProcessor {

    private static Logger logger = Logger.getLogger(SearchPageProcessor.class.getName());

    public List<WeiboStruct> getSearchList(Document document) {
        try {
            List<WeiboStruct> searchList = new ArrayList<WeiboStruct>();
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
                    struct.setWeiboSender(weiboDiv.select("a[class=nk]").text());

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
                    if (fromDateText.contains("来自")) {
                        weiboDate = fromDateText.substring(0, fromDateText.indexOf("来自"));
                        weiboFrom = fromDateText.substring(fromDateText.indexOf("来自"));
                        struct.setWeiboDate(weiboDate);
                        struct.setWeiboFrom(weiboFrom);
                    } else {
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
                    searchList.add(struct);
                    System.out.println(struct.getWeiboSender() + " : " + struct.getWeiboText() + "\t" + struct.getWeiboFrom() + struct.getWeiboDate());
                }
                count ++;   //当前页标
            }while((document = next(document)) != null);
            return searchList;
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
}
