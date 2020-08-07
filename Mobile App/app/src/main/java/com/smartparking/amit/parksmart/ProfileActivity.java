package com.smartparking.amit.parksmart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CHOOSE_IMAGE = 101;
    ImageView profilePicUpdate;
    String profileImageUrl, userId;
    TextView FirstName, PhoneNo, EmailId;
    String filename = "ProfilePic";

    Uri uriProfileImage;
    FirebaseAuth mAuth;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){
            Intent I = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(I);
            //Todo Toast
        }
        assert user != null;
        userId = user.getUid();

        FirstName =findViewById(R.id.firstName);
        PhoneNo = findViewById(R.id.phoneNo);
        EmailId = findViewById(R.id.emailId);
        profilePicUpdate = findViewById(R.id.profilePicUpdate);
        profilePicUpdate.setOnClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(user!=null && user.getPhotoUrl()!=null) {
                    //Log.d("ProfilePic", "onCreate: ProfileActivity "+ user.getPhotoUrl().toString());
                    mHandler.sendEmptyMessage(1);
                }
                FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
                try{
                    mFirebaseDatabase.setPersistenceEnabled(true);
                }catch (Throwable ignored){

                }
                DatabaseReference mRef = mFirebaseDatabase.getReference();
                DatabaseReference userRef = mRef.child("Users");
                //Log.d("userRef", "onCreate: "+ userRef.toString() );
                userRef.keepSynced(true);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();


        FirstName.setOnClickListener(this);
        EmailId.setOnClickListener(this);
        PhoneNo.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.signout).setOnClickListener(this);
    }

    private void showData(DataSnapshot dataSnapshot) {

        //for (DataSnapshot ds: dataSnapshot.getChildren()){
        user userOne = new user();
        //DataSnapshot dsChild = ds.child(userId);
        //Log.d("showData", "showData: " + dsChild.toString());
        //Log.d("showData", "showData: "+ds.child(userId).getValue(user.class).getFN());
        //userOne = ds.child(userId).getValue(user.class);
        userOne.setFirstName(dataSnapshot.child(userId).getValue(user.class).getFirstName());
        userOne.setPhoneNo(dataSnapshot.child(userId).getValue(user.class).getPhoneNo());
        userOne.setEmailId(dataSnapshot.child(userId).getValue(user.class).getEmailId());


        Log.d("showData", "showData: "+userOne.getFirstName());
        FirstName.setText(userOne.getFirstName().trim());
        PhoneNo.setText(userOne.getPhoneNo().trim());
        EmailId.setText(userOne.getEmailId().trim());
        //}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.profilePicUpdate):
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showImageChooser();
                    }
                }).start();
                break;

            case (R.id.back):
                finish();
                //onBackPressed();
                //startActivity(new Intent(ProfileActivity.this,Main2Activity.class));
                break;
            case (R.id.firstName):
                break;
            case (R.id.emailId):
                break;
            case (R.id.signout):
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent I = new Intent(ProfileActivity.this,LoginActivity.class);
                I.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(I);
        }
    }

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0){
                findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
                GlideApp.with(ProfileActivity.this)
                        .load(bitmap)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profilePicUpdate);
            }
            if(msg.what == 1){

                GlideApp.with(ProfileActivity.this)
                        .load(mAuth.getCurrentUser().getPhotoUrl().toString())
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profilePicUpdate);

            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK  && data!=null && data.getData()!=null){

            uriProfileImage = data.getData();

            try {
                Log.d("Bitmap", "onActivityResult: BitmapGenerated");
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                /*try (FileOutputStream out = new FileOutputStream(filename)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    Log.d("Offline", "onActivityResult: saved offline ");// PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                mHandler.sendEmptyMessage(0);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Bitmap", "onActivityResult: Bitmap not generated");
            }
        }
    }

    private void saveInfoToFirbase() {

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile =  new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Profile Pic Uploaded", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar2).setVisibility(View.GONE);
                    }
                    else{
                        Log.d("ProfileUpdated", "onComplete: Error Occured while uploading image");
                    }
                }
            });

        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage != null){
            UploadTask uploadTask = profileImageRef.putFile(uriProfileImage);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        profileImageUrl = downloadUri.toString();
                        saveInfoToFirbase();
                        Log.d("ProfileUploaded", "onSuccess: Profile picture uploaded "+ profileImageUrl);
                    } else {
                        Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar2).setVisibility(View.GONE);
                        Log.d("ProfileUploaded", "onFailure: Profile not uploaded");
                    }
                }
            });
                    /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(getApplicationContext(),"Profile Picture Updated",Toast.LENGTH_SHORT).show();
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            saveInfoToFirbase();
                            Log.d("ProfileUploaded", "onSuccess: Profile picture uploaded "+ taskSnapshot.getDownloadUrl().toString().trim());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
                            findViewById(R.id.progressBar2).setVisibility(View.GONE);
                            Log.d("ProfileUploaded", "onFailure: Profile not uploaded");
                        }
                    });*/

        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
}