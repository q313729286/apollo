package seker.webspider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebPageSpider {
    
    public static String REGEX_URLS = "<a\\s+href=\"(?<url>.*?/product/\\d+.shtml).*?\">(?<text>.*?)</a>";
    
    public static String REGEX_TITLE = "<h1>(?<text>.*?)</h1>";
    
    public static String REGEX_PRICE = "<b\\s+class=\"f26\"\\s+id=\"minprice\">(?<price>.*?)</b>";
    
    public static String REGEX_LOCATION = "<li\\s+id=\"stadiumInfo\"\\s+addr=\"(?<addr>.*?)\"";
    
    public List<String> spideIndexPage(String html) {
        List<String> urls = null;
        
        if (null != html && html.length() > 0) {
            urls = new ArrayList<String>();
            
            Pattern pattern = Pattern.compile(REGEX_URLS);
            Matcher matcher = pattern.matcher(html);
            while(matcher.find()) {
                urls.add(matcher.group());
            }
        }
        return urls;
    }
    
    public Data spideContent(String html) {
        Data data = null;
        if (null != html && html.length() > 0) {
            data = new Data();
            
            Pattern pattern = Pattern.compile(REGEX_TITLE);
            Matcher matcher = pattern.matcher(html);
            while(matcher.find()) {
                data.title = matcher.group();
                break;
            }
            
            pattern = Pattern.compile(REGEX_PRICE);
            matcher = pattern.matcher(html);
            while(matcher.find()) {
                data.price = matcher.group();
                break;
            }
            
            pattern = Pattern.compile(REGEX_LOCATION);
            matcher = pattern.matcher(html);
            while(matcher.find()) {
                data.loacation = matcher.group();
                break;
            }
        }
        return data;
    }
}
