package com.example.dsmapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.dsmapp.Gallery.FragmentGallery;
import com.example.dsmapp.Profile.FragmentProfile;
import com.example.dsmapp.Tasks.FragmentTasks;

public class ActivityHome extends AppCompatActivity{

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        token = getIntent().getExtras().getString("access_token");

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, FragmentGallery.newInstance(token, 0)).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFrag = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_gallery:
                            selectedFrag = FragmentGallery.newInstance(token, 0);
                            break;
                        case R.id.nav_tasks:
                            selectedFrag = FragmentTasks.newInstance(token, 0);
                            break;
                        case R.id.nav_profile:
                            selectedFrag = FragmentProfile.newInstance(token);
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, selectedFrag).commit();
                    return true;
                }
            };

}
