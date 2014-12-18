package com.glacier.spider.crawler.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by glacier on 14-12-18.
 */
public class UserStruct {
    public List<WeiboStruct> weiboList = new ArrayList<WeiboStruct>();
    public HashMap<String,String> basicInfo = new HashMap<String,String>();
    public HashMap<String,String> fansMap, followMap;
}
