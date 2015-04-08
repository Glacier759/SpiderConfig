import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by IntelliJ IDEA on 2015-04-08 19:33.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class lll {
    public static void main(String[] args) {

        byte b = (byte)128;
        System.out.println(b);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data><item id=\"1\">10</item><item id=\"2\">50</item></data>";
        lll obj = new lll();
        //obj.start(xml);
    }

    public void start( String xml ) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(xml.getBytes()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(bufferedReader);
            Element element = document.getRootElement();

            Map<String, Integer> item_map = new HashMap<String, Integer>();
            List<Element> elementList = element.elements("item");
            int random_seed = 0;
            for ( Iterator iterator = elementList.iterator(); iterator.hasNext(); ) {
                Element itor_ele = (Element)iterator.next();
                Integer num = Integer.parseInt(itor_ele.getText());
                String id = itor_ele.attributeValue("id");
                random_seed += num;
                item_map.put(id, num);
            }

            Random random = new Random();
            int rand = random.nextInt(random_seed);

            int count = 0;
            for ( String id : item_map.keySet() ) {
                int item_count = item_map.get(id);
                count += item_count;
                if ( rand <= count ) {
                    System.out.println("id = " + id + " 抽中了");
                    break;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
