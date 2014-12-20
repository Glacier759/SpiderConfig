package com.glacier.spider.configure;

import java.sql.Date;

/**
 * Created by IntelliJ IDEA on 2014-12-20 20:26.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class ConfigBatis {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public Date getSubmit_date() {
        return submit_date;
    }

    public void setSubmit_date(Date submit_date) {
        this.submit_date = submit_date;
    }

    public byte[] getBloomfilter() {
        return bloomfilter;
    }

    public void setBloomfilter(byte[] bloomfilter) {
        this.bloomfilter = bloomfilter;
    }

    private int id, aid, active;
    private String uid;
    private String conf;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;
    private Date submit_date;
    private byte[] bloomfilter;
}
