package com.example.cs477_covidtracker;

public class cardLocation {
    private int currentcase, currentDeath;
    private String mLocation, mCounty, mState;

    public cardLocation(int cc, int cd, String state, String location, String county){
        currentcase = cc;
        currentDeath = cd;
        mLocation = location;
        mCounty = county;
        mState = state;
    }

    public void changeLocation(String text){
        mLocation = text;
    }

    public String getLocation(){
        return mLocation;
    }

    public int getCurrentCase(){
        return currentcase;
    }

    public int getCurrentDeath(){
        return currentDeath;
    }

    public void setCurrentCases(int c){
        currentcase = c;
    }

    public void setCurrentDeath(int d){
        currentDeath = d;
    }
}
