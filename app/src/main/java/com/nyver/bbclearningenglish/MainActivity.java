package com.nyver.bbclearningenglish;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nyver.bbclearningenglish.db.DatabaseHelperFactory;
import com.nyver.bbclearningenglish.db.RssItemDAO;
import com.nyver.bbclearningenglish.fragment.MainFragment;
import com.nyver.bbclearningenglish.helper.NetHelper;
import com.nyver.bbclearningenglish.html.HtmlParser;
import com.nyver.bbclearningenglish.html.exception.HtmlException;
import com.nyver.bbclearningenglish.html.exception.ParseHtmlException;
import com.nyver.bbclearningenglish.html.task.ParseAudioLinkFromHtmlTask;
import com.nyver.bbclearningenglish.rss.RssReader;
import com.nyver.bbclearningenglish.rss.SixMinuteRssStrategy;
import com.nyver.bbclearningenglish.rss.adapter.RssItemAdapter;
import com.nyver.bbclearningenglish.rss.model.RssItem;
import com.nyver.bbclearningenglish.rss.task.LoadRssTask;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
