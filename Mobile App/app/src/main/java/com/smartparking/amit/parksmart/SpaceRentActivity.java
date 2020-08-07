package com.smartparking.amit.parksmart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Dialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpaceRentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<LevelParams> levelParamsArrayList;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_space_rent);
        levelParamsArrayList = new ArrayList<>();



        recyclerView = findViewById(R.id.recyle_view);
        fab = findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setRecyclerViewData(); //adding data to array list

        adapter = new RecyclerAdapter(this, levelParamsArrayList);
        recyclerView.setAdapter(adapter);
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("SystemInfo")
                .child("ParkingLevels");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.d("Recieved value","This: " + ds.getValue());
                    LevelParams levelParams = ds.getValue(LevelParams.class);
                    levelParamsArrayList.add(levelParams);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(onAddingListener());

    }

    private void setRecyclerViewData() {

    }

    private View.OnClickListener onAddingListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(SpaceRentActivity.this);
                dialog.setContentView(R.layout.dialog_add); //layout for dialog
                dialog.setTitle("Add a new Level");
                dialog.setCancelable(false); //none-dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                EditText rows = dialog.findViewById(R.id.rows);
                EditText columns = dialog.findViewById(R.id.columns);
                View btnAdd = dialog.findViewById(R.id.btn_ok);
                View btnCancel = dialog.findViewById(R.id.btn_cancel);

                btnAdd.setOnClickListener(onConfirmListener(rows, columns, dialog));
                btnCancel.setOnClickListener(onCancelListener(dialog));

                dialog.show();
            }
        };
    }



    private View.OnClickListener onConfirmListener(final EditText rows, final EditText columns, final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LevelParams levelParams = new LevelParams(rows.getText().toString().trim(), columns.getText().toString().trim());
                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("SystemInfo")
                        .child("ParkingLevels");

                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long i = dataSnapshot.getChildrenCount();
                        mRef.child("" + i)
                                .child("Columns").setValue(columns.getText().toString().trim());
                        mRef.child("" + i)
                                .child("Rows").setValue(rows.getText().toString().trim());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //adding new object to arraylist
                levelParamsArrayList.add(levelParams);

                //notify data set changed in RecyclerView adapter
                adapter.notifyDataSetChanged();

                //close dialog after all
                dialog.dismiss();

            }
        };
    }
    private View.OnClickListener onCancelListener(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }


}

