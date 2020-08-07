package com.smartparking.amit.parksmart;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MapNavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ImageView ProfileImage, menuIcon;
    private View headerView;
    private NavigationView navigationView;
    private TextView name_nav,email_nav;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private MapsFragment mapsFragment;
    private static final int PERMISSION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_nav);
        ///////////////////Hamburger_Icon_To_Toggle_Drawer/////////////////////////////////
        menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(R.id.nav_view);
                }
                else{
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });
        //////////////////////////Navigation Info/////////////////////////////////////////////////////////
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        ProfileImage = headerView.findViewById(R.id.profilePicId);
        name_nav = headerView.findViewById(R.id.name_nav);
        email_nav = headerView.findViewById(R.id.email_nav);
        if (user != null && user.getPhotoUrl() != null) {
            handler.sendEmptyMessage(0);
        }
        name_nav.setText(user.getDisplayName());
        email_nav.setText(user.getEmail());
        Log.d("Name", "handleMessage: " + user.getDisplayName());
        //////////////////////////FIREBASE_DATABASE////////////////////////////////////////////////////////
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        try{
            mDatabase.setPersistenceEnabled(true);
        }catch (Throwable t0 ){
            Log.d("dataPersistence",""+t0.getMessage());
        }


        ////////////////////////MAP_FRAGMENT//////////////////////////////////////////////
        Intent intent = getIntent();
        String s = intent.getStringExtra("Fragment");
        if(s==null){
            loadMapFragment();
        }
        else if(s.equals("MapsFragment")){
            loadMapFragment();
        }
        else{
            Bundle bundle = intent.getBundleExtra("location");
            BookingConfirmedFragment bookingConfirmedFragment = new BookingConfirmedFragment();
            bookingConfirmedFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map_container, bookingConfirmedFragment)
                    .commit();
        }
        //////////////////////////Navigation_Drawer_ItemSelectListener/////////////////////////////////////
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void loadMapFragment() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
        else{
            findlocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null && user.getPhotoUrl() != null) {
            handler.sendEmptyMessage(0);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                GlideApp.with(MapNavActivity.this)
                        .load(user.getPhotoUrl().toString())
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ProfileImage);
            }

        }
    };
    //////////////////////////Permission_Check////////////////////////////////////////////////////
    public void findlocation(){
        FusedLocationProviderClient mFusedLocationProvider =  LocationServices.getFusedLocationProviderClient(this);
        GeoDataClient mGeodataClient = Places.getGeoDataClient(this);
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
            mapsFragment = new MapsFragment();
            mapsFragment.setmFusedLocationProviderClient(mFusedLocationProvider);
            mapsFragment.setmGeoDataClient(mGeodataClient);
            mapsFragment.setmPlaceDetectionClient(mPlaceDetectionClient);
            FragmentManager mManager = getSupportFragmentManager();
            FragmentTransaction ft =  mManager.beginTransaction();
            ft.replace(R.id.map_container, mapsFragment);
            ft.commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findlocation();
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void profilePicClick(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_nav, menu);
        return true;
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.my_parkings) {
            Intent I = new Intent(MapNavActivity.this, BookingHistoryActivity.class);
            startActivity(I);
        } else if (id == R.id.help) {

        } else if (id == R.id.payment) {

        } else if (id == R.id.about_us) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.rent_your_space) {
            Intent I = new Intent(MapNavActivity.this,SpaceRentActivity.class);
            startActivity(I);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment f = getFragmentManager().findFragmentById(R.id.bottom_sheet);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (f != null)
        {
            if(f.equals(BookingConfirmedFragment.class)){
                Toast.makeText(MapNavActivity.this,"Do not close the app!",Toast.LENGTH_SHORT).show();
            }

        }
        else{
            super.onBackPressed();
        }
    }
}
