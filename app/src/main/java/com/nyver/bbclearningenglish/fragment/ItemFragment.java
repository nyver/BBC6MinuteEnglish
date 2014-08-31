package com.nyver.bbclearningenglish.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.io.IOException;

public class ItemFragment extends Fragment {

    public static final String INDEX_AUDIO_POSITION = "audio_position";
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        Bundle bundle = getArguments();
        item = (RssItem) bundle.get(INDEX_ITEM);

        TextView title = (TextView) rootView.findViewById(R.id.itemTitle);
        title.setText(item.getTitle());

        TextView summary = (TextView) rootView.findViewById(R.id.itemSummary);
        summary.setText(item.getSummary());

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(item.getAudioLink());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.item, menu);

        MenuItem item = menu.findItem(R.id.action_play);
        if (null != item && null != mediaPlayer) {
            if (mediaPlayer.isPlaying()) {
                item.setIcon(R.drawable.ic_action_pause);
            }
        }
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch(id) {
            case R.id.action_play:
                if (null != mediaPlayer) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        item.setIcon(R.drawable.ic_action_pause);
                    } else {
                        mediaPlayer.pause();
                        item.setIcon(R.drawable.ic_action_play);
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mediaPlayer) {
            outState.putInt(INDEX_AUDIO_POSITION, mediaPlayer.getCurrentPosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (null != savedInstanceState && null != savedInstanceState.get(INDEX_AUDIO_POSITION) && null != mediaPlayer) {
            mediaPlayer.seekTo(savedInstanceState.getInt(INDEX_AUDIO_POSITION));
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                getActivity().supportInvalidateOptionsMenu();
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }
}
