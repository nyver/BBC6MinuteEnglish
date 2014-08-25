package com.nyver.bbclearningenglish;

import android.app.Application;

import com.nyver.bbclearningenglish.db.DatabaseHelperFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelperFactory.setHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        DatabaseHelperFactory.releaseHelper();
        super.onTerminate();
    }
}
