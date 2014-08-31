package com.nyver.bbclearningenglish.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.helper.FileHelper;
import com.nyver.bbclearningenglish.helper.StorageHelper;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<RssItem, Integer, String> {
    private static final String TAG = DownloadTask.class.getSimpleName();
    private Activity context;

    private ProgressDialog progressDialog;

    public DownloadTask(Activity context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, context.getString(R.string.downloading_progress_title), context.getString(R.string.downloading_progress_description));
    }

    @Override
    protected String doInBackground(RssItem... items) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            for(int i = 0; i < items.length; i++) {
                RssItem item = items[i];
                String fileName = FileHelper.getFileNameFromUrl(item.getAudioLink());
                if (null != fileName && !fileName.isEmpty()) {
                    URL url = new URL(item.getAudioLink());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                        showError(String.format(context.getString(R.string.error_cant_download_file), item.getAudioLink()));
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    StorageHelper.createStorageIfNotExists(context);
                    File file = StorageHelper.getPath(context, fileName);
                    output = new FileOutputStream(file);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) { // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        }
                        output.write(data, 0, count);
                    }

                    return file.getCanonicalPath();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    protected void showError(final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
    }
}
