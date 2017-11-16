package com.example.octav.androidproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.octav.androidproject.model.MyLatLng;
import com.example.octav.androidproject.model.Route;
import com.example.octav.androidproject.model.Trip;
import com.example.octav.androidproject.util.DataParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.duration;
import static java.lang.Integer.parseInt;

public class AddTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText mTitle;
    private Spinner mComplexity;
    private EditText mHours;
    private EditText mMinutes;
    private EditText mDescription;
    private EditText mStops;
    private Button createTripButton;
    private Button routeBtn;
    private Button clearRouteBtn;

    private FirebaseDatabase db;
    private DatabaseReference tripRef;

    private GoogleMap mMap;
    private ArrayList<LatLng> MarkerPoints = new ArrayList<>();
    private ArrayList<ArrayList<LatLng>> points = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = FirebaseDatabase.getInstance();
        tripRef = db.getReference("trips");

        mTitle = (EditText) findViewById(R.id.title);
        mComplexity = (Spinner) findViewById(R.id.complexity);
        mHours = (EditText) findViewById(R.id.hours);
        mMinutes = (EditText) findViewById(R.id.minutes);
        mDescription = (EditText) findViewById(R.id.description);
        mStops = (EditText) findViewById(R.id.stops);
        createTripButton = (Button) findViewById(R.id.createTrip);
        routeBtn = (Button) findViewById(R.id.route);
        clearRouteBtn = (Button) findViewById(R.id.clear);

        createTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title;
                String description;
                int duration;
                String complexity;
                ArrayList<String> stops;
                Route route;

                ArrayList<ArrayList<MyLatLng>> myPoints = new ArrayList<ArrayList<MyLatLng>>();
                for(ArrayList<LatLng> listOfPoints: AddTripActivity.this.points){
                    myPoints.add(MyLatLng.convert(listOfPoints));
                }
                route = new Route(myPoints, MyLatLng.convert(MarkerPoints));

                int hours;
                int minutes;
                try {
                    title = mTitle.getText().toString();
                    description = mDescription.getText().toString();
                    try {
                        hours = mHours.getText().toString().equals("") ?
                                0 :
                                parseInt(mHours.getText().toString()) * 60;
                        minutes = mMinutes.getText().toString().equals("") ?
                                0 :
                                parseInt(mHours.getText().toString()) * 60;
                        duration = hours + minutes;
                        if (duration == 0) {
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
                } catch (NullPointerException e) {
                    Toast.makeText(AddTripActivity.this, "Some field are empty", Toast.LENGTH_LONG).show();
                    return;
                }

                Trip trip = new Trip()
                        .setTitle(title)
                        .setDescription(description)
                        .setDuration(duration)
                        .setStops(stops)
                        .setComplexity(parseInt(complexity))
                        .setRoute(route);

                Toast.makeText(AddTripActivity.this, "New trip created!", Toast.LENGTH_LONG).show();

                tripRef.push().setValue(trip);

                clearInputFields();
            }
        });

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LatLng origin = MarkerPoints.get(0);
//                LatLng waypoint = MarkerPoints.get(1);
//                LatLng dest = MarkerPoints.get(2);

                // Getting URL to the Google Directions API
                String url = getUrl(MarkerPoints);
                Log.d("onMapClick", url.toString());
                FetchUrl FetchUrl = new FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(MarkerPoints.get(0)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        });

        clearRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerPoints.clear();
                mMap.clear();
            }
        });
    }

    private void clearInputFields() {
        mTitle.setText("");
        mComplexity.setSelection(0);
        mHours.setText("");
        mMinutes.setText("");
        mDescription.setText("");
        mStops.setText("");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
//                if (MarkerPoints.size() > 2) {
//                    MarkerPoints.clear();
//                    mMap.clear();
//                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                //if (MarkerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                } else if (MarkerPoints.size() == 2) {
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//
//                }
//                else if(MarkerPoints.size() == 3){
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }


                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
//                if (MarkerPoints.size() >= 3) {
//                    LatLng origin = MarkerPoints.get(0);
//                    LatLng waypoint = MarkerPoints.get(1);
//                    LatLng dest = MarkerPoints.get(2);
//
//                    // Getting URL to the Google Directions API
//                    String url = getUrl(origin, dest, waypoint);
//                    Log.d("onMapClick", url.toString());
//                    FetchUrl FetchUrl = new FetchUrl();
//
//                    // Start downloading json data from Google Directions API
//                    FetchUrl.execute(url);
//                    //move map camera
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//                }

            }
        });
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-35.016, 143.321), 8));


    }

    private String getUrl(ArrayList<LatLng> waypoints) {

        // Origin of route
        String str_origin = "origin=" + waypoints.get(0).latitude + "," + waypoints.get(0).longitude;

        // Destination of route
        LatLng destination = waypoints.get(waypoints.size() - 1);
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;

        // Waypoints
        StringBuilder waypointsStringBuilder = new StringBuilder("waypoints=");
        for (int i = 1; i < waypoints.size() - 1; i++) {
            waypointsStringBuilder.append(waypoints.get(i).latitude)
                    .append(",")
                    .append(waypoints.get(i).longitude)
                    .append("|");
        }
        waypointsStringBuilder.deleteCharAt(waypointsStringBuilder.length() - 1);
        // Sensor enabled
        //String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + waypointsStringBuilder.toString();
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d("URL", url);

        return url;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                AddTripActivity.this.points.add(points);
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

}
