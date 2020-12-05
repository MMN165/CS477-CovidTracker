package com.example.cs477_covidtracker.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs477_covidtracker.R;
import com.example.cs477_covidtracker.cardLocation;
import com.example.cs477_covidtracker.locationAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    private class fetch extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            String county = (String) objects[0];
            String state = (String) objects[1];
            try {
               /* Task<Location> local = locclient.getLastLocation();
                local.addOnCompleteListener((Executor) this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            Location lastKnownLocation = task.getResult();
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("https://covidti.com/api/public/us/timeseries/Virginia/Fairfax")
                                        .method("GET", null)
                                        .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_2=value")
                                        .build();
                                Response response = client.newCall(request).execute();
                                String test = response.body().string();
                                ArrayList<int[]> timestamps = jsonParser.filterTimeSeriesResults(test);
                                for(int[] time : timestamps){

                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                });*/


            }catch(SecurityException e){
                e.printStackTrace();
                Toast.makeText(getContext(), "No Permissions!", Toast.LENGTH_SHORT).show();
            }


        /*  Request request = new Request.Builder()
                    .url("https://covidti.com/api/public/us/timeseries/Virginia/Fairfax")
                    .method("GET", null)
                    .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_1=value")
                    .build();
            Response response;*/
            try {
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(30, TimeUnit.SECONDS);
                client.setReadTimeout(30, TimeUnit.SECONDS);
                Request request = new Request.Builder()
                        .url("https://covidti.com/api/public/us/timeseries/" + state + "/" + county)
                        .method("GET", null)
                        .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_2=value")
                        .build();
                Response response = client.newCall(request).execute();
                String test = response.body().string();
                ArrayList<int[]> timestamps = jsonParser.filterTimeSeriesResults(test);
               /* for(int[] time : timestamps){

                }*/

               // for(cardLocation loc: locationList){
                 //   String query = loc.getLocation();
                    request = new Request.Builder()
                            .url("https://covidti.com/api/public/us/timeseries/" + "Virginia" + "/" + "Fairfax")
                            .method("GET", null)
                            .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_2=value")
                            .build();
                    response = client.newCall(request).execute();
                    test = response.body().string();
                    timestamps = jsonParser.filterTimeSeriesResults(test);
               // }
                return timestamps.get(timestamps.size() - 1)[0] - timestamps.get(timestamps.size() - 2)[0];
            }catch (Exception e){
                e.printStackTrace();
            }



            return 0;
        }
        @Override
        protected void onPostExecute(Object result ){
            currentCases.setText("" + (int) result);
        }

    }

    FusedLocationProviderClient locclient;
    SharedPreferences pref;
    TextView currentCases, lt;
    private RecyclerView mRecyclerView;
    private locationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<cardLocation> locationList;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        CardView textView = root.findViewById(R.id.local_dest);
      /*  homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
*/

       /* favorites = root.findViewById(R.id.favorites_list);
        ArrayAdapter lister = new ArrayAdapter<String>(root.getContext(), R.layout.card_info_layout);
        favorites.setAdapter(lister);

        favorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/

        pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);



        //String local = locations.getString("local")

        currentCases = root.findViewById(R.id.currentCases);

        String[] locationParams = {"Arlington", "Virginia"};
        lt = root.findViewById(R.id.info_text);

        lt.setText("" + "Arlington" + ", " + "Virginia");

        locationList = new ArrayList<>();
        //locationList.add(new cardLocation(100, 200, "LA, California"));
        //locationList.add(new cardLocation(205, 632, "New York, NY"));
        mRecyclerView = root.findViewById(R.id.favorites_list);
        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new locationAdapter(locationList);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);

        mAdapter.setOnItemClickListener(new locationAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                locationList.get(position).changeLocation("Clicked");
                mAdapter.notifyItemChanged(position);
            }
        });

        new fetch().execute(locationParams);
        return root;
    }

    void getLocations(){
       locclient =  LocationServices.getFusedLocationProviderClient(this.getActivity());
    }
    private void getLocation(){
       // if(checkPermissions())



    }
}