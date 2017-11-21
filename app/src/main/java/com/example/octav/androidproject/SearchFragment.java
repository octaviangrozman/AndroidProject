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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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

import java.lang.reflect.Array;
import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private EditText searchBar;
    // database
    FirebaseDatabase db;
    // UI
    ListView tripsListView;
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
    }

    public void configureSearchBar() {
        searchBar = (EditText) getView().findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.i("actionId", String.valueOf(actionId));
                    Log.i("textView", v.toString());
                    String searchValue = searchBar.getText().toString();
                    Log.i("value", String.valueOf(searchValue));
                    searchForTrips(searchValue);
                    return true;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchForTrips(event.getCharacters());
                    return true;
                }
                return false;
            }
        });
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
        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final boolean searchIconTouched = event.getX() > (v.getWidth() - 120);
                if (searchIconTouched) {
                    String searchValue = searchBar.getText().toString();
                    searchForTrips(searchValue);
                    return true;
                }
                Log.i("asd", event.toString());
                return false;
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
                Log.i("found", tripsArray.toString());
                ArrayList<Trip> foundTrips = new ArrayList<>();
                String text = searchValue.toLowerCase();
                Log.i("val", text);
                for (Trip trip: tripsArray) {
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
                if (foundTrips.size() > 0) {
                    TripsAdapter tripsAdapter = new TripsAdapter(getContext(), foundTrips);
                    tripsAdapter.notifyDataSetChanged();
                    tripsListView.setAdapter(tripsAdapter);
                    tripsListView.setVisibility(View.VISIBLE);
                }
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
