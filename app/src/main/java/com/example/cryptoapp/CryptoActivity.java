package com.example.cryptoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CryptoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    public final String TAG="CryptoActivity";

    private Spinner mSpinner;
    private TextView mCryptoRateView;
    private LineChart mChart;
    private LocalDateTime nowDate=LocalDateTime.now();
    private LocalDateTime prevDate=nowDate.minusDays(6);
    private Crypto currentCrypto;

    private Button startDate;
    private Button endDate;
    private Button goButton;
    private String abbreviation;
    private CryptoRate cryptoRate;
    private boolean isPrevDateChanged;
    private Button dateButtonToBeUpdated;
    private String assetQuoteSelected="USD";
    private String quoteSelection;
    private CryptoDbHelper dbHelper;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFavouritesDatabaseReference;

    private Menu menuCrypto;
    private double liveRate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);
        Intent intent=getIntent();
        currentCrypto=(Crypto)intent.getSerializableExtra("cryptoObject");

        String name=currentCrypto.getCryptoName();

        abbreviation=currentCrypto.getCryptoAbbreviation();
        setTitle(name+" ("+abbreviation+")");
        List<String> currencyList = new ArrayList<String>();
        currencyList.add("US Dollar(USD)");
        currencyList.add("Euro(EUR)");
        currencyList.add("Yen(JPY)");
        currencyList.add("Swiss Franc(CHF)");
        currencyList.add("Indian Rupee(INR)");
        mSpinner=(Spinner)findViewById(R.id.currency_spinner);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> currencySpinnerAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,currencyList);
        currencySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(currencySpinnerAdapter);
        mChart=(LineChart) findViewById(R.id.chart);
        mChart.setNoDataText("No data available from API");
        mChart.setNoDataTextColor(ContextCompat.getColor(CryptoActivity.this,R.color.orange));
        Paint p = mChart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(60f);
        startDate=(Button) findViewById(R.id.start_date);
        endDate=(Button) findViewById(R.id.end_date);
        goButton=(Button) findViewById(R.id.update_graph);


        mCryptoRateView=findViewById(R.id.crypto_rate_live);

        showCurrentRateAndGraph();

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPrevDateChanged=true;
                dateButtonToBeUpdated=startDate;
                showStartDatePicker();
                goButton.setEnabled(true);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPrevDateChanged=false;
                dateButtonToBeUpdated=endDate;
                showStartDatePicker();
                goButton.setEnabled(true);
            }
        });
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goButton.isEnabled()) {
                    makeGraph();
                    goButton.setEnabled(false);
                }
                else
                {
                    return ;
                }
            }
        });

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mFavouritesDatabaseReference=mFirebaseDatabase.getReference().child("favourites");
        dbHelper=new CryptoDbHelper(getApplicationContext(),null);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) parent.getChildAt(0)).setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.orange_border));
        ((TextView) parent.getChildAt(0)).setTextSize(18);
        ((TextView) parent.getChildAt(0)).setGravity(Gravity.RIGHT);
        quoteSelection=parent.getItemAtPosition(position).toString();
        int index=quoteSelection.indexOf("(");
        assetQuoteSelected=quoteSelection.substring(index+1,index+4);
        showCurrentRateAndGraph();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(parent.getContext(), "Nothing Selected ", Toast.LENGTH_LONG).show();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void makeGraph()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://rest.coinapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
        Call<List<CryptoTimeData>> call2=cryptoApi.getTimedResults(abbreviation,assetQuoteSelected,"1DAY",prevDate,nowDate);
        call2.enqueue(new Callback<List<CryptoTimeData>>() {
            @Override
            public void onResponse(Call<List<CryptoTimeData>> call, Response<List<CryptoTimeData>> response) {
                if (response.isSuccessful()) {
                    List<CryptoTimeData> timeDataList = response.body();
                    ArrayList<Entry> yValuesHigh = new ArrayList<Entry>();
                    ArrayList<Entry> yValuesLow = new ArrayList<Entry>();
                    int k = 0;
                    if(timeDataList.size()!=0) {
                        for (CryptoTimeData timeData : timeDataList) {
                            float roundedRateHigh = (float) (Math.round(timeData.getRate_high() * 100.0) / 100.0);
                            float roundedRateLow = (float) (Math.round(timeData.getRate_low() * 100.0) / 100.0);
                            yValuesHigh.add(new Entry(++k, roundedRateHigh));
                            yValuesLow.add(new Entry(k, roundedRateLow));
                        }
                        LineDataSet set1, set2;
                        set1 = new LineDataSet(yValuesHigh, "Daily Highest");
                        set2 = new LineDataSet(yValuesLow, "Daily Lowest");
                        LineData data = new LineData(set1, set2);
                        mChart.setData(data);

                        mChart.getAxisLeft().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                        mChart.getAxisRight().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                        mChart.getXAxis().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
//
                        mChart.getLegend().setEnabled(false);
                        mChart.getDescription().setEnabled(false);
                        mChart.setExtraOffsets(5f, 5f, 5f, 15f);
                        set1.setColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                        set2.setColor(ContextCompat.getColor(getApplicationContext(),R.color.light_blue));
                        set1.setValueTextColor(Color.WHITE);
                        set1.setValueTextSize(10f);
                        set2.setValueTextColor(Color.WHITE);
                        set2.setValueTextSize(10f);
                        set1.setDrawValues(false);
                        set2.setDrawValues(false);
                        IMarker marker=new CustomMarkerView(getApplicationContext(),R.layout.graph_marker);
                        mChart.setMarker(marker);
                        mChart.setNoDataText("No data available from API");
                        mChart.setNoDataTextColor(ContextCompat.getColor(getApplicationContext(),R.color.orange));
                        Paint p = mChart.getPaint(Chart.PAINT_INFO);
                        if (p != null) {
                            p.setTextSize(12);
                        }

                        mChart.animateX(1000);
                        mChart.invalidate();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No Data Available From Api", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CryptoTimeData>> call, Throwable t) {
                Log.e("OnFailure MakeGraph","failure");
            }
        });
    }
    private void showStartDatePicker()
    {
        DatePickerDialog datePickerDialog=new DatePickerDialog(this,this, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String monthString=String.valueOf(month+1);
        String dayString=String.valueOf(dayOfMonth);
        if(month<9)
        {
            monthString="0"+(month+1);
        }
        if (dayOfMonth<10)
        {
            dayString="0"+dayOfMonth;
        }
        String dateString=year+"-"+monthString+"-"+dayString;
        LocalDateTime changedDate=LocalDateTime.parse(dateString+"T00:00:00");

        dateButtonToBeUpdated.setText(dateString);
        if(isPrevDateChanged)
        {
            prevDate=changedDate;
        }
        else
        {
            nowDate=changedDate;
        }
    }
    private void showCurrentRateAndGraph()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://rest.coinapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
        Call<CryptoRate> call=cryptoApi.getRate(abbreviation,assetQuoteSelected);
        call.enqueue(new Callback<CryptoRate>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CryptoRate> call, Response<CryptoRate> response) {
                if (!response.isSuccessful())
                {
                    Log.e("OnResponse showGraph","response not successful");
                }
                else
                {
                    cryptoRate=response.body();
                    double rate=cryptoRate.getRate();
                    double roundedDouble = Math.round(rate * 100.0) / 100.0;
                    liveRate=roundedDouble;
                    mCryptoRateView.setText(String.valueOf(roundedDouble));//
                    String nowDateString=String.valueOf(nowDate);
                    nowDateString=nowDateString.substring(0,nowDateString.indexOf("T"));
                    String prevDateString=String.valueOf(prevDate);
                    prevDateString=prevDateString.substring(0,prevDateString.indexOf("T"));
                    startDate.setText(prevDateString);
                    endDate.setText(nowDateString);
                    mChart.getDescription().setText("Last 7 days activity");
                    mChart.getDescription().setTextColor(Color.WHITE);
                    makeGraph();
                }
            }

            @Override
            public void onFailure(Call<CryptoRate> call, Throwable t) {
                Log.e("OnFailure","failure");

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuCrypto=menu;
        getMenuInflater().inflate(R.menu.crypto_menu,menu);

        if (currentCrypto.isFavourite())
        {
            menu.findItem(R.id.fav_heart).setIcon(R.drawable.heart);
            menu.findItem(R.id.fav_heart).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId()==R.id.fav_heart) {
            if (item.isChecked()) {

                item.setIcon(R.drawable.outline_favorite_24);
                item.setChecked(false);
                Query cryptoQuery = mFavouritesDatabaseReference.orderByChild("Cname").equalTo(currentCrypto.getCryptoName());
                cryptoQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cryptoSnapshot : snapshot.getChildren()) {
                            cryptoSnapshot.getRef().removeValue();
                            dbHelper.removeFromFavourite(currentCrypto.getCryptoName(),currentCrypto.getCryptoAbbreviation());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled", error.toException());
                    }
                });
                Toast.makeText(getApplicationContext(),"Removed from Favourites",Toast.LENGTH_LONG).show();
            } else {

                item.setIcon(R.drawable.heart);
                item.setChecked(true);
                currentCrypto.setFavourite(true);
                mFavouritesDatabaseReference.push().setValue(currentCrypto);

                dbHelper.updateToFavourite(currentCrypto.getCryptoName(),currentCrypto.getCryptoAbbreviation());

                Toast.makeText(getApplicationContext(),"Added to Favourites",Toast.LENGTH_LONG).show();
            }
        }
        if(item.getItemId()==R.id.add_target)
        {
            Intent intent=new Intent(CryptoActivity.this,SetTargetActivity.class);
            intent.putExtra("targetCrypto",currentCrypto);
            intent.putExtra("assetQuote",quoteSelection);
            intent.putExtra("currentRate",liveRate);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
