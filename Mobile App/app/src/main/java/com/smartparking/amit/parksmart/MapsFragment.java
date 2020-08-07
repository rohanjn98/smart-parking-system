package com.smartparking.amit.parksmart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

public class MapsFragment extends Fragment implements OnMapReadyCallback, Serializable {
    public MapsFragment(){

    }
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutBottomSheet;
    private RelativeLayout relativeLayout;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation;
    private String mMarker = null;
    private float DEFAULT_ZOOM = 15;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    GoogleMap mMap;
    // Construct a FusedLocationProviderClient.
    public void setmFusedLocationProviderClient(FusedLocationProviderClient mFusedLocationProviderClient) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
    }
    // Construct a PlaceDetectionClient.
    public void setmGeoDataClient(GeoDataClient mGeoDataClient) {
        this.mGeoDataClient = mGeoDataClient;
    }
    // Construct a FusedLocationProviderClient.
    public void setmPlaceDetectionClient(PlaceDetectionClient mPlaceDetectionClient) {
        this.mPlaceDetectionClient = mPlaceDetectionClient;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps,container,false);
        return v;
    }

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        while (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details
            return;
        }

        mDefaultLocation = new LatLng(-34, 151);    //When current location is not available.

        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new MapsActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setMyLocationEnabled(true);
                    }
                    else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(true);
                        mMap.setMyLocationEnabled(true);
                    }
                }
            });
        }
        catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //////////////////////////Setting_Markers_Firebase_Database////////////////////////////////////
        FirebaseDatabase.getInstance().getReference("Parkings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MyLocation MyLocation = ds.getValue(MyLocation.class);
                    Log.d("mMarker", "onDataChange: "+ds.toString());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(MyLocation.getLatitude(), MyLocation.getLongitude()))
                            .title(ds.getKey().toString())
                            .snippet("Something"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /////////////////////////onMapClicked//////////////////////////////////////
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });
        /////////////////////////MarkerClickListener///////////////////////////////
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //////////////Zoom_In//////////////////////////////////////////////
                LatLng mMoveCamLocation;
                mMoveCamLocation = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mMoveCamLocation)
                        .zoom(DEFAULT_ZOOM)
                        .build();
                final CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(update);
                mMarker = marker.getTitle();
                ///////////////////////Show_bottom_sheet//////////////////////////
                /*sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/
                //relativeLayout.setVisibility(View.VISIBLE);
                ParkingInfoFragment parkingInfoFragment = new ParkingInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ParkingName", mMarker);
                bundle.putDouble("DestinationLat", mMoveCamLocation.latitude );
                bundle.putDouble("DestinationLong",mMoveCamLocation.longitude);
                if(mLastKnownLocation!=null){
                    bundle.putDouble("CurrentLat", mLastKnownLocation.getLatitude());
                    bundle.putDouble("CurrentLong", mLastKnownLocation.getLongitude());
                }
                parkingInfoFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.map_container, parkingInfoFragment)
                        .addToBackStack(MapsFragment.class.getSimpleName())
                        .commit();
                return true;
            }
        });
    }

}
