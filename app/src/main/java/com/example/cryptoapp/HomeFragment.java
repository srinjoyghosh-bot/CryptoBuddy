package com.example.cryptoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {
    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 123;
    public static final int RESULT_OK           = -1;
    public static final int RESULT_CANCELED    = 0;
    private String Username;


    private Button SignOutButton;
    private TextView HelloText;
    private TextView UserNameText;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private String mUserId;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        getActivity().setTitle(R.string.app_name);

        SignOutButton=(Button)view.findViewById(R.id.sign_out);
        HelloText=view.findViewById(R.id.hello_text);
        Typeface typeface=Typeface.createFromAsset(getActivity().getAssets(),"BarokahSignature.ttf");
        HelloText.setTypeface(typeface);
        HelloText.setTextSize(20);
        UserNameText=view.findViewById(R.id.user_name);
        Username=ANONYMOUS;


        mFirebaseAuth=FirebaseAuth.getInstance();
        mUser=mFirebaseAuth.getCurrentUser();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference("Users");
        mUserId=mUser.getUid();
        mDatabaseReference.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile=snapshot.getValue(User.class);
                if (userProfile!=null)
                {
                    UserNameText.setText(userProfile.getFullName()+"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });



        SignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setMessage("Want to Sign Out?");
                builder.setPositiveButton("Sign-Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       mFirebaseAuth.signOut();
                       startActivity(new Intent(getActivity(),LoginActivity.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog!=null)
                        {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN)
        {
            if (resultCode==RESULT_OK)
            {
                Toast.makeText(getContext(),"Signed In",Toast.LENGTH_SHORT).show();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(getContext(),"Sign in cancelled",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
