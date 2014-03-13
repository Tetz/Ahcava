package com.ver1.avacha;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DressUpGirlActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_up_girl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_dress_up_girl, menu);
        return true;
    }
}
