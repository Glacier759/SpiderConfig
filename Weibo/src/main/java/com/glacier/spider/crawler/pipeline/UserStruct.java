package com.glacier.spider.crawler.pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by glacier on 14-12-18.
 */
public class UserStruct {

    private Logger logger = Logger.getLogger(UserStruct.class.getName());

    public List<WeiboStruct> weiboList = new ArrayList<WeiboStruct>();
    public HashMap<String,String> basicInfo = new HashMap<String,String>();
    public HashMap<String,String> fansMap, followMap;
    public String user_url = null, user_id = null;
    public String weibo_count = null, follow_count = null, fans_count = null;

    public void save4xml() {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("root");

            Element userEle = root.addElement("user_info");
            if ( user_id != null ) {    userEle.addAttribute("user_id", this.user_id);  }
            if ( user_url != null ) {   userEle.addAttribute("user_url", this.user_url);    }
            if ( weibo_count != null ) {    userEle.addElement("weibo_count").addText(weibo_count); }
            if ( follow_count != null ) {   userEle.addElement("follow_count").addText(follow_count);   }
            if ( fans_count != null ) { userEle.addElement("fans_count").addText(fans_count);   }
            if ( basicInfo.size() != 0 ) {
                for ( String key : basicInfo.keySet() ) {
                    Element key_ele = userEle.addElement(key);
                    key_ele.addText(basicInfo.get(key));
                }
            }

            if ( weiboList.size() != 0 ) {
                Element weiboEle = root.addElement("weibo_list");
                for ( WeiboStruct key : weiboList ) {
                    Element weibo_ele = weiboEle.addElement("weibo");
                    weibo_ele.addAttribute("weibo_id", key.getWeiboID());
                    weibo_ele.addElement("weibo_sender").addText(key.getWeiboSender());
                    weibo_ele.addElement("weibo_content").addText(key.getWeiboText());
                    if (!key.getWeiboForward().equals("") && !key.getForwardReason().equals("")) {
                        weibo_ele.addElement("weibo_forward").addText(key.getWeiboForward()).addAttribute("forward_reason", key.getForwardReason());
                    }
                    if (!key.getWeiboImage().equals("")) {
                        weibo_ele.addElement("weibo_image").addText(key.getWeiboImage());
                    }
                    weibo_ele.addElement("weibo_count").addAttribute("like_count", key.getWeiboLikeCount())
                            .addAttribute("forward_count", key.getWeiboForwardCount())
                            .addAttribute("comment_count", key.getWeiboCommentCount());
                    weibo_ele.addElement("weibo_date").addText(key.getWeiboDate());
                    weibo_ele.addElement("weibo_from").addText(key.getWeiboFrom());
                }
            }

            if ( fansMap.size() != 0 ) {
                Element fansEle = root.addElement("fans_list").addAttribute("total_count", fansMap.size()+"");
                for ( String fans_name : fansMap.keySet() ) {
                    fansEle.addElement("weibo_fans").addAttribute("fans_name", fans_name)
                                                    .addAttribute("fans_link", fansMap.get(fans_name));
                }
            }

            if ( followMap.size() != 0 ) {
                Element followEle = root.addElement("follow_list").addAttribute("total_count", followMap.size()+"");
                for ( String follow_name : followMap.keySet() ) {
                    followEle.addElement("weibo_follow").addAttribute("follow_name", follow_name)
                                                        .addAttribute("follow_link", followMap.get(follow_name));
                }
            }

            File save_file = new File(System.currentTimeMillis() + ".xml");
            FileUtils.writeStringToFile(save_file, formatXML(root));
            logger.info("[保存成功] - " + save_file.getAbsolutePath());
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    private String formatXML(Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
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
}
