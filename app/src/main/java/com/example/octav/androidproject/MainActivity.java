package com.example.octav.androidproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.octav.androidproject.adapters.TripsAdapter;
import com.example.octav.androidproject.model.MyLatLng;
import com.example.octav.androidproject.model.Route;
import com.example.octav.androidproject.model.Trip;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // database
    FirebaseDatabase db;
    // UI
    ListView tripsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance();
        tripsList = (ListView) findViewById(R.id.tripsList);

        fetchTrips();
        tripsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = (Trip) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
            }
        });

    }

    public void fetchTrips() {
        ValueEventListener tripsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Trip> trips = new ArrayList<>();
                for (DataSnapshot tripSnapshot: dataSnapshot.getChildren()) {
                    Trip trip = new Trip(tripSnapshot.child("title").getValue(String.class))
                            .setComplexity(tripSnapshot.child("complexity").getValue(Integer.class))
                            .setDescription(tripSnapshot.child("description").getValue(String.class))
                            .setDuration(tripSnapshot.child("duration").getValue(Integer.class))
                            .setStops((ArrayList<String>) (tripSnapshot.child("stops").getValue()));

                    GenericTypeIndicator<ArrayList<MyLatLng>> t = new GenericTypeIndicator<ArrayList<MyLatLng>>(){};
                    GenericTypeIndicator<ArrayList<ArrayList<MyLatLng>>> pointsType = new GenericTypeIndicator<ArrayList<ArrayList<MyLatLng>>>(){};
                    ArrayList<MyLatLng> markers = tripSnapshot.child("route").child("markers").getValue(t);
                    ArrayList<ArrayList<MyLatLng>> points = (tripSnapshot.child("route").child("points").getValue(pointsType));
                    trip.setRoute(new Route(points, markers));

                    trips.add(trip);
                }
                tripsList.setAdapter(new TripsAdapter(MainActivity.this, trips));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "fetch Trips :onCancelled", databaseError.toException());
            }
        };
        db.getReference("trips").addListenerForSingleValueEvent(tripsListener);
    }

    public void goToAddTripActivity(View view) {
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
    }
}
