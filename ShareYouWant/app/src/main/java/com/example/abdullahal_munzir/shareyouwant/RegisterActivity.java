package com.example.abdullahal_munzir.shareyouwant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewDatabase";

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword,confirmPassword;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private TextView textViewLogin;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;




    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();



        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        confirmPassword=(EditText)findViewById(R.id.editConfirmPassword);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);
        textViewLogin=findViewById(R.id.textViewLogin);

        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void registerUser(){

        //getting email and password from edit texts
        final String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String confirmPass=confirmPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(!confirmPass.equals(password)) {
            Toast.makeText(this,"Your password do not match",Toast.LENGTH_SHORT).show();
            editTextPassword.setText("");
            confirmPassword.setText("");
            return;
        }


        //if the email and password are not empty
        //displaying a progress dialog


        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String userID = user.getUid();
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            myRef.child("users").child(userID).child("email").setValue(email);
                            //display some message here
                            Toast.makeText(RegisterActivity.this,"Successfully registered",Toast.LENGTH_LONG).show();
                            //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                            finish();
                            //startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        }else{
                            //display some message here
                            Toast.makeText(RegisterActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
    @Override
    public void onBackPressed(){
       // finishAffinity();
        //System.exit(0);
        finish();

    }

    @Override
    public void onClick(View view) {
        //calling register method on click
        registerUser();
    }
}