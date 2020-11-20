package com.example.cs477_covidtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationsAndSharedPreferences extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 34;
    SharedPreferences userLocation;
    SharedPreferences.Editor userLocationEditor;
    public static final String homeLoc = "homeLoc_key";
    public static final String currentLoc = "currentLoc_key";

    SharedPreferences userPreference; // like light/dark mode, etc
    SharedPreferences.Editor userPreferenceEditor;
    public static final String colorMode = "colorMode_key";


    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mlocationCallback;
    private LocationSettingsRequest.Builder builder;
    private static final int REQUEST_CHECK_SETTINGS = 102;

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startLocationPermissionRequest();
        // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        userLocation = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE); // or 0 for private mode

        Log.i("CHECK PREF home1", userLocation.getString(homeLoc, ""));
        Log.i("CHECK PREF curr1", userLocation.getString(currentLoc, ""));

        userLocationEditor = userLocation.edit();
        userLocationEditor.putString(homeLoc, "Fairfax City");
        userLocationEditor.putString(currentLoc, "George Mason University");
        userLocationEditor.commit();

        Log.i("CHECK PREF home2", userLocation.getString(homeLoc, ""));
        Log.i("CHECK PREF curr2", userLocation.getString(currentLoc, ""));


        // -----------------------------------------------------------------------------------------
        // somewhere the user will enter their home address ----------------------------------------
        // -----------------------------------------------------------------------------------------
        String homeAddress = "George Mason University";
        Log.i("LOCATION POINT", homeAddress);

        List<Address> geocodeMatches = null;
        try { // get geocode stuff
            geocodeMatches = new Geocoder(this).getFromLocationName(homeAddress, 1);
            Log.e("geocodematches", geocodeMatches.toString());
        } catch (IOException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (geocodeMatches != null && !geocodeMatches.isEmpty()) {
            String adminArea = geocodeMatches.get(0).getAdminArea(); // state
            String subAdminArea = geocodeMatches.get(0).getSubAdminArea(); // county
            String homeLocation = subAdminArea + ", " + adminArea;
            Log.i("LOCATION POINT", homeLocation);
            userLocationEditor.putString(homeLoc, homeLocation);
            userLocationEditor.commit();
        }

        Log.i("CHECK PREF home3", userLocation.getString(homeLoc, ""));
        Log.i("CHECK PREF curr3", userLocation.getString(currentLoc, ""));


        // -----------------------------------------------------------------------------------------
        // get device location ---------------------------- ----------------------------------------
        // -----------------------------------------------------------------------------------------

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("ON LOCATION CHANGED", location.getLatitude() + " " + location.getLongitude());

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    // String address = addresses.get(0).getAddressLine(0);
                    // Log.i("ON LOCATION CHANGED", address); // works!
                    String county = addresses.get(0).getSubAdminArea();
                    String state = addresses.get(0).getAdminArea();
                    Log.i("ON LOCATION CHANGED", county + ", " + state);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            startLocationPermissionRequest();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (checkPermissions() == true) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 10, locationListener);
            //  minTimeMs (long): minimum time interval between location updates in milliseconds
            // minDistanceM (float): minimum distance between location updates in meters
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Log.i("LOCATE", lastKnownLocation.getLatitude() + " " + lastKnownLocation.getLongitude());
        } else {
            startLocationPermissionRequest(); // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_l_LOCATION}, 1);
        }

    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return (permissionState & permissionState2) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(LocationsAndSharedPreferences.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(LocationsAndSharedPreferences.this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(LocationsAndSharedPreferences.this, new String[]{permission}, requestCode);
        } else {
            Toast.makeText(LocationsAndSharedPreferences.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // idk
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
