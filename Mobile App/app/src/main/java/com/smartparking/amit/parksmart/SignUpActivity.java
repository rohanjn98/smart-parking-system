package com.smartparking.amit.parksmart;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextPassword,editTextemail,phoneNo,firstName;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.signupButton).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
        editTextemail = findViewById(R.id.emailBox);
        editTextPassword = findViewById(R.id.passBox);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        firstName = findViewById(R.id.firstName);
        phoneNo = findViewById(R.id.phoneNo);
    }

    public void registerUser(){
        final String email = editTextemail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String mFirstName = firstName.getText().toString().trim();
        final String mPhone = phoneNo.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if(email.isEmpty()){
            editTextemail.setError("Email is required");
            editTextemail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextemail.setError("Please enter a valid Email");
            editTextemail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Please enter atleast 6 character password");
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if(phoneNo.length()<10){
            phoneNo.setError("Please enter valid Phone Number");
            phoneNo.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(firstName.getText()==null){
            firstName.setError("Please enter valid First Name");
            firstName.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    user user1 = new user(mPhone,mFirstName,email);
                    updatedisplayname(mFirstName);
                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("Reg", "onSuccess: Databse updated");
                                    Intent intent = new Intent(SignUpActivity.this,MapNavActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    finish();
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Log.d("Reg", "onFailure: "+e.getMessage());
                                }
                            });
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"User already exist",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void updatedisplayname(String mFirstName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mFirstName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ProfileUpdate", "User profile updated.");
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case(R.id.signupButton):
                registerUser();
                break;
            case(R.id.login):
                finish();
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }

    }
}
