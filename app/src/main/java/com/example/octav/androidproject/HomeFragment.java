package com.example.octav.androidproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octav.androidproject.adapters.TripsAdapter;
import com.example.octav.androidproject.model.MyLatLng;
import com.example.octav.androidproject.model.Route;
import com.example.octav.androidproject.model.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // database
    FirebaseDatabase db;
    // Fragments
    FragmentTransaction ft;
    // UI
    ListView tripsListView;
    FloatingActionButton addTripButton;
    private TextView loader;
    private TextView noDataText;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void onAttach(Context context){
        if (context instanceof TripFragment.OnFragmentInteractionListener) {
            mListener = (HomeFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.text_trips);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance();
        getActivity().setTitle(R.string.text_trips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tripsListView = (ListView) getView().findViewById(R.id.tripsList);
        loader = (TextView) getView().findViewById(R.id.loader);
        noDataText = (TextView) getView().findViewById(R.id.noDataText);
        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = (Trip) parent.getItemAtPosition(position);
                mListener.goToTripFragment(trip);
            }
        });
        addTripButton = (FloatingActionButton) getView().findViewById(R.id.addTripButton);
        addTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.goToAddTripFragment();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchTrips();
    }

    public void fetchTrips() {
        tripsListView.setVisibility(View.GONE);
        noDataText.setVisibility(View.GONE);
        setLoading(true);
        ValueEventListener tripsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Trip> tripsArray = new ArrayList<>();
                for (DataSnapshot tripSnapshot: dataSnapshot.getChildren()) {
                    Trip trip = new Trip(tripSnapshot.child("title").getValue(String.class))
                            .setKey(tripSnapshot.getKey())
                            .setComplexity(tripSnapshot.child("complexity").getValue(Integer.class))
                            .setDescription(tripSnapshot.child("description").getValue(String.class))
                            .setDuration(tripSnapshot.child("duration").getValue(Integer.class));

                    GenericTypeIndicator<ArrayList<MyLatLng>> t = new GenericTypeIndicator<ArrayList<MyLatLng>>(){};
                    GenericTypeIndicator<ArrayList<ArrayList<MyLatLng>>> pointsType = new GenericTypeIndicator<ArrayList<ArrayList<MyLatLng>>>(){};
                    ArrayList<MyLatLng> markers = tripSnapshot.child("route").child("markers").getValue(t);
                    ArrayList<ArrayList<MyLatLng>> points = (tripSnapshot.child("route").child("points").getValue(pointsType));
                    trip.setRoute(new Route(points, markers));

                    tripsArray.add(trip);
                }
                setLoading(false);
                if (tripsArray.size() > 0) {
                    TripsAdapter tripsAdapter = new TripsAdapter(getContext(), tripsArray);
                    tripsAdapter.notifyDataSetChanged();
                    tripsListView.setAdapter(tripsAdapter);
                    tripsListView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), "No trips found", Toast.LENGTH_LONG).show();
                    noDataText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "fetch Trips :onCancelled", databaseError.toException());
            }
        };
        Log.i("as", "attached listener");
        db.getReference("trips").addListenerForSingleValueEvent(tripsListener);
    }

    public void setLoading(boolean _loading) {
        loader.setVisibility(_loading ? View.VISIBLE : View.GONE);
    }

    public interface OnFragmentInteractionListener {
        void goToTripFragment(Trip trip);
        void goToEditTripFragment(Trip trip);
        void goToAddTripFragment();
    }

}
