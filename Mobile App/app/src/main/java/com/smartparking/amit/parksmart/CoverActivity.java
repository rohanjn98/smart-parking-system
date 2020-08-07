package com.smartparking.amit.parksmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.String.valueOf;

public class CoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                if(user != null){
                    Intent i;
                    Context context = CoverActivity.this;
                    SharedPreferences sharedPreferences = context.getSharedPreferences("Status", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Boolean s = sharedPreferences.getString("status","").isEmpty();
                    Log.d("sp", "run: " + sharedPreferences.getString("status",""));
                    if(s) {
                        i = new Intent(CoverActivity.this, MapNavActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("Fragment", "MapsFragment");
                    }
                    else{
                        Double CurrentLat = Double.parseDouble(String.valueOf(sharedPreferences.getFloat("CurrentLat",0)));
                        Double CurrentLong = Double.parseDouble(String.valueOf(sharedPreferences.getFloat("CurrentLong",0)));
                        Double DestinationLat = Double.parseDouble(String.valueOf(sharedPreferences.getFloat("DestinationLat",0)));
                        Double DestinationLong = Double.parseDouble(String.valueOf(sharedPreferences.getFloat("DestinationLong",0)));
                        String ParkingName = sharedPreferences.getString("ParkingName","");
                        String status = sharedPreferences.getString("status","");
                        Bundle bundle = new Bundle();
                        Log.d("Values", "CurrentLat: "+CurrentLat+ " CurrentLong: "+CurrentLong+" DestinationLat: "+ DestinationLat+" DestinationLong: "+DestinationLong);
                        bundle.putDouble("CurrentLat",CurrentLat);
                        bundle.putDouble("CurrentLong",CurrentLong);
                        bundle.putDouble("DestinationLat",DestinationLat );
                        bundle.putDouble("DestinationLong",DestinationLong);
                        bundle.putString("ParkingName",ParkingName);
                        bundle.putString("status",status);
                        editor.remove("status");
                        editor.remove("ParkingName");
                        editor.remove("CurrentLat");
                        editor.remove("CurrentLong");
                        editor.remove("DestinationLat");
                        editor.remove("DestinationLong");
                        editor.commit();
                        i = new Intent(CoverActivity.this, MapNavActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtra("Fragment", "BookingConfirmedFragment");
                        i.putExtra("location",bundle);
                    }
                    startActivity(i);
                }
                else{
                    Intent i = new Intent(CoverActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            }

        }, 2000);



    }
}
