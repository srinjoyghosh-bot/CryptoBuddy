package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class DefaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        getSupportFragmentManager().beginTransaction().replace(R.id.default_view,new HomeFragment()).commit();
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment selectedFragment=null;
                switch(item.getItemId())
                {
                    case R.id.home_frag:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.search_frag:
                        selectedFragment=new SearchFragment();
                        break;
                    case R.id.fav_frag:
                        selectedFragment=new FavouritesFragment();
                        break;
                    case R.id.targets_frag:
                        selectedFragment=new TargetsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.default_view,selectedFragment).commit();
                return true;
            }
        });
    }
}