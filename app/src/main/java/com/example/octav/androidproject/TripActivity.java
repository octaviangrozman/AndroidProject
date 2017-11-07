package com.example.octav.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class TripActivity extends AppCompatActivity {

//    TextView titleLabel;
    TextView titleTextView;
//    TextView complexityLabel;
//    TextView complexityTextView;
//    TextView startPointLabel;
//    TextView startPointTextView;
//    TextView endPointLabel;
//    TextView endPointTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

//        titleLabel = (TextView) findViewById(R.id.titleLabel);
        titleTextView = (TextView) findViewById(R.id.title);
//        complexityLabel = (TextView) findViewById(R.id.complexityLabel);
//        complexityTextView = (TextView) findViewById(R.id.complexity);
//        startPointLabel = (TextView) findViewById(R.id.startPointLabel);
//        startPointTextView = (TextView) findViewById(R.id.startPoint);
//        endPointLabel = (TextView) findViewById(R.id.endPointLabel);
//        endPointTextView = (TextView) findViewById(R.id.endPoint);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        Integer complexity = bundle.getInt("complexity");
        ArrayList<String> stops = bundle.getStringArrayList("stops");

        titleTextView.setText(title);
//        complexityTextView.setText(String.valueOf(complexity));
//        startPointTextView.setText(stops.get(0));
//        endPointTextView.setText(stops.get(stops.size() - 1));

    }
}
