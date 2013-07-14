
package seker.common.net.transport;

/**
 * 
 * 传输进度监听器
 * 
 * 如果有多个URL，那么:
 *      全部URL传输完成才算成功
 *      有一个URL传输失败就算是失败（其他的URL会继续传输完成）
 * 
 * @author Lifeix
 * 
 */
public interface ITransportCallback {
//    
//    public final int STATE_ERROR      = -2;
//    
//    public final int STATE_CANCEL     = -1;
//    
//    public final int STATE_START      = 0;
//    
//    public final int STATE_PROCESSING = 1;
//    
//    public final int STATE_END        = 2;
    
    public void onStart();
    
    public void onProgress(long curr, long total);
    
    public void onFinished();
    
    public void onError();
    
    public void onCanceled();
}
