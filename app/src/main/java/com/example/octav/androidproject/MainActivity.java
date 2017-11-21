package com.example.octav.androidproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.octav.androidproject.adapters.MyTripsAdapter;
import com.example.octav.androidproject.model.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends BaseActivity implements
        HomeFragment.OnFragmentInteractionListener,
        TripFragment.OnFragmentInteractionListener,
        AddTripFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        MyTripsFragment.OnFragmentInteractionListener,
        EditTripFragment.OnFragmentInteractionListener,
        MyTripsAdapter.OnAdapterInteractionListener {

    // database
    FirebaseDatabase db;
    // UI
    ListView tripsList;
    // Fragments
    FragmentTransaction ft;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);

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

                            case R.id.action_my_trips:
                                MyTripsFragment myTripsFragment = new MyTripsFragment();
                                ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, myTripsFragment);
                                ft.addToBackStack("myTrips").commit();
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
    public void goToEditTripFragment(Trip trip) {
        EditTripFragment editTripFragment = EditTripFragment.newInstance(trip);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, editTripFragment);
        ft.addToBackStack("editTrip").commit();
    }

    @Override
    public void deleteTrip(String tripId) {
        db.getReference("trips").child(tripId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "Your trip has been successfully deleted!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void goToTripFragment(Trip trip) {
        TripFragment tripFragment = TripFragment.newInstance(trip);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, tripFragment);
        ft.addToBackStack("trip").commit();
    }

}
