package com.smartparking.amit.parksmart;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class ParkingInfoActivity extends AppCompatActivity {
    private Button Bookslot;
    private TextView boookedText;
    private String mMarker;
    int bookingFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_info);
        mMarker = getIntent().getStringExtra("mMarker");
        Log.d("StringRecieved", "onCreate: "+mMarker);
        Bookslot = findViewById(R.id.bookslot);
        Bookslot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingFlag = 1;
                bookaslot(mMarker);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bookslot:
                Log.d("Method", "onClick: Booking a slot");
                bookaslot(mMarker);
                break;
            default:
                break;
        }
    }*/

    private void bookaslot(final String marker) {
        Log.d("Booking", "Function called");
        final DatabaseReference mDatabase  = FirebaseDatabase.getInstance().getReference("Parkings")
                .child(marker)
                .child("Slots");
        mDatabase.child("Available").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("hasChild", "onDataChange: "+ dataSnapshot.getChildrenCount());
                String s = null,key = null;
                if(dataSnapshot.getChildrenCount()>=1 & bookingFlag == 1) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        s = ds.getValue().toString();
                        key = ds.getKey().toString();
                        break;
                    }
                    book(s,key,marker);
                }
                else{
                    //Slot Not Available
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //Todo Delete all logs
    private void book(String s, String key, String marker) {
        bookingFlag =0;
        final DatabaseReference mDatabase  = FirebaseDatabase.getInstance().getReference("Parkings")
                .child(marker)
                .child("Slots");
        mDatabase.child("Available").child(key).removeValue();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Booked").child(id).child("Slot").setValue(s);
        mDatabase.child("Booked").child(id).child("OTP").setValue("SOMEOTP");

        //////////////////OnBOokingComplete///////////////////////////////////
        Intent intent = new Intent(ParkingInfoActivity.this, MapNavActivity.class);
        finish();
        intent.putExtra("BookingCode", "Booked");
        startActivity(intent);
    }

}
