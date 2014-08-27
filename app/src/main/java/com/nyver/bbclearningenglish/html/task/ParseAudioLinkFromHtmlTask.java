package com.nyver.bbclearningenglish.html.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nyver.bbclearningenglish.html.HtmlParser;
import com.nyver.bbclearningenglish.html.exception.LoadHtmlException;

public class ParseAudioLinkFromHtmlTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = ParseAudioLinkFromHtmlTask.class.getSimpleName();

    private Context context;
    private ProgressDialog progressDialog;

    public ParseAudioLinkFromHtmlTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Parsing...", "Parsing audio link");
    }

    @Override
    protected String doInBackground(String... values) {
        String link = null;
        HtmlParser parser = new HtmlParser(values[0]);
        try {
             link = parser.getAudioLink();
        } catch (LoadHtmlException e) {
            e.printStackTrace();
            Log.e(TAG, "Couldn't parse audio link from " + values[0]);
        }
        return link;
    }

    @Override
    protected void onPostExecute(String s) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPostExecute(s);
    }
}
