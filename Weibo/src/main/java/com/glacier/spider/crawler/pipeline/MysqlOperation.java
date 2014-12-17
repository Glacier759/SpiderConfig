package com.glacier.spider.crawler.pipeline;

/**
 * Created by glacier on 14-12-17.
 */
public interface MysqlOperation {

    public Accounts getAccounts(String type);

    public void updateAccounts(Accounts accounts);
}
