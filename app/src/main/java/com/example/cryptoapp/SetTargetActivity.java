package com.example.cryptoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SetTargetActivity extends AppCompatActivity {
    private TextView mCryptoNameText;
    private TextView mAssetIdText;
    private TextView currentRateText;
    private EditText mTargetText;
    private EditText mStopLossText;
    private TextView mAssetQuote;
    private String quoteAsset;
    private Crypto crypto;
    private String cryptoName;
    private String assetId;
    private double targetPrice;
    private String targetText;
    private double stopLossPrice;
    private String stopLossText;
    private Button mSaveButton;
    private boolean isTargetChanged=false;
    private boolean isStopLossChanged=false;
    private FirebaseDatabase mDb;
    private DatabaseReference mTargetsReference;
    private double currentCryptoRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_target);
        Intent intent=getIntent();
        crypto=(Crypto)intent.getSerializableExtra("targetCrypto");
        quoteAsset=intent.getStringExtra("assetQuote");
        currentCryptoRate=intent.getDoubleExtra("currentRate",100);
        cryptoName=crypto.getCryptoName();
        assetId=crypto.getCryptoAbbreviation();
        mCryptoNameText=findViewById(R.id.name_text);
        mAssetIdText=findViewById(R.id.asset_id_text);
        mTargetText=(EditText)findViewById(R.id.target_text);
        mStopLossText=(EditText)findViewById(R.id.stop_loss_text);
        mSaveButton=(Button)findViewById(R.id.save_button);
        mAssetQuote=findViewById(R.id.quote_text);
        currentRateText=findViewById(R.id.current_rate);

        mCryptoNameText.setText(crypto.getCryptoName());
        mAssetIdText.setText(crypto.getCryptoAbbreviation());
        mAssetQuote.setText(quoteAsset);
        currentRateText.setText(String.valueOf(currentCryptoRate));

        mDb=FirebaseDatabase.getInstance();
        mTargetsReference=mDb.getReference().child("targets");

        mTargetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length()>0)
                {
                    isTargetChanged=true;
                }
                else
                {
                    isTargetChanged=false;
                }
            }
        });
        mStopLossText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0)
                {
                    isStopLossChanged=true;
                }
                else
                {
                    isStopLossChanged=false;
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTargetChanged&&isStopLossChanged) {
                    targetPrice=Double.parseDouble(mTargetText.getText().toString());
                    stopLossPrice=Double.parseDouble(mStopLossText.getText().toString());
                    if(targetPrice>stopLossPrice)
                    {
                        CryptoTargets cryptoTargets = new CryptoTargets(cryptoName, assetId, quoteAsset, targetPrice, stopLossPrice);
                        mTargetsReference.push().setValue(cryptoTargets);
                        Toast.makeText(getApplicationContext(), "Target Added", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        mStopLossText.setError("Stop-Loss price must be less than Target price");
                    }

                }
                else if(!isTargetChanged)
                {
                    mTargetText.setError("Specify Target Price");
                }
                else
                {
                    mStopLossText.setError("Specify Stop-Loss Price");
                }
//

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!isTargetChanged && !isStopLossChanged)
        {
            super.onBackPressed();
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(SetTargetActivity.this);
        builder.setMessage("Quit and Discard changes?");
        builder.setPositiveButton("Discard and Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Keep Changing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();





    }
}