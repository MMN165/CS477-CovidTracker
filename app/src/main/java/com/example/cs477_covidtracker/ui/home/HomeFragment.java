package com.example.cs477_covidtracker.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    // permissions
    private static final int PERMISSIONS_REQUEST_CODE = 34;

    // location shared preferences
    SharedPreferences userLocation;
    SharedPreferences.Editor userLocationEditor;
    public static final String countyKey = "county_key";
    public static final String stateKey = "state_key";




    // location
    LocationManager locationManager;
    LocationListener locationListener;

    double longitude;
    double latitude;

    String county = "";
    String state = "";

    /**
     * Fetch is an Async task that fetches Covidti API and setting values and list views as such.
     */
    private class fetch extends AsyncTask {


        /**
         * Getting the Data
         */
        @Override
        protected Object doInBackground(Object[] objects) {
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
                int local = timestamps.get(timestamps.size() - 1)[0];
                int deaths = timestamps.get(timestamps.size() - 1)[1];
                localHistory = timestamps;
                for (cardLocation loc : locationList) {
                    county = loc.getCounty();
                    state = loc.getState();
                    request = new Request.Builder()
                            .url("https://covidti.com/api/public/us/timeseries/" + state + "/" + county)
                            .method("GET", null)
                            .addHeader("Cookie", "__cfduid=d643853aa641016922decbeeaf960a3121604966690; Cookie_2=value")
                            .build();
                    response = client.newCall(request).execute();
                    test = response.body().string();
                    timestamps = jsonParser.filterTimeSeriesResults(test);

                    loc.setCurrentCases(timestamps.get(timestamps.size() - 1)[0]);
                    loc.setCurrentDeath(timestamps.get(timestamps.size() - 1)[1]);
                    loc.history = timestamps;
                }
                return new Pair(local, deaths);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return new Pair<Integer, Integer>(0, 0);
        }

        /**
         * After we are done, we set our local location card to responded values.
         */
        @Override
        protected void onPostExecute(Object result) {
            Pair<Integer, Integer> result2 = (Pair) result;
            if(getActivity() != null){
                currentCases.setText("" + result2.first);
                currentDeaths.setText("" + result2.second);
                mAdapter.notifyDataSetChanged();
                api_call_notif.setVisibility(View.GONE);
            }
        }

    }

    ArrayList<int[]> localHistory;
    FusedLocationProviderClient locclient;
    SharedPreferences pref;
    TextView currentCases, currentDeaths, lt;
    CardView localCard;
    public static final String CASESPASS = "com.example.cs477_covidtracker.CASESEPASS";
    public static final String LOCATIONCUR = "com.example.cs477_covidtracker.LOCATIONCUR";
    public static final String ISLOCAL = "com.example.cs477_covidtracker.ISLOCAL";
    private RecyclerView mRecyclerView;
    private locationAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    ArrayList<cardLocation> locationList;
    TextView api_call_notif, debug;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        currentCases = root.findViewById(R.id.currentCases);
        currentDeaths = root.findViewById(R.id.currentDeaths);
        localCard = root.findViewById(R.id.local_dest);
        api_call_notif = root.findViewById(R.id.API_Call_notif);

	// set defaults in case of null
        if (county == null || state == null) {
            county = "Fairfax";
            state = "Virginia";
        } else if (county.equals("") || state.equals("")) {
            county = "Fairfax";
            state = "Virginia";
        }
        debug = root.findViewById(R.id.debug);

        /**
         * Get the current device's location.
         */
        getDeviceLocation();
        final String[] locationParams = {county, state};
        lt = root.findViewById(R.id.info_text);

        // lt.setText("" + "Arlington" + ", " + "Virginia");
        lt.setText("" + county + ", " + state);

        /**
         * Setting our custom card view and using our own adapter.
         */
        locationList = new ArrayList<>();
        mRecyclerView = root.findViewById(R.id.favorites_list);
        pref = this.getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        Set<String> test = pref.getStringSet("favorites", null);
        /**
         * Getting the shared preferences to grab the favorites set so we can grab information on them.
         */
        if(test != null) {
            for (String x : test) {
                String[] spliter = x.split(", ", 2);
                locationList.add(new cardLocation(0, 0, spliter[0], spliter[1]));
            }
        }
        else{
            mRecyclerView.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), "The favorites list is currently empty. Please go to search to add to your favorites.", Toast.LENGTH_LONG).show();
        }
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mAdapter = new locationAdapter(locationList);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);

        /**
         * These 2 methods call LocationDetailsActivity. One from the list, the other from the Local location card.
         */
        mAdapter.setOnItemClickListener(new locationAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
                intent.putExtra(CASESPASS, locationList.get(position).history);
                intent.putExtra(ISLOCAL, false);
                intent.putExtra(LOCATIONCUR, locationList.get(position).getLocation());
                startActivity(intent);
            }
        });

        localCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
                intent.putExtra(CASESPASS, localHistory);
                intent.putExtra(ISLOCAL, true);
                intent.putExtra(LOCATIONCUR, "" + locationParams[0] + ", " + locationParams[1]);
                startActivity(intent);
            }
        });

        /**
         * Call our Async task to fetch data from the API.
         */
        new fetch().execute(locationParams);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    // ------------------------> Local Location

    private boolean checkPermissions() { // check
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        return (permissionState & permissionState2) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() { // ask
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }

    public void checkPermission(String permission, int requestCode) { // check and request permission.
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    /**
     * Gets the device Location. NOTE: We had varying degrees of success with Android 24 emulators. Success may vary. If unsuccessful, then it uses the default (Fairfax, VA) instead.
     */
    public void getDeviceLocation() {
        try {
            startLocationPermissionRequest(); // check that the user accepted

	    // if the app has been run before, use the previously saved location from share preferences
            userLocation = getActivity().getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE); // or 0 for private mode
            userLocationEditor = userLocation.edit();
            county = userLocation.getString(countyKey, "");
            state = userLocation.getString(stateKey, "");

            Log.i("PRINT 1 BEGIN", county + ", " + state);
            if (county.equals("") || state.equals("")) {
                state = "Virginia";
                county = "Fairfax";
            }
            Log.i("PRINT 2 BEGIN", county + ", " + state);

            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                startLocationPermissionRequest();
            }

	    // allows access to location services
            locationListener = new LocationListener() { // will use the data of the new location

                /**
                 * Empty so this can work on Android 24. Seems that our location method
                 */
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
                @Override
                public void onLocationChanged(Location location) {
                    Log.i("PRINT ON LOCATION CHANGED", location.getLatitude() + " " + location.getLongitude());

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> geocodeMatches = null;
                    try { // get geocode stuff
                        geocodeMatches = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        Log.e("geocodematches", geocodeMatches.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

	    	    // can get a info from address, such as country, zip, state, etc
                    if (geocodeMatches != null && !geocodeMatches.isEmpty()) {
                        String adminArea = geocodeMatches.get(0).getAdminArea(); // state
                        state = adminArea;
                        String subAdminArea = geocodeMatches.get(0).getSubAdminArea(); // county
                        county = subAdminArea;

			// if either are null, use the one saved in shared preferences
                        if(county == null || state == null){
                            county = userLocation.getString(countyKey, "Fairfax");
                            state =  userLocation.getString(stateKey, "Virginia");
                        }

			// since the api will have "Fairfax" instead of "Fairfax County",
			// remove the "County" and trim() will remove leading and trailing spaces
                        if (county.contains("County")) {
                            county = county.replaceAll("County", "");
                            county = county.trim();
                        }

                        Log.i("PRINT 12", county);

			// update shared preferences with non-null values
                        userLocationEditor.putString(countyKey, county);
                        userLocationEditor.putString(stateKey, state);
                        userLocationEditor.commit();

                    }

                    Log.i("PRINT 8", "Location: " + county + ", " + state);
                }
            };

            Log.i("PRINT PERM ", Boolean.toString(checkPermissions()));
            if (checkPermissions()) { // check that the user accepted location permissions
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                longitude = lastKnownLocation.getLongitude();
                latitude = lastKnownLocation.getLatitude();

                Log.i("PRINT CHECK PERM", Double.toString(longitude) + ", " + Double.toString(latitude));

                List<Address> addresses;
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                List<Address> geocodeMatches = null;
                try { // get geocode stuff, get one address max from lat and long
                    geocodeMatches = new Geocoder(getContext()).getFromLocation(latitude, longitude, 1);
                    Log.e("geocodematches", geocodeMatches.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (geocodeMatches != null && !geocodeMatches.isEmpty()) {
                    String adminArea = geocodeMatches.get(0).getAdminArea(); // state
                    state = adminArea;
                    String subAdminArea = geocodeMatches.get(0).getSubAdminArea(); // county
                    county = subAdminArea;

                    if(county == null || state == null){ // use shared preferences if either are null
                        county = userLocation.getString(countyKey, "Fairfax");
                        state =  userLocation.getString(stateKey, "Virginia");
                    }
                    if (county.contains("County")) { // same as above, remove "County" and extra spaces
                        String temp;
                        county = county.replaceAll("County", "");
                        county = county.trim();
                    }

		    // update shared preferences
                    userLocationEditor.putString(countyKey, county);
                    userLocationEditor.putString(stateKey, state);
                    userLocationEditor.commit();

                    Log.i("PRINT 7", "Location: " + county + ", " + state);
                } else { // defaults
                    county = "Fairfax";
                    state = "Virginia";
                    startLocationPermissionRequest();
                }

                Log.i("PRINT 3", "Location: " + county + ", " + state);
            }
        }catch(NullPointerException e){
            /**
             * We had issues with null pointers for county and list before, so if anything happens we just set our default county and state.
             */
            county = "Fairfax";
            state = "Virginia";
        }


    }


}
