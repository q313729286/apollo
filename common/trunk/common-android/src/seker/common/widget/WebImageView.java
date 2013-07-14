
package seker.common.widget;

import seker.common.cache.ImageLoader;
import seker.common.cache.ImageLoaderHandler;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Lifeix
 */
public class WebImageView extends ImageView {
    
    protected String mImageUrl;
    
    public WebImageView(Context context) {
        this(context, null);
    }
    
    public WebImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }
    
    public void loadWebImage(String url) {
        // Log.d("l99", url);
        mImageUrl = url;
        ImageLoader.start(mImageUrl, new WebImageViewHandler());
    }
    
    private class WebImageViewHandler extends ImageLoaderHandler {
        
        @Override
        protected boolean handleImageLoaded(String url, Bitmap bitmap) {
            if (url.equalsIgnoreCase(mImageUrl)) {
                if (null != bitmap) {
                    setImageBitmap(bitmap);
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * @return the mImageUrl
     */
    public String getImageUrl() {
        return mImageUrl;
    }
}
