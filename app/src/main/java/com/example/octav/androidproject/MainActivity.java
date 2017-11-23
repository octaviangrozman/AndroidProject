package com.example.octav.androidproject;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
    Fragment currentFragment;


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
        ((TextView) findViewById(R.id.action_current_user)).setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        db = FirebaseDatabase.getInstance();

        if(savedInstanceState == null){
            this.currentFragment = HomeFragment.newInstance();
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, currentFragment);
            ft.commit();
        }

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
                                HomeFragment homeFragment = HomeFragment.newInstance();
                                goToFragment(HomeFragment.newInstance(), "home");
                                return true;

                            case R.id.action_my_trips:
                                goToFragment(MyTripsFragment.newInstance(), "myTrips");
                                return true;

                            case R.id.action_search:
                                goToFragment(SearchFragment.newInstance(), "search");
                                return true;

                            case R.id.action_profile:
                                goToFragment(new ProfileFragment(), "profile");
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    public void goToAddTripFragment() {
        goToFragment(AddTripFragment.newInstance(), "addTrip");
    }

    @Override
    public void goToEditTripFragment(Trip trip) {
        goToFragment(EditTripFragment.newInstance(trip), "editTrip");
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
        goToFragment(TripFragment.newInstance(trip), "trips");
    }

    private void goToFragment(Fragment fragment, String contextString){
        currentFragment = fragment;
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if(contextString != null)
            ft.addToBackStack(contextString).commit();
    }
}
