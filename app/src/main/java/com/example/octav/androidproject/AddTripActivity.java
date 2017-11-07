package com.example.octav.androidproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.octav.androidproject.model.Trip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private FirebaseDatabase db;
    private DatabaseReference tripRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        db = FirebaseDatabase.getInstance();
        tripRef = db.getReference("trips");

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

                int hours;
                int minutes;
                try {
                     title = mTitle.getText().toString();
                     description = mDescription.getText().toString();
                    try {
                         hours = mHours.getText().toString().equals("")?
                                0 :
                                parseInt(mHours.getText().toString()) * 60;
                         minutes = mMinutes.getText().toString().equals("")?
                                0 :
                                parseInt(mHours.getText().toString()) * 60;
                         duration = hours + minutes;
                        if(duration == 0) {
                            Toast.makeText(AddTripActivity.this, "Duration was not specified", Toast.LENGTH_LONG).show();
                            return;
                        }
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

                Toast.makeText(AddTripActivity.this, "New trip created!", Toast.LENGTH_LONG).show();

                tripRef.push().setValue(trip);

                clearInputFields();
            }
        });
    }

    private void clearInputFields(){
        mTitle.setText("");
        mComplexity.setSelection(0);
        mHours.setText("");
        mMinutes.setText("");
        mDescription.setText("");
        mStops.setText("");
    }

}
