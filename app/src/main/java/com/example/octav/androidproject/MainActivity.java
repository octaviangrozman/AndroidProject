package com.example.octav.androidproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.octav.androidproject.model.Trip;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity implements
        HomeFragment.OnFragmentInteractionListener,
        TripFragment.OnFragmentInteractionListener,
        AddTripFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener {

    // database
    FirebaseDatabase db;
    // UI
    ListView tripsList;
    // Fragments
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        ft = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        ft.replace(R.id.fragment_container, homeFragment);
        ft.commit();
        addBottomNavigation();
    }

    public void addBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_trips:
                                HomeFragment homeFragment = new HomeFragment();
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, homeFragment);
                                ft.addToBackStack("home").commit();
                                return true;

                            case R.id.action_search:
                                SearchFragment searchFragment = new SearchFragment();
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, searchFragment);
                                ft.addToBackStack("search").commit();
                                return true;

                            case R.id.action_profile:
                                ProfileFragment profileFragment = new ProfileFragment();
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, profileFragment);
                                ft.addToBackStack("profile").commit();
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public void goToAddTripFragment() {
        AddTripFragment addTripFragment = AddTripFragment.newInstance();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, addTripFragment);
        ft.addToBackStack("addTrip").commit();
    }

    @Override
    public void goToTripFragment(Trip trip) {
        TripFragment tripFragment = TripFragment.newInstance(trip);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, tripFragment);
        ft.addToBackStack("trip").commit();
    }
}
