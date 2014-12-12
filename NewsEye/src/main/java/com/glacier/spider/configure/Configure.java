package com.glacier.spider.configure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-12-12.
 */
public class Configure {

    public class NewspaperClass {
        public String paper_name, paper_starturl, paper_encode;
    }
    public class PageClass {
        public String page_url_outer, page_url_node, page_url_tag, page_url_attr, attr_value;
        public String page_url_sub, page_url_front, page_url_rear, page_url_have, page_url_draw;
    }
    public class NewsClass {
        public String news_url_outer, news_url_node, news_url_tag, news_url_attr, attr_value;
        public String news_url_sub, news_url_front, news_url_rear, news_url_have, news_url_draw;
    }
    public class ContentClass {
        public String title_outer, title_tag, title_attr, title_draw, time_format;
        public String content_outer, content_tag, img_outer, img_tag;
    }
    public class Config {
        public List<NewspaperClass> newspaperList = new ArrayList<NewspaperClass>();
        public PageClass page = new PageClass();
        public NewsClass news = new NewsClass();
        public ContentClass content = new ContentClass();
    }
    public List<Config> configList = new ArrayList<Config>();
}
