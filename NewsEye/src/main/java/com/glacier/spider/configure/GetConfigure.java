package com.glacier.spider.configure;

import com.glacier.spider.crawler.scheduler.BloomFilter;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * Created by glacier on 14-12-19.
 */
public class GetConfigure {

    private static Logger logger = Logger.getLogger(GetConfigure.class.getName());
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
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public static String getConfigure(Integer id) {
        try {
            init();
            String configure = mapper.getConfigure(id);
            session.commit();
            return configure;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }

    public static BloomFilter getBloomFilter(Integer id) {
        try {
            init();
            BloomBatis bloomBatis = mapper.getBloomFilter(id);
            System.out.println(bloomBatis);
            byte[] bloomByte = bloomBatis.getBloomfilter();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bloomByte));
            BloomFilter bloomFilter = (BloomFilter)ois.readObject();
            if ( bloomFilter == null )
                bloomFilter = new BloomFilter(100000);
            return bloomFilter;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
            BloomFilter bloomFilter = new BloomFilter(100000);
            return bloomFilter;
        }
    }

    public static void setBloomFilter(Integer id, BloomFilter bloomFilter) {
        try {
            init();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(bloomFilter);
            byte[] bloomByte = baos.toByteArray();

            BloomBatis bloomBatis = new BloomBatis();
            bloomBatis.setId(id);
            bloomBatis.setBloomfilter(bloomByte);

            mapper.setBloomFilter(bloomBatis);
            session.commit();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
    }

    public static String getUsername(Integer id) {
        try {
            init();
            String username = mapper.getUsername(id);
            session.commit();
            return username;
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.error(baos.toString());
        }
        return null;
    }
}
