package com.example.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView signup_txt;
    private EditText email,password;
    private Button btnLogin;
    //Firebase Authentication
    private FirebaseAuth mAuth;

    //private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup_txt=findViewById(R.id.signup_txt);
        email=findViewById(R.id.email_login);
        password=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.login_btn);
        mAuth=FirebaseAuth.getInstance();

        //mDialog=new ProgressDialog(this);

        signup_txt.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
           }
       });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=email.getText().toString().trim();
                String mPassword=password.getText().toString().trim();
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required field!!!");
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Required field!!!");
                    return;
                }
                //mDialog.setMessage("Processing...");
                //mDialog.show();
                mAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Login Successfull",Toast.LENGTH_SHORT);
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            //mDialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Problem!!!",Toast.LENGTH_SHORT);
                            //mDialog.dismiss();
                        }


                    }
                });

            }
        });
    }

}
