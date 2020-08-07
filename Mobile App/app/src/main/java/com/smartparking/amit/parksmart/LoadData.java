package com.smartparking.amit.parksmart;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LoadData extends AsyncTask<GoogleMap,Integer,GoogleMap> {

    private static final float DEFAULT_ZOOM = 10;
    private GoogleMap mMap;
    private LatLng mDefaultLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;

    @Override
    protected GoogleMap doInBackground(GoogleMap... googleMaps) {
        mMap = googleMaps[0];

        //updateLocationUI();
        //getDeviceLocation();

        // Add a marker in Sydney and move the camera

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        return mMap;
    }
}
