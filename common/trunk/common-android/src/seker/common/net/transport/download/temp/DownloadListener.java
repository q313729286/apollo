
package seker.common.net.transport.download.temp;

/**
 * 
 * 下载进度监听器
 * 
 * @author Lifeix
 * 
 */
public interface DownloadListener {
    public final int STATE_ERROR      = -1;
    
    public final int STATE_START      = 0;
    
    public final int STATE_PROCESSING = 1;
    
    public final int STATE_END        = 2;
    
    public final int STATE_CANCEL     = 3;
    
    public void onDownloadStart(int size, int curr);
    
    public void onDownloadProgress(int size, int curr);
    
    public void onDownloadEnd(int size, int curr);
    
    public void onDownloadError(int size, int curr);
    
    public void onDownloadCancel(int size, int curr);
}
