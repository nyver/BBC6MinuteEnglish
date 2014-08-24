package com.nyver.bbclearningenglish.rss;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractRssStrategy implements RssStrategyInterface {

    public abstract String getUrlString();

    public URL getUrl() throws MalformedURLException {
        return new URL(getUrlString());
    }
}
