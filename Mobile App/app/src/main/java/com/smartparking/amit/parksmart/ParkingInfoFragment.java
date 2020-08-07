package com.smartparking.amit.parksmart;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParkingInfoFragment extends Fragment{

    private int  bookingFlag;
    private TextView sysName,cost;
    public ParkingInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parking_info, container, false);
        sysName = view.findViewById(R.id.systemName);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button bookmyspot = view.findViewById(R.id.bookslot);
        final String marker = getArguments().getString("ParkingName");
        sysName.setText(marker);
        bookmyspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingFlag = 1;
                bookaslot(marker);
            }
        });
    }

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
        //Todo QR code generation and OTP generation
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Booked").child(id).child("Slot").setValue(s);
        int random = (int)(Math.random() * 9000 + 1000);
        mDatabase.child("Booked").child(id).child("OTP").setValue(Integer.toString(random));
        mDatabase.child("Booked").child(id).child("Status").setValue("Booked");
        //////////////////OnBOokingComplete///////////////////////////////////
        Bundle bundle = this.getArguments();
        //if(bundle!=null) {
            BookingConfirmedFragment bookingConfirmedFragment = new BookingConfirmedFragment();
            bookingConfirmedFragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.map_container, bookingConfirmedFragment)
                    //.addToBackStack(MapsFragment.class.getSimpleName())
                    .commit();
        //}
    }
}
