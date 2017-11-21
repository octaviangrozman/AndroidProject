package com.example.octav.androidproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octav.androidproject.adapters.MyTripsAdapter;
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

import static com.example.octav.androidproject.R.id.addTripButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTripsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTripsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTripsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseDatabase db;

    private OnFragmentInteractionListener mListener;
    private ListView tripsListView;
    private TextView loader;
    private TextView noDataText;
    private FloatingActionButton addTripButton;
    private Button editBtn;
    private Button deleteBtn;

    public MyTripsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment MyTripsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTripsFragment newInstance() {
        MyTripsFragment fragment = new MyTripsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseDatabase.getInstance();
        getActivity().setTitle("My Trips");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripsListView = (ListView) getView().findViewById(R.id.tripsList);
        loader = (TextView) getView().findViewById(R.id.loader);
        noDataText = (TextView) getView().findViewById(R.id.noDataText);
        editBtn= (Button) getView().findViewById(R.id.edit);
        deleteBtn= (Button) getView().findViewById(R.id.delete);

        tripsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip trip = (Trip) parent.getItemAtPosition(position);
                mListener.goToTripFragment(trip);
            }
        });

//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View parentRow = (View) v.getParent();
//                ListView listView = (ListView) parentRow.getParent();
//                final int position = listView.getPositionForView(parentRow);
//                mListener.goToEditTripFragment((Trip)listView.getAdapter().getItem(position));
//            }
//        });
//
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                View parentRow = (View) v.getParent();
//                ListView listView = (ListView) parentRow.getParent();
//                final int position = listView.getPositionForView(parentRow);
//                deleteTrip((Trip)listView.getAdapter().getItem(position));
//            }
//        });

        addTripButton = (FloatingActionButton) getView().findViewById(R.id.addTripButton);
        addTripButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.goToAddTripFragment();
            }
        });

    }

    public void editBtnClickHandler(View view){
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        mListener.goToEditTripFragment((Trip)listView.getAdapter().getItem(position));
    }

    public void deleteBtnClickHandler(View view){
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        deleteTrip((Trip)listView.getAdapter().getItem(position));
    }

    private void deleteTrip(Trip item) {
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
                    MyTripsAdapter myTripsAdapter = new MyTripsAdapter(getContext(), tripsArray);
                    myTripsAdapter.notifyDataSetChanged();
                    tripsListView.setAdapter(myTripsAdapter);
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

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

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
        // TODO: Update argument type and name
        void goToEditTripFragment(Trip trip);
        void goToAddTripFragment();
        void goToTripFragment(Trip trip);
    }
}
