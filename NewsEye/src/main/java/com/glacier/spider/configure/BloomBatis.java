package com.glacier.spider.configure;

/**
 * Created by IntelliJ IDEA on 2014-12-20 20:35.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
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