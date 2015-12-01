package com.glacier.spider.configure;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA on 2015-03-30 10:46.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class Configure {

    private Integer crawl_type = 0;
    private List<String> user_list = new LinkedList<String>();
    private List<String> keywords_list = new LinkedList<String>();
    private boolean user_info, follow_list, fans_list, weibo_list;

    public Integer getCrawl_type() {
        return crawl_type;
    }

    public List<String> getUser_list() {
        return user_list;
    }

    public List<String> getKeywords_list() {
        return keywords_list;
    }

    public boolean isUser_info() {
        return user_info;
    }

    public boolean isFollow_list() {
        return follow_list;
    }

    public boolean isFans_list() {
        return fans_list;
    }

    public boolean isWeibo_list() {
        return weibo_list;
    }

    private Configure() {
        try {
            SAXReader saxReader = new SAXReader();
            Document xmlDoc = saxReader.read(new File("weibo_configure.xml"));
            Element root = xmlDoc.getRootElement();
            Element crawlType = root.element("crawl_type");

            if (crawlType.attribute("search") != null && crawlType.attribute("search").getValue().equals("true")) {
                crawl_type += 1;
            }
            if (crawlType.attribute("user") != null && crawlType.attribute("user").getValue().equals("true")) {
                crawl_type += 2;
            }
            List<Element> userList = root.element("user_list").elements("start_url");
            for ( Element start_url : userList ) {
                user_list.add(start_url.getText());
            }
            List<Element> keywordsList = root.element("keywords_list").elements("keywords");
            for ( Element keywords : keywordsList ) {
                keywords_list.add(keywords.getText());
            }
            if (root.element("user_info") != null) { user_info = Boolean.valueOf(root.element("user_info").getText());  }
            if (root.element("follow_list") != null) { follow_list = Boolean.valueOf(root.element("follow_list").getText());    }
            if (root.element("fans_list") != null) { fans_list = Boolean.valueOf(root.element("fans_list").getText());  }
            if (root.element("weibo_list") != null) { weibo_list = Boolean.valueOf(root.element("weibo_list").getText());   }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConfigureFactory {
        private static Configure config = new Configure();
    }

    public static Configure getInstance() {
        return ConfigureFactory.config;
    }

    public static void main(String[] args) {
        Configure.getInstance();
    }
}
