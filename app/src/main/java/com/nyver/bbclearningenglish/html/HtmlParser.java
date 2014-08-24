package com.nyver.bbclearningenglish.html;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.nyver.bbclearningenglish.html.exception.LoadHtmlException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {
    private String url;

    private static final Pattern audioLinkPattern = Pattern.compile("<a[^>]* href=\"([^\"]*\\.mp3)\" class=\"audio-link file-link\">");

    public HtmlParser(String url) {
        this.url = url;
    }

    public String getAudioLink() throws LoadHtmlException {
        String audioLink = null;

        if (null != url) {
            audioLink = parseAudioLink(loadHtml());
        }

        return audioLink;
    }

    private String parseAudioLink(String content) {
        String audioLink = null;
        if (null != content && !content.isEmpty()) {
            Matcher matcher = audioLinkPattern.matcher(content);
            if (matcher.find()) {
                audioLink = matcher.group(matcher.groupCount());
            }
        }

        return audioLink;
    }

    private String loadHtml() throws LoadHtmlException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            return CharStreams.toString(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));

        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new LoadHtmlException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LoadHtmlException(e);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

    }
}
