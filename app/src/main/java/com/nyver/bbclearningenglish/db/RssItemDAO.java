package com.nyver.bbclearningenglish.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.sql.SQLException;
import java.util.List;

public class RssItemDAO extends BaseDaoImpl<RssItem, Integer> {

    protected RssItemDAO(ConnectionSource connectionSource, Class<RssItem> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<RssItem> getAllItems() throws SQLException {
        return queryForAll();
    }
}