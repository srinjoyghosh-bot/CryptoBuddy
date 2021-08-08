package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextView SignUpText;
    private TextView ForgotPassword;
    private EditText mEmail,mPassword;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private Button mLogInButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SignUpText=findViewById(R.id.sign_up);
        mEmail=findViewById(R.id.email_text);
        mPassword=findViewById(R.id.password_text);
        mProgressBar=findViewById(R.id.login_progress);
        mLogInButton=findViewById(R.id.login_btn);
        ForgotPassword=findViewById(R.id.forgot_password);
        mAuth=FirebaseAuth.getInstance();
        SignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

    }
    private void userLogin(){
        String email=mEmail.getText().toString();
        String password=mPassword.getText().toString();
        if (email.isEmpty())
        {
            mEmail.setError("Provide email address");
            mEmail.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            mPassword.setError("Provide password");
            mPassword.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmail.setError("Provide Correct EmailId!");
            mEmail.requestFocus();
            return;
        }
        if (password.length()<6)
        {
            mPassword.setError("Password length must be atleast 6!");
            mPassword.requestFocus();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    startActivity(new Intent(LoginActivity.this,DefaultActivity.class));
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Failed yo LogIn! Check your credentials and try again", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
