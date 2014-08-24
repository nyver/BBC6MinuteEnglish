package com.nyver.bbclearningenglish;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nyver.bbclearningenglish.helper.NetHelper;
import com.nyver.bbclearningenglish.html.HtmlParser;
import com.nyver.bbclearningenglish.html.exception.HtmlException;
import com.nyver.bbclearningenglish.html.exception.LoadHtmlException;
import com.nyver.bbclearningenglish.rss.RssReader;
import com.nyver.bbclearningenglish.rss.SixMinuteRssStrategy;
import com.nyver.bbclearningenglish.rss.adapter.RssItemAdapter;
import com.nyver.bbclearningenglish.rss.model.RssItem;
import com.nyver.bbclearningenglish.rss.task.LoadRssTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

        private List<RssItem> items = new ArrayList<RssItem>();

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (items.isEmpty()) {
                items = loadItems();
            }
            ArrayAdapter<RssItem> listAdapter = new RssItemAdapter(getActivity(), R.layout.rss_item, items);

            ListView rssItemsListView = (ListView) rootView.findViewById(R.id.rssItemsListView);
            rssItemsListView.setAdapter(listAdapter);
            rssItemsListView.setOnItemClickListener(this);

            return rootView;
        }

        private List<RssItem> loadItems() {
            if (NetHelper.isOnline(getActivity())) {
                try {
                    return new LoadRssTask(getActivity()).execute(new RssReader(new SixMinuteRssStrategy())).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.error_cant_connect_or_load_list, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.error_u_havent_internet_connection, Toast.LENGTH_LONG).show();
            }

            return Collections.emptyList();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            RssItem item = items.get(i);
            if (null != item) {
                if (null == item.getAudioLink()) {
                    HtmlParser parser = new HtmlParser(item.getLink());
                    try {
                        item.setAudioLink(parser.getAudioLink());
                        Toast.makeText(getActivity(), item.getAudioLink(), Toast.LENGTH_LONG).show();
                    } catch (HtmlException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), String.format(getString(R.string.error_cant_load_or_parse), item.getLink()), Toast.LENGTH_LONG);
                    }
                }

                
            }
        }
    }

    public static class ItemFragment extends Fragment {

        private RssItem item;

        public ItemFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_item, container, false);
            return rootView;
        }
    }

}
