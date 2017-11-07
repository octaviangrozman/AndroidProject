package com.example.octav.androidproject.model;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

/**
 * Created by lenovo on 11/7/2017.
 */

public class Trip {

    private String title;

    private int complexity;

    private String description;

    private ArrayList<String> stops;

    private double duration;

    //CONSTRUCTORS
    public Trip() {

    }

    public Trip(String title) {
        this.title = title;
    }

    //GETTERS
    public String getTitle() {
        return title;
    }

    public int getComplexity() {
        return complexity;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getStops() {
        return stops;
    }

    public double getDuration() {
        return duration;
    }

    //SETTER WITH BUILDING FUNCTIONALITY
    public Trip setTitle(String title) {
        this.title = title;
        return this;
    }

    public Trip setComplexity(int complexity) {
        this.complexity = complexity;
        return this;
    }

    public Trip setDescription(String description) {
        this.description = description;
        return this;
    }

    public Trip setStops(ArrayList<String> stops) {
        this.stops = stops;
        return this;
    }

    public Trip setDuration(double duration) {
        this.duration = duration;
        return this;
    }
}
