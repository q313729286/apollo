
package seker.common.cache;


public class CacheHelper {
    public static String getSuffix(String remote) {
        int start = remote.lastIndexOf(".");
        int end = remote.lastIndexOf("?version=");
        end = (-1 == end || end <= start) ? remote.length() : end;
        String suffix = remote.substring(start, end);
        
        return suffix;
    }
    
    public static String getFileNameFromUrl(String url) {
        // replace all special URI characters with a single + symbol
        return url.replaceAll("[:/,%?&=]", "+").replaceAll("[+]+", "+") + getSuffix(url);
    }
}
