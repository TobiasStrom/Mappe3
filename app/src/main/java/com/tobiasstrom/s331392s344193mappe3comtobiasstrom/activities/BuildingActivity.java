package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;

public class BuildingActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.builiding_info);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Bygning");
    }


}
