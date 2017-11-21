package com.example.octav.androidproject;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTripFragment extends Fragment implements OnMapReadyCallback {

    private EditText mTitle;
    private Spinner mComplexity;
    private EditText mHours;
    private EditText mMinutes;
    private EditText mDescription;
    private Button updateTripButton;
    private Button routeBtn;
    private Button clearRouteBtn;

    private FirebaseDatabase db;
    private FirebaseAuth mAuth;
    private DatabaseReference tripRef;

    private GoogleMap mMap;
    private ArrayList<LatLng> MarkerPoints = new ArrayList<>();
    private ArrayList<ArrayList<LatLng>> points = new ArrayList<>();

    private static final String TRIP_PARAM = "trip";

    private Trip tripParam;

    private OnFragmentInteractionListener mListener;

    public EditTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tripParam Parameter 1.
     * @return A new instance of fragment EditTripFragment.
     */
    public static EditTripFragment newInstance(Trip tripParam) {
        EditTripFragment fragment = new EditTripFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRIP_PARAM, tripParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        tripRef = db.getReference("trips");
        getActivity().setTitle(R.string.text_edit_trip_title);

        if (getArguments() != null) {
            tripParam = (Trip) getArguments().getSerializable(TRIP_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTitle = getView().findViewById(R.id.title);
        mComplexity = getView().findViewById(R.id.complexity);
        mHours = getView().findViewById(R.id.hours);
        mMinutes = getView().findViewById(R.id.minutes);
        mDescription = getView().findViewById(R.id.description);
        updateTripButton = getView().findViewById(R.id.updateTrip);
        routeBtn = getView().findViewById(R.id.route);
        clearRouteBtn = getView().findViewById(R.id.clear);

        updateTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String title;
                String description;
                int duration;
                String complexity;
                Route route;

                String tripId = tripParam.getKey();

                ArrayList<ArrayList<MyLatLng>> myPoints = new ArrayList<ArrayList<MyLatLng>>();
                for(ArrayList<LatLng> listOfPoints: points){
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
                            Toast.makeText(getContext(), "Duration was not specified", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Hours or Minutes have wrong format", Toast.LENGTH_LONG).show();
                        return;
                    }
                    complexity = mComplexity.getSelectedItem().toString();
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "Some field are empty", Toast.LENGTH_LONG).show();
                    return;
                }

                Trip trip = new Trip()
                        .setTitle(title)
                        .setDescription(description)
                        .setDuration(duration)
                        .setComplexity(parseInt(complexity))
                        .setRoute(route);

                tripRef.child(tripId).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Your trip has been successfully updated!", Toast.LENGTH_LONG).show();
                    }
                });

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

        initializeTripData();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap = googleMap;
        drawRoute(tripParam.getRoute());

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
            ArrayList<LatLng> _points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                _points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    _points.add(position);
                }

                // Adding all the points in the route to LineOptions
                points.add(_points);
                lineOptions.addAll(_points);
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

    private void initializeTripData() {
        mTitle.setText(tripParam.getTitle());
        mHours.setText(String.valueOf(tripParam.getDuration() / 60));
        mMinutes.setText(String.valueOf(tripParam.getDuration() % 60));
        mDescription.setText(tripParam.getDescription());
    }

    private void clearInputFields(){
        mTitle.setText("");
        mComplexity.setSelection(0);
        mHours.setText("");
        mMinutes.setText("");
        mDescription.setText("");
    }

    private void drawRoute(Route route){
        PolylineOptions lineOptions = null;

        if (route.getMarkers() != null) {

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
