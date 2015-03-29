package com.glacier.spider.crawler.pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA on 2015-03-29 20:21.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class SearchAns{

    public static Logger logger = Logger.getLogger(SearchAns.class.getName());

    public UserInfo userInfo = null;
    public WeiboStruct weiboStruct = null;

    public void save4xml() {
        try {
            org.dom4j.Document document = DocumentHelper.createDocument();
            org.dom4j.Element root = document.addElement("root");

            if ( userInfo != null ) {
                org.dom4j.Element userEle = root.addElement("user_info");
                if ( userInfo.user_id != null ) {    userEle.addAttribute("user_id", userInfo.user_id);  }
                if ( userInfo.user_url != null ) {   userEle.addAttribute("user_url", userInfo.user_url);    }
                if ( userInfo.weibo_count != null ) {    userEle.addElement("weibo_count").addText(userInfo.weibo_count); }
                if ( userInfo.follow_count != null ) {   userEle.addElement("follow_count").addText(userInfo.follow_count);   }
                if ( userInfo.fans_count != null ) { userEle.addElement("fans_count").addText(userInfo.fans_count);   }
                if ( userInfo.basicInfo.size() != 0 ) {
                    for ( String key : userInfo.basicInfo.keySet() ) {
                        org.dom4j.Element key_ele = userEle.addElement(key);
                        key_ele.addText(userInfo.basicInfo.get(key));
                    }
                }
            }
            if ( weiboStruct != null ) {
                org.dom4j.Element weibo_ele = root.addElement("weibo");
                weibo_ele.addAttribute("weibo_id", weiboStruct.getWeiboID());
                weibo_ele.addElement("weibo_sender").addText(weiboStruct.getWeiboSender());
                weibo_ele.addElement("weibo_content").addText(weiboStruct.getWeiboText());
                if (!weiboStruct.getWeiboForward().equals("") && !weiboStruct.getForwardReason().equals("")) {
                    weibo_ele.addElement("weibo_forward").addText(weiboStruct.getWeiboForward()).addAttribute("forward_reason", weiboStruct.getForwardReason());
                }
                if (!weiboStruct.getWeiboImage().equals("")) {
                    weibo_ele.addElement("weibo_image").addText(weiboStruct.getWeiboImage());
                }
                weibo_ele.addElement("weibo_count").addAttribute("like_count", weiboStruct.getWeiboLikeCount())
                        .addAttribute("forward_count", weiboStruct.getWeiboForwardCount())
                        .addAttribute("comment_count", weiboStruct.getWeiboCommentCount());
                weibo_ele.addElement("weibo_date").addText(weiboStruct.getWeiboDate());
                weibo_ele.addElement("weibo_from").addText(weiboStruct.getWeiboFrom());
            }

            addFileInfo(root);  //在XML文件中加入签名信息(待订制)

            File save_file = new File(System.currentTimeMillis() + ".xml");
            FileUtils.writeStringToFile(save_file, formatXML(root));
            logger.info("[保存成功] - " + save_file.getAbsolutePath());

        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if ( userInfo != null ) {
            buffer.append(userInfo.toString());
        }
        buffer.append("--------------------------\n");
        if ( weiboStruct != null ) {
            buffer.append(weiboStruct.toString());
        }
        return buffer.toString();
    }

    /**
     * 将xml文件格式化
     * @param root 传入xml文档树根节点
     * @return 格式化后的xml文件
     * */
    private String formatXML(org.dom4j.Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            org.dom4j.Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.debug(baos.toString());
        }
        return formatXMLStr;
    }

    /**
     * 添加所有文件的签名信息, 待订制
     * @param root 需要修改的xml文档树根节点
     * */
    public static void addFileInfo( Element root ) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String crawl_date = format.format(new Date());
            root.addAttribute("crawl_date", crawl_date);
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.debug(baos.toString());
        }
    }
}