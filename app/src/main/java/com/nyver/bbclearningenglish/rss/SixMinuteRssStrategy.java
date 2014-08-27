package com.nyver.bbclearningenglish.rss;

import android.util.Log;
import android.util.Xml;

import com.nyver.bbclearningenglish.rss.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SixMinuteRssStrategy extends AbstractRssStrategy {

    private static final String TAG = SixMinuteRssStrategy.class.getSimpleName();

    public static final String URL = "http://www.bbc.co.uk/worldservice/learningenglish/general/sixminute/index.xml";
    private static final String NS = null;

    private static final String TAG_FEED = "feed";
    private static final String TAG_ENTRY = "entry";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_PUBLISHED = "published";
    private static final String TAG_LINK = "link";

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH);

    private static final Pattern titleCheckPattern = Pattern.compile("[0-9]{4} programmes");

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
                RssItem item = readEntry(parser);
                if (!isIgnoredTitle(item.getTitle())) {
                    items.add(item);
                }
            } else {
                skip(parser);
            }
        }

        return items;
    }

    private boolean isIgnoredTitle(String title) {
        Matcher matcher = titleCheckPattern.matcher(title);
        return matcher.find();
    }

    private RssItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, TAG_ENTRY);
        String rssId = null;
        String title = null;
        String summary = null;
        String link = null;
        Date published = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TAG_ID)) {
                rssId = readTag(TAG_ID, parser);
            } else if (name.equals(TAG_TITLE)) {
                title = readTag(TAG_TITLE, parser);
            } else if (name.equals(TAG_SUMMARY)) {
                summary = readSummary(parser);
            } else if (name.equals(TAG_LINK)) {
                link = readLink(parser);
            } else if (name.equals(TAG_PUBLISHED)) {
                published = parseDate(readTag(TAG_PUBLISHED, parser));
            } else {
                skip(parser);
            }
        }
        return new RssItem(rssId, title, summary, link, published);
    }

    private Date parseDate(String str) {
        Date date = Calendar.getInstance().getTime();
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not parse date " + str);
        }
        return date;
    }

    // Processes title tags in the feed.
    private String readTag(String tag, XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, tag);
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, tag);
        return content;
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
