package com.example.octav.androidproject;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripFragment extends Fragment implements OnMapReadyCallback {
    private static final String TRIP_PARAM = "trip";

    private Trip tripParam;

    private GoogleMap mMap;

    private TextView tripTitle;
    private TextView tripDuration;
    private TextView tripComplexity;
    private TextView tripDescription;

    private Trip currentTrip;

    private OnFragmentInteractionListener mListener;

    public TripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tripParam Parameter 1.
     * @return A new instance of fragment TripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripFragment newInstance(Trip tripParam) {
        TripFragment fragment = new TripFragment();
        Bundle args = new Bundle();
        args.putSerializable(TRIP_PARAM, tripParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tripParam = (Trip) getArguments().getSerializable(TRIP_PARAM);
            currentTrip = tripParam;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initLayoutElements();
        if (getArguments() != null) {
            getActivity().setTitle(currentTrip.getTitle());
            populateLayoutElements(currentTrip);
        }
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

    private void initLayoutElements(){
        tripTitle = (TextView) getView().findViewById(R.id.title);
        tripDuration = (TextView) getView().findViewById(R.id.tripDuration);
        tripDescription = (TextView) getView().findViewById(R.id.tripDescription);
        tripComplexity = (TextView) getView().findViewById(R.id.tripComplexity);
    }

    private void populateLayoutElements(Trip trip) {
        tripTitle.setText(trip.getTitle());
        tripDuration.setText(String.valueOf(trip.getDuration()));
        tripComplexity.setText(String.valueOf(trip.getComplexity()));
        tripDescription.setText(trip.getDescription());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        drawRoute(currentTrip.getRoute());
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
}
