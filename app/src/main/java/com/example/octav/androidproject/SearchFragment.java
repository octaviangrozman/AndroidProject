package com.example.octav.androidproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octav.androidproject.adapters.TripsAdapter;
import com.example.octav.androidproject.model.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private EditText searchBar;
    // database
    FirebaseDatabase db;
    // UI
    ListView tripsList;
    ArrayList<Trip> trips;
    private TextView loader;
    private TextView noDataText;

    private SearchFragment.OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Search for trips");
        db = FirebaseDatabase.getInstance();
        trips = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureSearchBar();
        tripsList = (ListView) getView().findViewById(R.id.tripsList);
        loader = (TextView) getView().findViewById(R.id.loader);
        noDataText = (TextView) getView().findViewById(R.id.noDataText);
        tripsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = (Trip) parent.getItemAtPosition(position);
                mListener.goToTripFragment(trip);
            }
        });
    }

    public void configureSearchBar() {
        searchBar = (EditText) getView().findViewById(R.id.searchBar);
        searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String searchValue = searchBar.getText().toString();
                    searchForTrips(searchValue);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (SearchFragment.OnFragmentInteractionListener) context;
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
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
        void goToTripFragment(Trip trip);
    }

    public void searchForTrips(final String searchValue) {
        trips = new ArrayList<>();
        tripsList.setAdapter(new TripsAdapter(getActivity(), trips));
        noDataText.setVisibility(View.GONE);
        setLoading(true);
        ValueEventListener tripsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tripSnapshot: dataSnapshot.getChildren()) {
                    Trip trip = new Trip(tripSnapshot.child("title").getValue(String.class))
                            .setComplexity(tripSnapshot.child("complexity").getValue(Integer.class))
                            .setDescription(tripSnapshot.child("description").getValue(String.class))
                            .setDuration(tripSnapshot.child("duration").getValue(Integer.class));

                    trips.add(trip);
                }
                ArrayList<Trip> foundTrips = new ArrayList<>();
                String text = searchValue.toLowerCase();
                for (Trip trip: trips) {
                    if (trip.getTitle().toLowerCase().contains(text) ||
                        text.contains(String.valueOf(trip.getComplexity())) ||
                        trip.getDescription().toLowerCase().contains(text) ||
                        text.contains(String.valueOf(trip.getDuration()))
                    )
                    {
                        foundTrips.add(trip);
                    }
                }
                setLoading(false);
                if (foundTrips.size() > 0) tripsList.setAdapter(new TripsAdapter(getActivity(), foundTrips));
                else {
                    Toast.makeText(getContext(), "No trips found", Toast.LENGTH_LONG).show();
                    noDataText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setLoading(false);
                Log.w("ERROR", "fetch Trips :onCancelled", databaseError.toException());
            }
        };
        db.getReference("trips").addListenerForSingleValueEvent(tripsListener);
    }

    public void setLoading(boolean _loading) {
        loader.setVisibility(_loading ? View.VISIBLE : View.GONE);
    }

}
