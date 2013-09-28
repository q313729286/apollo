package seker.webspider;

import java.io.IOException;

public class WebPageGet {
    public static final String WGET = "E:/git/apollo/WebSpider/truck/WebSpider/wget";
    
    public void request(String url, String file) {
        try {
            Process proc = Runtime.getRuntime().exec(WGET + " " + url + " -O " + file);
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
