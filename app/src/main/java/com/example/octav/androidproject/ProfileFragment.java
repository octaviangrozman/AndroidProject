package com.example.octav.androidproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.octav.androidproject.adapters.MyTripsAdapter;
import com.example.octav.androidproject.model.MyLatLng;
import com.example.octav.androidproject.model.Route;
import com.example.octav.androidproject.model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private TextView userEmailView;
    private TextView userTripCountView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase db;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        getActivity().setTitle(R.string.text_profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userEmailView = (TextView) getView().findViewById(R.id.userEmail);
        userTripCountView = (TextView) getView().findViewById(R.id.userTripCount);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        userEmailView.setText("Email: " + currentUser.getEmail());
        setUsersTripCount();
    }

    public void setUsersTripCount() {
        ValueEventListener tripsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    count++;
                }
                ProfileFragment.this.userTripCountView.setText("Trips created: " + count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "fetch Trips :onCancelled", databaseError.toException());
            }
        };
        Log.i("as", "attached listener");
        db.getReference("trips").orderByChild("userUid").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(tripsListener);
    }
}