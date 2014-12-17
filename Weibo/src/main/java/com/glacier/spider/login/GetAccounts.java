package com.glacier.spider.login;

import com.glacier.spider.crawler.pipeline.Accounts;
import com.glacier.spider.crawler.pipeline.MysqlOperation;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Reader;

/**
 * Created by glacier on 14-12-17.
 */
public class GetAccounts {

    private static Logger logger = Logger.getLogger(GetAccounts.class.getName());
    private static Reader reader;
    private static SqlSessionFactory sessionFactory;
    private static SqlSession session;
    private static MysqlOperation mapper;

    private static void init() {
        try {
            reader = Resources.getResourceAsReader("mybatis.xml");
            sessionFactory = new SqlSessionFactoryBuilder().build(reader);
            session = sessionFactory.openSession();
            mapper = session.getMapper(MysqlOperation.class);
        } catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }


    public static Accounts accounts(String type) {
        try {
            init();
            Accounts accounts = mapper.getAccounts(type);
            session.commit();
            mapper.updateAccounts(accounts);
            session.commit();
            return accounts;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }
}
