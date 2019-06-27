package com.example.abdullahal_munzir.shareyouwant;

/*created
        by
        shifat  12-03-2018*/

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdullahal_munzir.shareyouwant.MyDirectory.MyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);


        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        progressDialog = new ProgressDialog(this);
     /* if (firebaseAuth != null) {
          Toast.makeText(this, "" + firebaseAuth.getUid(), Toast.LENGTH_SHORT).show();
          startActivity(new Intent(getApplicationContext(), AfterloginActivity.class));
      }*/
        buttonLogin.setOnClickListener(this);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }

    private void signinUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Signing loading....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //checking if success
                                if (task.isSuccessful()) {
                                    //display some message here
                                    Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_LONG).show();
                                    finish();
                                    //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    //display some message here
                                    Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }


                        }
                );
    }

    @Override
    public void onClick(View view) {
        //calling register method on click

        signinUser();
    }
}
