package com.example.cs477_covidtracker.ui.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cs477_covidtracker.LocationDetailsSearchActivity;
import com.example.cs477_covidtracker.R;
import com.example.cs477_covidtracker.ui.home.LocationDetailsActivity;
import com.example.cs477_covidtracker.ui.home.jsonParser;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String COUNTY = "com.example.cs477_covidtracker.COUNTYPASS";
    public static final String STATE = "com.example.cs477_covidtracker.STATEPASS";
    public static final String LOCATIONCUR = "com.example.cs477_covidtracker.LOCATIONCUR2";
    private DashboardViewModel dashboardViewModel;
    ArrayList<String> states;
    Spinner stateSpinner;
    ListView listView;

    String selectedState;
    String selectedCounty;

    ArrayAdapter stateAdapter;
    ArrayAdapter listAdapter;

    HashMap<String, ArrayList<String>> map;
    jsonParser jsonParser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        stateSpinner = (Spinner) root.findViewById(R.id.stateSpinner);
        listView = (ListView) root.findViewById(R.id.listView);

        states = new ArrayList<String>();
        map = new HashMap<>();

        jsonParser = new jsonParser();

        new Async().execute();
        // ?

        states.add("Alabama");
        states.add("Alaska");
        states.add("Arizona");
        states.add("Arkansas");
        states.add("California");
        states.add("Colorado");
        states.add("Connecticut");
        states.add("Delaware");
        states.add("Florida");
        states.add("Georgia");
        states.add("Hawaii");
        states.add("Idaho");
        states.add("Illinois");
        states.add("Indiana");
        states.add("Iowa");
        states.add("Kansas");
        states.add("Kentucky");
        states.add("Louisiana");
        states.add("Maine");
        states.add("Maryland");
        states.add("Massachusetts");
        states.add("Michigan");
        states.add("Minnesota");
        states.add("Mississippi");
        states.add("Missouri");
        states.add("Montana");
        states.add("Nebraska");
        states.add("Nevada");
        states.add("New Hampshire");
        states.add("New Jersey");
        states.add("New Mexico");
        states.add("New York");
        states.add("North Carolina");
        states.add("North Dakota");
        states.add("Ohio");
        states.add("Oklahoma");
        states.add("Oregon");
        states.add("Pennsylvania");
        states.add("Rhode Island");
        states.add("South Carolina");
        states.add("South Dakota");
        states.add("Tennessee");
        states.add("Texas");
        states.add("Utah");
        states.add("Vermont");
        states.add("Virginia");
        states.add("Washington");
        states.add("West Virginia");
        states.add("Wisconsin");
        states.add("Wyoming");

        stateAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

//        List<String> x = map.get("Alabama");
//        listAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, x);
//        listView.setAdapter(listAdapter);
//        listAdapter.notifyDataSetChanged();

        return root;
    }

    private class Async extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                map = jsonParser.filterCounties();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {

            List<String> x = map.get("Alabama");
            listAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, x);
            listView.setAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();

            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    selectedState = adapterView.getItemAtPosition(i).toString();

                    Log.i("PRINT 1", "Selected State: " + selectedState);

                    List<String> x = map.get(selectedState); // list of counties

                    listAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, x);
                    listView.setAdapter(listAdapter);
                    listAdapter.notifyDataSetChanged();

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // shouldn't do anything, will set a default
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    List<String> x = map.get(selectedState);
                    selectedCounty = x.get(position);

                    Intent intent = new Intent(getActivity(), LocationDetailsSearchActivity.class);
                    intent.putExtra(LOCATIONCUR, "" + selectedCounty + ", " + selectedState );
                    intent.putExtra(COUNTY, selectedCounty);
                    intent.putExtra(STATE, selectedState);
                    //intent.putExtra("name", locationList.get(position).getCounty());
                    startActivity(intent);


                    //Log.i("PRINT 2", "Selected County: " + selectedCounty);

                }
            });
        }
    }


}