package com.example.octav.androidproject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.octav.androidproject.model.MyLatLng;
import com.example.octav.androidproject.model.Route;
import com.example.octav.androidproject.model.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class TripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView tripTitle;
    private TextView tripDuration;
    private TextView tripStartPoint;
    private TextView tripEndPoint;
    private TextView tripComplexity;
    private TextView tripDescription;

    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initLayoutElements();

        Bundle bundle = getIntent().getExtras();
        currentTrip = (Trip) bundle.getSerializable("trip");

        populateLayoutElements(currentTrip);
    }

    private void populateLayoutElements(Trip trip) {
        tripTitle.setText(trip.getTitle());
        tripDuration.setText(String.valueOf(trip.getDuration()));
        tripComplexity.setText(String.valueOf(trip.getComplexity()));
        tripDescription.setText(trip.getDescription());
        tripStartPoint.setText(trip.getStops().toString());
//        tripEndPoint.setText(trip.getStops().get(trip.getStops().size() - 1));
    }

    private void initLayoutElements(){
        tripTitle = (TextView) findViewById(R.id.title);
        tripDuration = (TextView) findViewById(R.id.tripDuration);
        tripStartPoint = (TextView) findViewById(R.id.tripStartPoint);
        tripEndPoint = (TextView) findViewById(R.id.tripEndPoint);
        tripDescription = (TextView) findViewById(R.id.tripDescription);
        tripComplexity = (TextView) findViewById(R.id.tripComplexity);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRoute(currentTrip.getRoute());
    }

    private void drawRoute(Route route){
        PolylineOptions lineOptions = null;

        for (MyLatLng myLatLng: route.getMarkers()) {
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(myLatLng.getLatitude(), myLatLng.getLongtitude()));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }

        for (ArrayList<MyLatLng> listOfMyPoints: route.getPoints()) {
            lineOptions = new PolylineOptions();

            ArrayList<LatLng> points = new ArrayList<>();
            for(MyLatLng myLatLng: listOfMyPoints){
                points.add(new LatLng(myLatLng.getLatitude(), myLatLng.getLongtitude()));
            }

            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.GREEN);
        }

        if (lineOptions != null) {
            mMap.addPolyline(lineOptions);
        } else {
            Log.d("onPostExecute", "without Polylines drawn");
        }

        MyLatLng firstMarker = route.getMarkers().get(0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstMarker.getLatitude(), firstMarker.getLongtitude()), 11));

    }
}
