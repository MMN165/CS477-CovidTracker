package com.example.cs477_covidtracker;

import java.util.ArrayList;

/**
 * cardLocation is the object that stores the information on the current location, along with current cases and deaths. To be used in tangent with our custom adapter and recycle view.
 */
public class cardLocation {
    private int currentcase, currentDeath;
    private String  mCounty, mState;
    public ArrayList<int[]> history;
    public cardLocation(int cc, int cd, String county, String state){
        currentcase = cc;
        currentDeath = cd;
        mCounty = county;
        mState = state;
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
