package com.glacier.spider.crawler.pageprocessor;

import com.glacier.spider.crawler.downloader.Downloader;

import com.glacier.spider.crawler.pipeline.SearchAns;
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

    private List<SearchAns> searchAnses = new ArrayList<SearchAns>();
    private String search_key = "";

    /**
     * 依据关键字进行搜索, 维护一个存有SearchAns对象的List, SearchAns中包含一个UserInfo对象与一个WeiboStruct对象
     * @param key 需要检索的关键字
     * */
    public void getSearchList( String key ) {
        getSearchList(key, false, false);
    }

    /**
     * 依据关键字进行搜索, 维护一个存有SearchAns对象的List, SearchAns中包含一个UserInfo对象与一个WeiboStruct对象
     * @param key 需要检索的关键字
     * @param get_user 是否需要为条微博附带博主信息, true表示需要, false表示不需要
     * @param save 是否需要将检索到的结果保存起来, true表示需要, false表示不需要
     * */
    public void getSearchList(String key, boolean get_user, boolean save) {
        try {
            this.search_key = key;
            StringBuffer buffer = new StringBuffer();
            buffer.append("[搜索] 进入微博搜索模块. key = " + key);
            if ( get_user ) {   buffer.append(" [获取]用户资料"); }
            else {  buffer.append(" [不获取]用户资料");    }
            if ( save ) {   buffer.append(" [存储]至文件");  }
            else {  buffer.append(" [不存储]至文件"); }

            logger.info(buffer.toString());

            Document document = Downloader.searchDocument(key);
            get_search_list(document, get_user, save);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    /**
     * 获取微博搜索页面的微博集合
     * @param document 搜索页面的document文档树
     * @param get_user 是否需要为微博附带博主信息, true表示需要, false表示不需要
     * @param save 是否需要将检索到的结果保存起来, true表示需要, false表示不需要
     * */
    private void get_search_list(Document document, boolean get_user, boolean save) {
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
                    /**
                     * 暂时废弃，改为在document()方法中进行判断;
                     * */
//                    if ( document.title().equals("微博广场") ) {
//                        document = Downloader.reLogin();
//                    }
                    logger.info("[解析] 正在获取第 " + count + " 页 / " + maxPage + " 页");
                    Elements weiboDivs = document.select("div[class=c]").select("div[id]");
                    logger.info("[解析] 当前页解析得到 " + weiboDivs.size() + " 条微博");

                    if ( weiboDivs.size() == 0 ) {
                        System.out.println(document.toString());
                        System.exit(1);
                    }

                    for (Element weiboDiv : weiboDivs) {
                        SearchAns searchAns = new SearchAns(search_key);
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
                        searchAns.weiboStruct = struct;
                        if ( get_user == true ) {
                            String username = searchAns.weiboStruct.getWeiboSender();
                            Document user_doc = Downloader.userDocument(username);
                            if ( user_doc == null ) {
                                break;      //如果用户信息获取失败，抛弃该条微博
                            }
                            searchAns.userInfo = new UserPageProcessor().getUserInfo(user_doc);
                        }
                        searchAnses.add(searchAns);
                        //--------------------------------
                        if ( save == true ) {
                            searchAns.save4xml();
                        }
                        //--------------------------------
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

    /**
     * 返回维护的List<SearchAns> SearchAns内维护有一个WeiboStruct和UserInfo
     * @return 返回维护的List<SearchAns> SearchAns内维护有一个WeiboStruct和UserInfo
     * */
    public List<SearchAns> getSearchAnses() {
        return searchAnses;
    }

    /**
     * 对于搜索结果不能在一页进行展示的情况，利用该方法得到下一页的文档树
     * @param document 存在该情况的当前document文档树
     * @return 执行翻页操作后的document文档树
     * */
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
