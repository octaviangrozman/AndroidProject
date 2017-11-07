package com.example.octav.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.octav.androidproject.model.Trip;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.duration;
import static java.lang.Integer.parseInt;

public class AddTripActivity extends AppCompatActivity {

    private EditText mTitle;
    private Spinner mComplexity;
    private EditText mHours;
    private EditText mMinutes;
    private EditText mDescription;
    private EditText mStops;
    private Button createTripButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        mTitle = (EditText) findViewById(R.id.title);
        mComplexity = (Spinner) findViewById(R.id.complexity);
        mHours = (EditText) findViewById(R.id.hours);
        mMinutes = (EditText) findViewById(R.id.minutes);
        mDescription = (EditText) findViewById(R.id.description);
        mStops = (EditText) findViewById(R.id.stops);
        createTripButton = (Button) findViewById(R.id.createTrip);

        createTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title;
                String description;
                int duration;
                String complexity;
                ArrayList<String> stops;
                try {
                     title = mTitle.getText().toString();
                     description = mDescription.getText().toString();
                    try {
                         duration = (parseInt(mHours.getText().toString()) / 60) + parseInt(mMinutes.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(AddTripActivity.this, "Hours or Minutes have wrong format", Toast.LENGTH_LONG).show();
                        return;
                    }
                    complexity = mComplexity.getSelectedItem().toString();
                    String[] stopsArray = mStops.getText().toString().split(",");
                    stops = new ArrayList<>(Arrays.asList(stopsArray));
                }
                catch (NullPointerException e){
                    Toast.makeText(AddTripActivity.this, "Some field are empty", Toast.LENGTH_LONG).show();
                    return;
                }

                Trip trip = new Trip()
                        .setTitle(title)
                        .setDescription(description)
                        .setDuration(duration)
                        .setStops(stops)
                        .setComplexity(parseInt(complexity));

                Toast.makeText(AddTripActivity.this, trip.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
