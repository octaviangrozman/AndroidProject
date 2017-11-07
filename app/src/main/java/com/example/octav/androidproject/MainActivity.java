package com.example.octav.androidproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.octav.androidproject.adapters.TripsAdapter;
import com.example.octav.androidproject.model.Trip;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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

        final ArrayList<Trip> tripsData = new ArrayList<>();
        ArrayList<String> stops = new ArrayList<>();
        stops.add("Horsens");
        stops.add("Aarhus");
        ArrayList<String> stops2 = new ArrayList<>();
        stops2.add("Copenhagen");
        stops2.add("Aalborg");

        tripsData.add(
                new Trip("HOR-AAR")
                        .setComplexity(4)
                        .setStops(stops));

        tripsData.add(
                new Trip("CPH-AAL")
                        .setComplexity(5)
                        .setStops(stops2));

        tripsList.setAdapter(new TripsAdapter(this, tripsData));
        tripsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = (Trip) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, TripActivity.class);
                intent.putExtra("title", trip.getTitle());
                intent.putExtra("stops", trip.getStops());
                intent.putExtra("complexity", trip.getComplexity());
                startActivity(intent);
            }
        });

    }

    public void goToAddTripActivity(View view) {
        Intent intent = new Intent(this, AddTripActivity.class);
        startActivity(intent);
    }
}
