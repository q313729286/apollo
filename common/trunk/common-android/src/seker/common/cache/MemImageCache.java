package seker.common.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap; 

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MemImageCache extends LinkedHashMap<String, SoftReference<Bitmap>> implements IImageCache
{
    private static final int  DEFAULT_CACHE_CAPACITY = 30;

    private int               CACHE_CAPACITY;

    private static final long serialVersionUID       = 4687950469523498447L;

    @Override
    protected boolean removeEldestEntry(LinkedHashMap.Entry<String, SoftReference<Bitmap>> eldest) {
        if (size() > CACHE_CAPACITY) {
//            int i = 0;
//            for (SoftReference<Bitmap> sr : values()) {
//                if (null != sr.get()) {
//                    i ++;
//                }
//            }
//            boolean value = (null == eldest.getValue().get());
//            Log.w("l99", String.format("MemImageCache.removeEldestEntry size()=%d, i=%d, value==null?%s, key=%s.", size(), i, value, eldest.getKey()));
            return true;
        }
        else
            return false;
    }

    public MemImageCache() {
        this(DEFAULT_CACHE_CAPACITY);
    }

    public MemImageCache(int capacity) {
        super(capacity / 2, 0.75f, true);
        CACHE_CAPACITY = capacity;
    }

    /**
     */
    @Override
    public synchronized Bitmap getBitmap(String url) {
        final SoftReference<Bitmap> sr = super.get(url);
        if (sr != null) {
            final Bitmap bitmap = sr.get();
            if (null == bitmap) {
                remove(url);
            } else {
                remove(url);
                put(url, sr);
                return bitmap;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean putBitmap(String url, Bitmap bitmap) {
        put(url, new SoftReference<Bitmap>(bitmap));
        return true;
    }

    @Override
    public synchronized boolean putBitmap(String url, byte[] imageData) {
        boolean result = false;
        
        if (imageData != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, null);
            result = putBitmap(url, bitmap);
        }
        
        return result;
    }

    @Override
    public synchronized void clearImage() {
        super.clear();
    }

    @Override
    public boolean hasImage(String url) {
        return super.containsKey(url);
    }

    @Override
    public boolean removeImage(String url) {
        super.remove(url);
        return super.containsKey(url);
    }
}
