package com.nyver.bbclearningenglish.rss;

import com.nyver.bbclearningenglish.rss.model.RssItem;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public interface RssStrategyInterface {
    public URL getUrl() throws MalformedURLException;

    public List<RssItem> parse(InputStream in) throws IOException, XmlPullParserException;
}
