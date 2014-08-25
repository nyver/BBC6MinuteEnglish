package com.nyver.bbclearningenglish.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseHelperFactory {
    private static DatabaseHelper databaseHelper;

    public static void setHelper(Context context){
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static void releaseHelper(){
        OpenHelperManager.release();
        databaseHelper = null;
    }

    public static DatabaseHelper getHelper() {
        return databaseHelper;
    }
}
