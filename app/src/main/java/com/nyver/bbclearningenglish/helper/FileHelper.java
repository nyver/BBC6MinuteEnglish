package com.nyver.bbclearningenglish.helper;

public class FileHelper {
    public static String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }
}
