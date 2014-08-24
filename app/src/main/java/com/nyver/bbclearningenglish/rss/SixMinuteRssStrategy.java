package com.nyver.bbclearningenglish.rss;

import android.util.Xml;

import com.nyver.bbclearningenglish.rss.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SixMinuteRssStrategy extends AbstractRssStrategy {

    public static final String URL = "http://www.bbc.co.uk/worldservice/learningenglish/general/sixminute/index.xml";
    private static final String NS = null;

    private static final String TAG_FEED = "feed";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_PUBLISHED = "published";
    private static final String TAG_LINK = "link";

    @Override
    public String getUrlString() {
        return URL;
    }

    @Override
    public List<RssItem> parse(InputStream in) throws IOException, XmlPullParserException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    private List<RssItem> readFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<RssItem> items = new ArrayList<RssItem>();
        parser.require(XmlPullParser.START_TAG, NS, TAG_FEED);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(TAG_ENTRY)) {
                items.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }

        return items;
    }

    private RssItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, TAG_ENTRY);
        String title = null;
        String summary = null;
        String link = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(TAG_SUMMARY)) {
                summary = readSummary(parser);
            } else if (name.equals(TAG_LINK)) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new RssItem(title, summary, link);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, TAG_TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, TAG_TITLE);
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, NS, TAG_LINK);
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, NS, TAG_LINK);
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, TAG_SUMMARY);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, TAG_SUMMARY);
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
