package ru.elspirado.elspirado_app.elspirado_project.controller.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import ru.elspirado.elspirado_app.elspirado_project.R;
import ru.elspirado.elspirado_app.elspirado_project.controller.fragments.FragmentPreference;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static ru.elspirado.elspirado_app.elspirado_project.model.Utils.APP_PREFERENCES;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FragmentPreference fragmentPreference;

    private FragmentManager fragmentManager;

    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initUI();

        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        FloatingActionButton fab = findViewById(R.id.fab_settings);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(1, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(1, intent);
        finish();
        super.onBackPressed();
    }

    public void initUI() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.Settings_activity_title);

        BottomAppBar bar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bar);

        fragmentManager = getSupportFragmentManager();

        if (fragmentPreference == null) {
            fragmentPreference = new FragmentPreference();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.settingsContainer, fragmentPreference)
                    .show(fragmentPreference)
                    .commit();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}