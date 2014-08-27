package com.nyver.bbclearningenglish.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.rss.model.RssItem;

import java.io.IOException;

public class ItemFragment extends Fragment {

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
