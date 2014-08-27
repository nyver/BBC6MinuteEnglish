package com.nyver.bbclearningenglish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME ="bbc6me.db";
    private static final int DATABASE_VERSION = 2;

    private RssItemDAO rssItemDAO = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, RssItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, RssItem.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error upgrading db " + DATABASE_NAME + " from ver " + oldVersion);
            throw new RuntimeException(e);
        }
    }

    public RssItemDAO getRssItemDAO() throws SQLException {
        if (null == rssItemDAO ) {
            rssItemDAO = new RssItemDAO(getConnectionSource(), RssItem.class);
        }
        return rssItemDAO;
    }

    @Override
    public void close() {
        super.close();
        rssItemDAO = null;
    }
}
