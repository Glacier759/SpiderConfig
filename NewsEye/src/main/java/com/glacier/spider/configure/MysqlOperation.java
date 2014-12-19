package com.glacier.spider.configure;

/**
 * Created by glacier on 14-12-19.
 */
public interface MysqlOperation {
    public String getConfigure(Integer id);
    public BloomBatis getBloomFilter(Integer id);
    public void setBloomFilter(BloomBatis bloom);
    public String getUsername(Integer id);
}
