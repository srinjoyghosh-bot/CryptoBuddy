package com.example.cryptoapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText EmailText;
    private Button ResetButton;
    private ProgressBar mProgressBar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        EmailText=findViewById(R.id.email_text_fp);
        ResetButton=findViewById(R.id.reset_btn);
        mProgressBar=findViewById(R.id.progress_fp);
        auth=FirebaseAuth.getInstance();

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }
    private void resetPassword(){
        String email=EmailText.getText().toString().trim();
        if (email.isEmpty())
        {
            EmailText.setError("Provide email address");
            EmailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            EmailText.setError("Provide Correct EmailId!");
            EmailText.requestFocus();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(getApplicationContext(), "Check mail to reset password!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Something wrong happened. Try again!", Toast.LENGTH_SHORT).show();
                }
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
