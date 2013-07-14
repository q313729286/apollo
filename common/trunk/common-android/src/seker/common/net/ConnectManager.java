package seker.common.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.util.Log;

/**
 * 用于判断当前网络连接类型，接入点，代理服务器信息。 
 */
public class ConnectManager {
    
    /** log tag. */
    private static final String TAG = ConnectManager.class.getSimpleName();
    
    /** log debug on/off .*/
    private static final boolean DEBUG = false;
    
    /** current apn. */
    private String mApn;
    
    /** apn proxy. */
    private String mProxy;
    
    /** proxy port. */
    private String mPort;
    
    /** 当前网络是否使用wap。
     *  wifi 或者 cmnet等为不使用 wap.
     */
    private boolean mUseWap;
    
    /** 
     * 网络类型字符串.wifi网络返回 wifi， mobile网络返回对应的apn name:  cmnet, uninet, ctnet, cmwap
     * 有可能为空。
     * */
    private String mNetType; //add by fujiaxing 20120323
    
    /**
     * constructor.
     * @param context  context
     */
    public ConnectManager(Context context) {
        checkNetworkType(context);
    }
    
    /**
     * 检查当前系统apn 设置 状态.
     * @param context context
     * @param networkInfo NetworkInfo instance
     */
    private void checkApn(Context context, NetworkInfo networkInfo) {
        
        if (networkInfo.getExtraInfo() != null) {
            String info = networkInfo.getExtraInfo().toLowerCase(); //3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap 
            // 先根据网络apn信息判断,并进行 proxy 自动补齐
            if (info != null) {
                if (info.startsWith("cmwap") || info.startsWith("uniwap") || info.startsWith("3gwap")) {
                    mUseWap = true;
                    mApn = info;
                    mProxy = "10.0.0.172";
                    mPort = "80";
                    
                    return;
                } else if (info.startsWith("ctwap")) {
                    mUseWap = true;
                    mApn = info;
                    mProxy = "10.0.0.200";
                    mPort = "80";
                    
                    return;
                } else if (info.startsWith("cmnet") || info.startsWith("uninet") || info.startsWith("ctnet")
                        || info.startsWith("3gnet")) {
                    mUseWap = false;
                    mApn = info;
                    // mProxy  mPort 保留初始值
                    
                    return;
                } // else fall through
            } // else fall through
        }
        

        
        // 如果没有 apn 信息，则根据 proxy代理判断。
        // 由于android 4.2 对 "content://telephony/carriers/preferapn" 读取进行了限制，我们通过系统接口获取。
        
        @SuppressWarnings("deprecation")
        String defaultProxyHost = Proxy.getDefaultHost();
        @SuppressWarnings("deprecation")
        int defaultProxyPort = Proxy.getDefaultPort();
        
        if (defaultProxyHost != null && defaultProxyHost.length() > 0) {
            
            // 设置 mProxy  mPort
            mProxy = defaultProxyHost;
            /*
             * 无法根据  proxy host 还原 apn 名字 这里不设置  mApn
             */
            if ("10.0.0.172".equals(mProxy.trim())) {
                // 当前网络连接类型为cmwap || uniwap
                mUseWap = true;
                mPort = "80";
            } else if ("10.0.0.200".equals(mProxy.trim())) {
                mUseWap = true;
                mPort = "80";
            } else {
                // 否则为net
                mUseWap = false;
                mPort = Integer.toString(defaultProxyPort);
            }
        } else {
            // 其它网络都看作是net
            mUseWap = false;
        }
    }
    
    /**
     * 检查当前网络类型。
     * @param context context
     */
    private void checkNetworkType(Context context) {
        
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            
            if (DEBUG) {
                Log.d(TAG, "network type : " + activeNetInfo.getTypeName().toLowerCase());
            }
            
            if ("wifi".equals(activeNetInfo.getTypeName().toLowerCase())) {
                mNetType = "wifi";
                mUseWap = false;
            } else { // mobile
                checkApn(context, activeNetInfo);
                
                mNetType = mApn; // 移动网络对应apn name，有可能为空。
            }
        }
        
        if (DEBUG) {
            Log.d(TAG, "checkApn: " + " mApn = " + mApn + "， mProxy = "
                    + mProxy + " ，mPort = " + mPort + " , mUseWap = " + mUseWap + " , mNetType = " + mNetType);
        }
    }
    
    /**
     * 当前网络是否连接上了。
     * 
     * @param context Context
     * @return activeNetInfo.isConnected();
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = 
            (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            // 修复部分手机其实网络已经连接，但是 activeNetInfo.isConnected（） 返回false的bug。此时状态为 connecting.
            //return activeNetInfo.isConnected();
            return activeNetInfo.isConnectedOrConnecting();
        }
        return false;
    }
    
    /**
     * 当前网络连接是否是wap网络。
     * @return  cmwap 3gwap ctwap 返回true
     */
    public boolean isWapNetwork() {
        return mUseWap;
    }
    
    /**
     * 获取当前网络连接apn.
     * @return apn
     */
    public String getApn() {
        return mApn;
    }
    
    /**
     * 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172.
     * @return 获取当前网络连接的代理服务器地址，比如 cmwap 代理服务器10.0.0.172
     */
    public String getProxy() {
        return mProxy;
    }
    
    /**
     * 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80.
     * @return 获取当前网络连接的代理服务器端口，比如 cmwap 代理服务器端口 80
     */
    public String getProxyPort() {
        return mPort;
    }
    
    //<add by fujiaxing 20120323 BEGIN
    /**
     * 获得当前网络类型
     * @return wifi, cmnet, uninet, ctnet, cmwap ……
     */
    public String getNetType() {
        return mNetType;
    }
    //add by fujiaxing END>
}
