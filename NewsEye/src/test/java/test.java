import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by glacier on 14-12-13.
 */
public class test {
    public static void main(String[] agrs) {
        try {
            Document document = Jsoup.parse("<html>\n" +
                    " <head></head>\n" +
                    " <body></body>\n" +
                    "</html>");
            System.out.println(document);
            System.out.println(document.baseUri());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
