package com.example.octav.androidproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.octav.androidproject.R;
import com.example.octav.androidproject.model.Trip;

import java.util.ArrayList;

/**
 * Created by Octav on 11/7/2017.
 */

public class TripsAdapter extends ArrayAdapter<Trip> {
    public TripsAdapter(Context context, ArrayList<Trip> data) {
        super(context, 0, data);
        Log.i("as", "created");
    }

    public static class ViewHolder {
        TextView title;
        TextView complexity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trip_item_layout, parent, false);

            viewHolder.title = (TextView) convertView.findViewById(R.id.tripTitle);
            viewHolder.complexity = (TextView) convertView.findViewById(R.id.tripComplexity);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Trip trip = getItem(position);

        viewHolder.title.setText(trip.getTitle());
        viewHolder.complexity.setText(String.valueOf(trip.getComplexity()));

        return convertView;
    }
}
