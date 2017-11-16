package com.example.octav.androidproject.model;

import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lenovo on 11/16/2017.
 */

public class MyLatLng implements Serializable {

    private double latitude;
    private double longtitude;

    public MyLatLng() {
    }

    public MyLatLng(double latitude, double longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    @Override
    public String toString() {
        return "MyLatLng{" +
                "latitude=" + latitude +
                ", longtitude=" + longtitude +
                '}';
    }


    public static ArrayList<MyLatLng> convert(ArrayList<LatLng> points){
        ArrayList<MyLatLng> myPoints = new ArrayList<MyLatLng>();

        for(LatLng point: points){
            myPoints.add(new MyLatLng(point.latitude, point.longitude));
        }

        return myPoints;
    }
}
