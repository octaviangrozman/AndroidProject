package com.example.octav.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.octav.androidproject.model.Trip;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

    private TextView tripTitle;
    private TextView tripDuration;
    private TextView tripStartPoint;
    private TextView tripEndPoint;
    private TextView tripComplexity;
    private TextView tripDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initLayoutElements();

        Bundle bundle = getIntent().getExtras();
        Trip trip = (Trip)bundle.getSerializable("trip");

        populateLayoutElements(trip);
    }

    private void populateLayoutElements(Trip trip) {
        tripTitle.setText(trip.getTitle());
        tripDuration.setText(String.valueOf(trip.getDuration()));
        tripComplexity.setText(String.valueOf(trip.getComplexity()));
        tripDescription.setText(trip.getDescription());
        tripStartPoint.setText(trip.getStops().get(0));
        tripEndPoint.setText(trip.getStops().get(trip.getStops().size() - 1));
    }

    private void initLayoutElements(){
        tripTitle = (TextView) findViewById(R.id.title);
        tripDuration = (TextView) findViewById(R.id.tripDuration);
        tripStartPoint = (TextView) findViewById(R.id.tripStartPoint);
        tripEndPoint = (TextView) findViewById(R.id.tripEndPoint);
        tripDescription = (TextView) findViewById(R.id.tripDescription);
        tripComplexity = (TextView) findViewById(R.id.tripComplexity);
    }
}
