package com.nyver.bbclearningenglish.rss.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.rss.RssReader;
import com.nyver.bbclearningenglish.rss.exception.LoadRssException;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.util.ArrayList;
import java.util.List;

public class LoadRssTask extends AsyncTask<RssReader, Integer, List<RssItem>> {

    private Activity context;

    public LoadRssTask(Activity context) {
        this.context = context;
    }

    @Override
    protected List<RssItem> doInBackground(RssReader... rssReaders) {
        List<RssItem> items = new ArrayList<RssItem>();

        int count = rssReaders.length;
        for (int i = 0; i < count; i++) {
            try {
                items.addAll(rssReaders[i].load());
            } catch (LoadRssException e) {
                e.printStackTrace();
                showError(R.string.error_cant_connect_or_load_list);
            }

            publishProgress((int) ((i / (float) count) * 100));
        }
        return items;
    }

    protected void showError(final int message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
