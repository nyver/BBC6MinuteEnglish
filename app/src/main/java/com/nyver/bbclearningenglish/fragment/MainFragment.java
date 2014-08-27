package com.nyver.bbclearningenglish.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.db.DatabaseHelperFactory;
import com.nyver.bbclearningenglish.db.RssItemDAO;
import com.nyver.bbclearningenglish.helper.NetHelper;
import com.nyver.bbclearningenglish.html.exception.ParseHtmlException;
import com.nyver.bbclearningenglish.html.task.ParseAudioLinkFromHtmlTask;
import com.nyver.bbclearningenglish.rss.RssReader;
import com.nyver.bbclearningenglish.rss.SixMinuteRssStrategy;
import com.nyver.bbclearningenglish.rss.adapter.RssItemAdapter;
import com.nyver.bbclearningenglish.rss.model.RssItem;
import com.nyver.bbclearningenglish.rss.task.LoadRssTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private Map<String, RssItem> items = new LinkedHashMap<String, RssItem>();

    private RssItemDAO rssItemDAO;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDatabase();

        loadItemsFromDatabase();

        if (items.isEmpty()) {
            loadItemsFromRss();
            saveItemsToDatabase();
        }

    }

    private void setupDatabase() {
        try {
            rssItemDAO = DatabaseHelperFactory.getHelper().getRssItemDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error creating or updating a database");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayAdapter<RssItem> listAdapter = new RssItemAdapter(getActivity(), R.layout.rss_item, new ArrayList<RssItem>(items.values()));
        ListView rssItemsListView = (ListView) rootView.findViewById(R.id.rssItemsListView);
        rssItemsListView.setAdapter(listAdapter);
        rssItemsListView.setOnItemClickListener(this);

        return rootView;
    }

    private void loadItemsFromDatabase() {
        try {
            List<RssItem> loadedItems = rssItemDAO.getAllItems();
            for(RssItem item: loadedItems) {
                items.put(item.getRssId(), item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error retrieving rss items from database");
        }
    }

    private void saveItemsToDatabase() {
        try {
            for(RssItem item: items.values()) {
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

    private List<RssItem> loadItemsFromRss() {
        if (NetHelper.isOnline(getActivity())) {
            try {
                List<RssItem> loadedItems = new LoadRssTask(getActivity()).execute(new RssReader(new SixMinuteRssStrategy())).get();
                for(RssItem loadedItem: loadedItems) {
                    if (items.containsKey(loadedItem.getRssId())) {
                        items.get(loadedItem.getRssId()).updateFrom(loadedItem);
                    } else {
                        items.put(loadedItem.getRssId(), loadedItem);
                    }
                }
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
        ListView listView = (ListView) getView().findViewById(R.id.rssItemsListView);
        RssItem item = (RssItem) listView.getAdapter().getItem(i);
        if (null != item) {
            if (null == item.getAudioLink()) {
                try {
                    String audioLink = parseAudioLinkFromHtml(item);
                    if (null != audioLink) {
                        item.setAudioLink(audioLink);
                        rssItemDAO.update(item);
                    }
                } catch (ParseHtmlException e) {
                    e.printStackTrace();
                    Log.e(TAG, String.format(getString(R.string.error_cant_load_or_parse), item.getLink()));
                    Toast.makeText(getActivity(), String.format(getString(R.string.error_cant_load_or_parse), item.getLink()), Toast.LENGTH_LONG);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error update rss item " + item.getTitle());
                }

            }

            if (null != item.getAudioLink() && !item.getAudioLink().isEmpty()) {
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

    private String parseAudioLinkFromHtml(RssItem item) throws ParseHtmlException {
        String audioLink = null;
        try {
            if (NetHelper.isOnline(getActivity())) {
                audioLink = new ParseAudioLinkFromHtmlTask(getActivity()).execute(item.getLink()).get();
            } else {
                Toast.makeText(getActivity(), R.string.error_u_havent_internet_connection, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new ParseHtmlException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new ParseHtmlException(e);
        }
        return audioLink;
    }
}
