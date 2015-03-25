package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.crawler.downloader.Downloader;
import com.glacier.spider.crawler.pipeline.UserInfo;
import com.glacier.spider.crawler.pipeline.UserStruct;
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
    class SearchAns{
        UserInfo userInfo = null;
        WeiboStruct weiboStruct = null;
    }
    private List<SearchAns> searchAnses = new ArrayList<SearchAns>();

    public void getSearchList( String key ) {
        try {
            Document document = Downloader.searchDocument(key);
            get_search_list(document);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public void get_user_info() {
        try {
            logger.info("[获取] 正在获取微博对应用户信息...");

            for ( SearchAns searchAns : searchAnses ) {
                String username = searchAns.weiboStruct.getWeiboSender();
                Document user_doc = Downloader.userDocument(username);
                searchAns.userInfo = new UserPageProcessor().getUserInfo(user_doc);
            }
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private void get_search_list(Document document ) {
        try {
            logger.info("[解析] 正在获取搜索结果微博...");

            Element maxPageEle = document.select("input[type=hidden]").first();
            String maxPage;
            if ( maxPageEle != null )
                maxPage = maxPageEle.attr("value");
            else
                maxPage = "1";

            int count = 1;
            do {
                try {
                    logger.info("[解析] 正在获取第 " + count + " 页 / " + maxPage + " 页");
                    Elements weiboDivs = document.select("div[class=c]").select("div[id]");
                    logger.info("[解析] 当前页解析得到 " + weiboDivs.size() + " 条微博");
                    for (Element weiboDiv : weiboDivs) {
                        SearchAns searchAns = new SearchAns();
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
                        System.out.println(struct);
                        searchAns.weiboStruct = struct;
                        searchAnses.add(searchAns);
                    }
                    count++;   //当前页标
                }catch (Exception e) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(baos));
                    logger.error(baos.toString());
                }
            }while((document = next(document)) != null);
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
}
