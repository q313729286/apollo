package seker.common.net.transport;

import java.util.ArrayList;
import java.util.List;

/**
 * 传输任务信息
 * 
 * 如果有多个URL，那么:
 *      全部URL传输完成才算成功
 *      有一个URL传输失败就算是失败
 *          串行：后续的URL不会再继续传输
 *          并行：其他的URL会被继续传输
 * 
 * @author seker
 *
 */
public class TransportTaskInfo {
    
    public enum TransportMode{
        MTTMF, // single thread transport single file;
        MTTSF, // single thread transport multiple files;
        STTMF, // multiple threads transport single file(必须指定线程数);
        STTSF, // multiple threads transport single files(线程数和文件数必须相等);
    }
    
    final List<String> mFiles;
    
    /**
     * 仅上传文件时使用：上传文件所带的参数
     */
    List<String> mKeys;

    final int mThreadNum;

    final TransportMode mTransportMode;
    
    final List<String> mUrls;

    /**
     * 
     * 
     * @param serial
     *      true: single thread transport multiple files;
     *      false: multiple threads transport multiple files;
     * @param urls
     */
    public TransportTaskInfo(boolean serial, String[] urls, String[] files) {
        this(serial ? TransportMode.STTMF : TransportMode.MTTMF, serial ? 1 : urls.length, urls, files);
    }
    
    /**
     * multiple threads transport single file;
     * 
     * @param threadNum
     * @param url
     */
    public TransportTaskInfo(int threadNum, String url, String file) {
        this(TransportMode.MTTSF, threadNum, new String[] {url}, new String[] {file});
    }
    
    /**
     * single thread transport single file;
     * 
     * @param url
     */
    public TransportTaskInfo(String url, String file) {
        this(TransportMode.STTSF, 1, new String[] {url}, new String[] {file});
    }

    private TransportTaskInfo(TransportMode mode, int threadNum, String[] urls, String[] files) {
        if (urls.length != files.length) {
            throw new RuntimeException("urls.length != files.length");
        }
        
        mTransportMode = mode;
        mThreadNum = threadNum;
        
        mUrls = new ArrayList<String>(urls.length);
        for (String url : urls) {
            mUrls.add(url);
        }

        mFiles = new ArrayList<String>(files.length);
        for (String file : files) {
            mFiles.add(file);
        }
    }
    
    public List<String> getFiles() {
        return mFiles;
    }
    
    public List<String> getKeys() {
        return mKeys;
    }
    
    public TransportMode getTransportMode() {
        return mTransportMode;
    } 
    
    public List<String> getUrls() {
        return mUrls;
    }
    
    public void setKeys(String[] keys) {
        if (null == keys || mFiles.size() != keys.length) {
            throw new RuntimeException("null == keys || mFiles.size() != keys.length");
        }
        
        mKeys = new ArrayList<String>(keys.length);
        for (String key : keys) {
            mKeys.add(key);
        }
    } 
}
