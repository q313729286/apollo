package seker.webspider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import seker.common.utils.StreamUtils;

public class Main {

    public static final String URL_58_PIAO = "http://bj.58.com/piao/";

    public static final String ROOT = "E:/git/apollo/WebSpider/truck/WebSpider/";

    public static final String PAGES = ROOT + "pages/";

    public static final String PAGE_58_PIAO = ROOT + "index.html";

    /**
     * @param args
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        String html = null;
        String file = null;
        List<String> urls = null;

        WebPageGet wget = new WebPageGet();
        file = PAGE_58_PIAO;
        wget.request(URL_58_PIAO, file);
        try {
            html = StreamUtils.streamToString(new FileInputStream(file));
            System.out.println(html);

            if (null != html && html.length() > 0) {
                WebPageSpider spider = new WebPageSpider();
                urls = spider.spideIndexPage(html);
                if (null != urls && !urls.isEmpty()) {
                    List<Data> datas = new ArrayList<Data>(urls.size());
                    for (String url : urls) {
                        file = PAGES + url.substring(url.lastIndexOf("/"));
                        wget.request(url, file);
                        html = StreamUtils.streamToString(new FileInputStream(file));
                        System.out.println(html);
                        
                        Data data = spider.spideContent(html);
                        System.out.println(data.toString());
                        datas.add(data);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
