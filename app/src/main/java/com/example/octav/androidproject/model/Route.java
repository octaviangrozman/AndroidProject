package com.example.octav.androidproject.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 11/14/2017.
 */

public class Route implements Serializable{

    private ArrayList<ArrayList<MyLatLng>> points;
    private ArrayList<MyLatLng> markers;

    public Route(ArrayList<ArrayList<MyLatLng>> points, ArrayList<MyLatLng> markers) {
        this.points = points;
        this.markers = markers;
    }

    public Route() {

    }

    public ArrayList<ArrayList<MyLatLng>> getPoints() {
        return points;
    }

    public ArrayList<MyLatLng> getMarkers() {
        return markers;
    }

    public void setPoints(ArrayList<ArrayList<MyLatLng>> points) {
        this.points = points;
    }

    public void setMarkers(ArrayList<MyLatLng> markers) {
        this.markers = markers;
    }

    @Override
    public String toString() {
        return "Route{" +
                "points=" + points +
                ", markers=" + markers +
                '}';
    }
}
