package seker.multimedia;

import seker.common.BaseApplication;
import seker.common.cache.ImageLoader;

public class MultimediaApp extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        
        ImageLoader.initialize(getApplicationContext());
    }

}
