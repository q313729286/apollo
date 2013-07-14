package seker.common.cache;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class ImageLoaderHandler extends Handler {
    @Override
    public final void handleMessage(Message msg) {
        if (msg.what == ImageLoader.HANDLER_MESSAGE_ID) {
            Bundle data = msg.getData();
            String url = data.getString(ImageLoader.IMAGE_URL_EXTRA);
            if (data.getBoolean(ImageLoader.IMAGE_LIMIT_EXTRA, false)) {
                handleImageLimited(url);
            } else {
                Bitmap bitmap = (Bitmap) msg.obj;
                handleImageLoaded(url, bitmap);
            }
        }
    }

    protected boolean handleImageLimited(String url) {
        return false;
    }
    
    protected abstract boolean handleImageLoaded(String url, Bitmap bitmap);
}
