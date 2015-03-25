package com.glacier.spider.crawler.pipeline;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA on 2015-03-25 21:52.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class UserInfo {
    public String user_url = null, user_id = null, user_name;
    public String weibo_count = null, follow_count = null, fans_count = null;
    public HashMap<String,String> basicInfo = new HashMap<String,String>();

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(user_name + "\tuser_url = " + user_url + "\tuser_id = " + user_id + "\n");
        buffer.append("weibo_count = " + weibo_count + "\tfollow_count = " + follow_count + "\tfans_count = " + fans_count + "\n");
        for ( String key : basicInfo.keySet() ) {
            buffer.append(key + " - " + basicInfo.get(key) + "\n");
        }
        return buffer.toString();
    }
}
