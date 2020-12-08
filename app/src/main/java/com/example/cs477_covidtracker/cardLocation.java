package com.example.cs477_covidtracker;

import java.util.ArrayList;

public class cardLocation {
    private int currentcase, currentDeath;
    private String mLocation, mCounty, mState;
    public ArrayList<int[]> history;
    public cardLocation(int cc, int cd, String county, String state){
        currentcase = cc;
        currentDeath = cd;
        //mLocation = location;
        mCounty = county;
        mState = state;
    }

    public void changeLocation(String text){
       // mLocation = text;
        mCounty = text;
    }

    public String getLocation(){
        return "" + mCounty + ", " + mState; //mLocation;
    }

    public String getCounty(){
        return mCounty;
    }
    public String getState(){
        return mState;
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
