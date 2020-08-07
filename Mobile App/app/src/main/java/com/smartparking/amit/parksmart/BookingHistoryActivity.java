package com.smartparking.amit.parksmart;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        FirebaseUser UserID = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference BookingRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(UserID.getUid())
                .child("MyBookings");
        if(BookingRef==null){
            //Todo No Data
        }else {
            final ListView listView = findViewById(R.id.HistList);
            final ArrayList<customHistory> bookinghistory = new ArrayList<customHistory>();
            final CustomAdapter arrayAdapter = new CustomAdapter(this, bookinghistory);
            listView.setAdapter(arrayAdapter);
            BookingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot d: dataSnapshot.getChildren()){
                        /*String sysName = d.child("mSystemName").getValue().toString();
                        String date = d.child("mDate").getValue().toString();
                        long bill = (long) d.child("mBill").getValue();
                        String status = d.child("status").getValue().toString();*/
                        Log.d("customHistory", "onChildAdded: "+ d.toString());
                        customHistory value = d.getValue(customHistory.class);
                        //customHistory value = dataSnapshot.getValue(customHistory.class);
                        bookinghistory.add(value);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
