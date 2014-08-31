package com.nyver.bbclearningenglish.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nyver.bbclearningenglish.R;
import com.nyver.bbclearningenglish.db.DatabaseHelperFactory;
import com.nyver.bbclearningenglish.exception.DownloadFileException;
import com.nyver.bbclearningenglish.rss.model.RssItem;
import com.nyver.bbclearningenglish.task.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ItemFragment extends Fragment {

    private static final String TAG = ItemFragment.class.getSimpleName();

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

        try {
            mediaPlayer = new MediaPlayer();
            if (null != item.getLocalAudioLink()) {
                File file = new File(item.getLocalAudioLink());
                if (file.exists()) {
                    mediaPlayer.setDataSource(item.getLocalAudioLink());
                    Toast.makeText(getActivity(), item.getLocalAudioLink(), Toast.LENGTH_LONG).show();
                } else {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(item.getAudioLink());
                }
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(item.getAudioLink());
            }
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
        if (null != item) {
            if (null != mediaPlayer && mediaPlayer.isPlaying()) {
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
            case R.id.action_download:
                try {
                    downloadAudioFile();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this.item.getLocalAudioLink());
                    mediaPlayer.prepare();
                } catch (DownloadFileException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getActivity(), R.string.error_cant_download_file, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
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

    private void downloadAudioFile() throws DownloadFileException {
        if (null != item) {
            try {
                String localAudioFile = new DownloadTask(getActivity()).execute(item).get();
                if (null != localAudioFile && !localAudioFile.isEmpty()) {
                    item.setLocalAudioLink(localAudioFile);
                    DatabaseHelperFactory.getHelper().getRssItemDAO().update(item);
                }
            } catch (InterruptedException e) {
                throw new DownloadFileException(e);
            } catch (ExecutionException e) {
                throw new DownloadFileException(e);
            } catch (SQLException e) {
                throw new DownloadFileException(e);
            }
        }
    }
}
