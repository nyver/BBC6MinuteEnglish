package com.nyver.bbclearningenglish.rss.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "rss_items")
public class RssItem implements Serializable {

    public final static String RSS_ITEM_RSS_ID_FIELD_NAME = "rss_id";
    public final static String RSS_ITEM_TITLE_FIELD_NAME = "title";
    public final static String RSS_ITEM_SUMMARY_FIELD_NAME = "summary";
    public final static String RSS_ITEM_LINK_FIELD_NAME = "link";
    public final static String RSS_ITEM_AUDIO_LINK_FIELD_NAME = "audio_link";
    public final static String RSS_ITEM_PUBLISHED_FIELD_NAME = "published";
    public final static String RSS_ITEM_LOCAL_AUDIO_LINK_FIELD_NAME = "local_audio_link";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = RSS_ITEM_RSS_ID_FIELD_NAME)
    private String rssId;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = RSS_ITEM_TITLE_FIELD_NAME)
    private String title;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = RSS_ITEM_SUMMARY_FIELD_NAME)
    private String summary;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = RSS_ITEM_LINK_FIELD_NAME)
    private String link;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = RSS_ITEM_AUDIO_LINK_FIELD_NAME)
    private String audioLink;

    @DatabaseField(canBeNull = true, dataType = DataType.DATE, columnName = RSS_ITEM_PUBLISHED_FIELD_NAME)
    private Date published;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, columnName = RSS_ITEM_LOCAL_AUDIO_LINK_FIELD_NAME)
    private String localAudioLink;

    private boolean isNew = false;

    public RssItem() {
    }

    public RssItem(String rssId, String title, String summary, String link, Date published) {
        this(rssId, title, summary, link, published, true);
    }

    public RssItem(String rssId, String title, String summary, String link, Date published, boolean isNew) {
        this.rssId = rssId;
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.published = published;
        this.isNew = isNew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getLocalAudioLink() {
        return localAudioLink;
    }

    public void setLocalAudioLink(String localAudioLink) {
        this.localAudioLink = localAudioLink;
    }

    public String getRssId() {
        return rssId;
    }

    public void setRssId(String rssId) {
        this.rssId = rssId;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public String toString() {
        return String.format("id=%s, rssId=%s, title=%s", getId(), getRssId(), getTitle());
    }
}
