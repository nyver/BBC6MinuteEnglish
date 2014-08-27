package com.nyver.bbclearningenglish.rss.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.rss.RssReader;
import com.nyver.bbclearningenglish.rss.exception.LoadRssException;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.util.ArrayList;
import java.util.List;

public class LoadRssTask extends AsyncTask<RssReader, Integer, List<RssItem>> {

    private Activity context;

    private ProgressDialog progressDialog;

    public LoadRssTask(Activity context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, context.getString(R.string.loading_progress_title), context.getString(R.string.loading_progress_description), true);
        super.onPreExecute();
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

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(List<RssItem> rssItems) {
        super.onPostExecute(rssItems);
        progressDialog.dismiss();
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
