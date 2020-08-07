package com.smartparking.amit.parksmart;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class
SlotViewingActivity extends AppCompatActivity {
    private int level,r,c;
    private GridView gridview;
    private DatabaseReference mRef;
    private ArrayList<Integer> mThumbIds = new ArrayList<>();
    DataSnapshot mdataSnapshot;
    ImageAdapter imageAdapter;
    Button edit;
    String meEdit = "EDIT",myDone = "DONE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_viewing);
        Intent intent = getIntent();
        level = intent.getIntExtra("MyPosition", level);
        mRef= FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("SystemInfo")
                .child("ParkingLevels")
                .child("" + level);
        gridview = findViewById(R.id.gridview);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mr = Integer.parseInt(dataSnapshot.child("Rows").getValue().toString());
                int mc = Integer.parseInt(dataSnapshot.child("Columns").getValue().toString());
                // imageAdapter.setGridSize(r, c);
                setRowsCoulumns(mr,mc,dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        edit = findViewById(R.id.edit);
        oneditclick();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneditclick();
            }
        });


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (edit.getText() == "DONE") {
                    mRef.child("SlotIds")
                            .child("Slot" + position)
                            .setValue(Long.toString(id));

                    if (mThumbIds.get(position) == R.drawable.white) {
                        mThumbIds.set(position, R.drawable.grey);
                    } else {
                        mThumbIds.set(position, R.drawable.white);
                        mRef.removeValue();
                    }
                    ArrayList<Integer> newArrayList = new ArrayList<>();
                    ImageAdapter newImageAdapter = new ImageAdapter(SlotViewingActivity.this, newArrayList);
                    gridview.setAdapter(newImageAdapter);
                    gridview.setAdapter(imageAdapter);
                    //gridview.invalidateViews();
                    //Red and green markers code
                }

            }
        });



    }

    private void oneditclick() {
        if(edit.getText()=="EDIT"){
            edit.setText(myDone);


            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    myParamenters(r, c, dataSnapshot.child("SlotIds"));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        else{
            edit.setText(meEdit);
            showSlots();
        }
    }

    public void showSlots(){
        mThumbIds.clear();
        for(long k = 0; k<r*c; k++){

            if(mdataSnapshot.child("SlotIds").child("Slot" + k).getValue() == null)  {
                mThumbIds.add((int) k, R.drawable.noborder);
            }
            else{
                mThumbIds.add((int)k,R.drawable.green);
            }
        }
        refreshView();
    }

    public void  myParamenters(int rows, int columns, DataSnapshot dataSnapshot){
        mThumbIds.clear();
        for(long k = 0; k<rows*columns; k++){

            if(dataSnapshot.child("Slot" + k).getValue() == null)  {
                mThumbIds.add((int) k, R.drawable.white);
            }
            else{
                Log.d("Value","datasnapshot value " + dataSnapshot.child("Slot" + k) );
                mThumbIds.add((int)k,R.drawable.grey);
            }
        }
        refreshView();
    }

    public void refreshView(){
        ArrayList<Integer> newArrayList = new ArrayList<>();
        ImageAdapter newImageAdapter = new ImageAdapter(SlotViewingActivity.this, newArrayList);
        gridview.setAdapter(newImageAdapter);

        imageAdapter = new ImageAdapter(SlotViewingActivity.this,mThumbIds);
        gridview.setAdapter(imageAdapter);

    }
    public void setRowsCoulumns(int mr,int mc,DataSnapshot dataSnapshot){
        r=mr;
        c=mc;
        mdataSnapshot = dataSnapshot;
    }
}
