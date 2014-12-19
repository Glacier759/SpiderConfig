package com.glacier.spider.configure;


/**
 * Created by glacier on 14-12-19.
 */
public class BloomBatis {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;

    private byte[] bloomfilter;

    public byte[] getBloomfilter() {
        return bloomfilter;
    }

    public void setBloomfilter(byte[] bloomfilter) {
        this.bloomfilter = bloomfilter;
    }
}
