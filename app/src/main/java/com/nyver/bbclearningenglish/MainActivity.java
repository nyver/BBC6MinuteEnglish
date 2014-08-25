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

import com.nyver.bbclearningenglish.db.DatabaseHelper;
import com.nyver.bbclearningenglish.db.DatabaseHelperFactory;
import com.nyver.bbclearningenglish.db.RssItemDAO;
import com.nyver.bbclearningenglish.helper.NetHelper;
import com.nyver.bbclearningenglish.html.HtmlParser;
import com.nyver.bbclearningenglish.html.exception.HtmlException;
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

        private static final String TAG = MainFragment.class.getSimpleName();


        private List<RssItem> items = new ArrayList<RssItem>();

        private RssItemDAO rssItemDAO;

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            try {
                rssItemDAO = DatabaseHelperFactory.getHelper().getRssItemDAO();
                items = rssItemDAO.getAllItems();
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error retrieving rss items from database");
            }

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            if (items.isEmpty()) {
                items = loadItems();
                saveItems();

            }
            ArrayAdapter<RssItem> listAdapter = new RssItemAdapter(getActivity(), R.layout.rss_item, items);

            ListView rssItemsListView = (ListView) rootView.findViewById(R.id.rssItemsListView);
            rssItemsListView.setAdapter(listAdapter);
            rssItemsListView.setOnItemClickListener(this);

            return rootView;
        }

        private void saveItems() {
            try {
                for(RssItem item: items) {
                    if (item.getId() > 0) {
                        rssItemDAO.update(item);
                    } else {
                        rssItemDAO.create(item);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e(TAG, "Error save rss items to database");
            }
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
                        rssItemDAO.update(item);
                    } catch (HtmlException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), String.format(getString(R.string.error_cant_load_or_parse), item.getLink()), Toast.LENGTH_LONG);
                        Log.e(TAG, String.format(getString(R.string.error_cant_load_or_parse), item.getLink()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error save rss item " + item.getTitle());
                    }
                }

                if (null != item.getAudioLink() && !item.getAudioLink().isEmpty()) {
                    Toast.makeText(getActivity(), item.getAudioLink(), Toast.LENGTH_LONG).show();

                    Fragment fragment = ItemFragment.newInstance(item);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), R.string.error_couldnt_parse_an_audio_link_for_this_episode, Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public static class ItemFragment extends Fragment {

        public static final String INDEX_ITEM = "item";

        private boolean initialState = true;

        private RssItem item;
        private MediaPlayer mediaPlayer;

        public static ItemFragment newInstance(RssItem item) {
            ItemFragment fragment = new ItemFragment();

            Bundle args = new Bundle();
            args.putSerializable(INDEX_ITEM, item);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_item, container, false);

            Bundle bundle = getArguments();
            item = (RssItem) bundle.get(INDEX_ITEM);

            TextView title = (TextView) rootView.findViewById(R.id.itemTitle);
            title.setText(item.getTitle());

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(item.getAudioLink());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Button playButton = (Button) rootView.findViewById(R.id.playButton);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            });

            Button stopButton = (Button) rootView.findViewById(R.id.stopButton);
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
            });

            return rootView;
        }

        @Override
        public void onStop() {
            super.onStop();
            if (null != mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

}
