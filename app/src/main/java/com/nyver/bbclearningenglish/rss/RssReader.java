package com.nyver.bbclearningenglish.rss;

import com.nyver.bbclearningenglish.rss.exception.LoadRssException;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssReader {
    private RssStrategyInterface rssStrategy;

    private List<RssItem> items = new ArrayList<RssItem>();

    public RssReader(RssStrategyInterface rssStrategy) {
        this.rssStrategy = rssStrategy;
    }

    public List<RssItem> load() throws LoadRssException {
        items.clear();

        HttpURLConnection connection = null;
        try {
            URL url = rssStrategy.getUrl();
            connection = (HttpURLConnection) url.openConnection();
            items.addAll(rssStrategy.parse(connection.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LoadRssException(e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

        return items;
    }
}
