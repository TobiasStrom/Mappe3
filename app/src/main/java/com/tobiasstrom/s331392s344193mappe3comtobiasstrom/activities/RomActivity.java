package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;

public class RomActivity extends AppCompatActivity {
    //private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


    }


}