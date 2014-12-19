package com.glacier.spider.crawler.pipeline;

import com.glacier.spider.NewsEye;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by glacier on 14-12-12.
 */
public class SaveFormat {
    public static String title, source;
    public static String newspaper, page;
    public static String publishDate, crawlDate;
    public static String language, encode;
    public static String body;
    public static List<String> img = new ArrayList<String>();

    private static Logger logger = Logger.getLogger(SaveFormat.class.getName());

    public static String save() {
        try {
            return save(new File("./Data_" + NewsEye.USERNAME));
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.debug(baos.toString());
        }
        return null;
    }

    public static String save(File savePath) {
        try {
            Document xmlDoc = DocumentHelper.createDocument();
            Element root = xmlDoc.addElement("root");

            Element titleEle = root.addElement("title");
            titleEle.addText(title);
            Element urlEle = root.addElement("url");
            urlEle.addText(source);
            Element newspaperEle = root.addElement("newspaper");
            newspaperEle.addText(newspaper);
            Element pageEle = root.addElement("page");
            pageEle.addText(page);
            Element publishDateEle = root.addElement("publishdate");
            publishDateEle.addText(publishDate);
            Element crawlDateEle = root.addElement("crawldate");
            crawlDateEle.addText(crawlDate);
            Element languageEle = root.addElement("language");
            languageEle.addText(language);
            Element encodeEle = root.addElement("encode");
            encodeEle.addText(encode);
            Element bodyEle = root.addElement("body");
            bodyEle.addText(body);
            for ( String imgSrc:img ) {
                Element img = bodyEle.addElement("img");
                img.addAttribute("src", imgSrc);
            }

            String xml = formatXML(root);
            String fileName = System.currentTimeMillis() + xml.hashCode() + ".xml";
            File saveFile = new File(savePath, fileName);

            FileUtils.writeStringToFile(saveFile, xml, "UTF-8");

            return saveFile.getName();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.debug(baos.toString());
        }
        return null;
    }

    private static String formatXML(Element root) {
        String formatXMLStr = null;
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(root.asXML().getBytes()));
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            formatXMLStr = writer.toString();
        }catch (Exception e) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(baos));
            logger.debug(baos.toString());
        }
        return formatXMLStr;
    }
}
