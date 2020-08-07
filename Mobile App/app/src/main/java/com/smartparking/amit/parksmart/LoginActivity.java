package com.smartparking.amit.parksmart;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    EditText mEmail,mPassword;
    //TextView error;
    ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
            findViewById(R.id.loginButton).setOnClickListener(this);
            findViewById(R.id.signUp).setOnClickListener(this);
            //error = findViewById(R.id.error);
            mEmail = findViewById(R.id.emailBox);
            mPassword = findViewById(R.id.passBox);
            mProgressBar = findViewById(R.id.progressBar);
            mAuth = FirebaseAuth.getInstance();
    }
    public void userRegister(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        mProgressBar.setVisibility(View.VISIBLE);

        if(email.isEmpty()){
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("Please enter a valid Email");
            mEmail.requestFocus();
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty()){
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        if(password.length()<6){
            mPassword.setError("Please enter atleast 6 character password");
            mPassword.requestFocus();
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this,MapNavActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case(R.id.signUp):
                finish();
                startActivity(new Intent(this,SignUpActivity.class));
                break;
            case(R.id.loginButton):
                userRegister();
                break;
        }
    }
}
