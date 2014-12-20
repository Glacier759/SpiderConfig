package com.glacier.spider.configure;

/**
 * Created by glacier on 14-12-19.
 */
public interface MysqlOperation {
    public String getConfigure(Integer id);
    public ConfigBatis getConfigBatis(Integer id);
    public void setBloomFilter(BloomBatis bloom);
    public String getUsername(Integer id);
    public void setFileName(ConfigBatis configBatis);
}
