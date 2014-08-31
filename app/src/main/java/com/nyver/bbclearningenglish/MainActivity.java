package com.nyver.bbclearningenglish;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.nyver.bbclearningenglish.fragment.MainFragment;

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
}
