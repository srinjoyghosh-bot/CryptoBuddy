package com.example.cryptoapp;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mNameText;
    private EditText mAgeText;
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mSignUpButton;
    private ProgressBar mProgressBar;
    private ImageView showPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //getActionBar().hide();
        mNameText=findViewById(R.id.enter_name);
        mAgeText=findViewById(R.id.enter_age);
        mEmailText=findViewById(R.id.enter_email);
        mPasswordText=findViewById(R.id.enter_password);
        mSignUpButton=findViewById(R.id.sign_up_btn);
        mProgressBar=findViewById(R.id.sign_up_progress);
        showPassword=findViewById(R.id.password_lock_sign_in);
        mFirebaseAuth=FirebaseAuth.getInstance();
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance()))
                {
                    mPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }
                else
                {
                    mPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }
    private void registerUser(){
        String name=mNameText.getText().toString();
        String age=mAgeText.getText().toString();
        String email=mEmailText.getText().toString();
        String password=mPasswordText.getText().toString();
        if (name.isEmpty())
        {
            mNameText.setError("Full Name Required!");
            mNameText.requestFocus();
            return;
        }
        if (age.isEmpty())
        {
            mAgeText.setError("Age Required!");
            mAgeText.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            mEmailText.setError("Email Address Required!");
            mEmailText.requestFocus();
            return;
        }
        if (password.isEmpty())
        {
            mPasswordText.setError("Password Required!");
            mPasswordText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmailText.setError("Provide Correct EmailId!");
            mEmailText.requestFocus();
            return;
        }
        if (password.length()<6)
        {
            mPasswordText.setError("Password length must be atleast 6!");
            mPasswordText.requestFocus();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    User user=new User(name,age,email);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "User Registered!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Failed to Register! Try Again", Toast.LENGTH_SHORT).show();

                            }
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Failed to Register! Try Again", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
