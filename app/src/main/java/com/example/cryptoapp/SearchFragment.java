package com.example.cryptoapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFragment extends Fragment implements CryptoLiveListAdapter.OnCryptoListener {

    private TextView mTextView;
    private List<CryptoLive> cryptoList;
    public List<CryptoLive> notCryptoList;
    private List<Crypto> dbCryptoList;
    private RecyclerView mRecyclerView;
    private EditText mSearchText;
    private CryptoLiveListAdapter mAdapter;
    private  boolean isListFiltered=false;
    private List<CryptoLive> filteredList;
    private List<CryptoLive> finalCryptoList;
    private KeyListener searchKeyListener;
    private View ProgressView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton upwardFAB;
    private CryptoDbHelper dbHelper;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);
        getActivity().setTitle("Search");
        cryptoList=new ArrayList<CryptoLive>();
        notCryptoList=new ArrayList<CryptoLive>();
        mRecyclerView=view.findViewById(R.id.crypto_list);
        mSearchText=view.findViewById(R.id.search_text);
        searchKeyListener=mSearchText.getKeyListener();
        mSearchText.setKeyListener(null);
        upwardFAB=view.findViewById(R.id.upward_arrow);

        ProgressView=view.findViewById(R.id.progress);

        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        dbHelper=new CryptoDbHelper(getContext(),null);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://rest.coinapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
        Call<List<CryptoLive>> call=cryptoApi.getCryptos();
        call.enqueue(new Callback<List<CryptoLive>>() {
            @Override
            public void onResponse(Call<List<CryptoLive>> call, Response<List<CryptoLive>> response) {
                if (response.isSuccessful())
                {

                    List<CryptoLive> cryptos=response.body();
                    for (CryptoLive crypto:cryptos)
                    {

                        if (crypto.getType()==1)
                        {

                            cryptoList.add(crypto);


                        }
                        else
                        {
                            notCryptoList.add(crypto);
                        }
                    }
                    finalCryptoList=cryptoList;
                    mAdapter=new CryptoLiveListAdapter(cryptoList,SearchFragment.this::onCryptoClick);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ProgressView.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mSearchText.setKeyListener(searchKeyListener);

                        }
                    });



                }

                else
                {

                    Toast.makeText(getActivity(), "No data from API", Toast.LENGTH_LONG).show();
                    Log.e("OnResponse CryptoList","response unsuccessful");

                }

            }
            @Override
            public void onFailure(Call<List<CryptoLive>> call, Throwable t) {
                Log.e("onFailure error",t.getMessage());
                ProgressView.setVisibility(View.VISIBLE);

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (CryptoLive crypto:cryptoList
                ) {
                    dbHelper.addCrypto(crypto);

                }
            }
        }).start();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Retrofit retrofit=new Retrofit.Builder()
                        .baseUrl("https://rest.coinapi.io/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
                Call<List<CryptoLive>> call=cryptoApi.getCryptos();
                call.enqueue(new Callback<List<CryptoLive>>() {
                    @Override
                    public void onResponse(Call<List<CryptoLive>> call, Response<List<CryptoLive>> response) {
                        if (response.isSuccessful())
                        {
                            List<CryptoLive> cryptos=response.body();
                            cryptoList.clear();
                            for (CryptoLive crypto:cryptos)
                            {
                                if (crypto.getType()==1)
                                {

                                    cryptoList.add(crypto);
                                }
                            }
                            finalCryptoList=cryptoList;
                            mAdapter=new CryptoLiveListAdapter(cryptoList,SearchFragment.this::onCryptoClick);
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    ProgressView.setVisibility(View.INVISIBLE);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mSearchText.setKeyListener(searchKeyListener);
                                }
                            });
                        }
                        else
                        {
                            View noDataView=view.findViewById(R.id.no_data);
                            noDataView.setVisibility(View.VISIBLE);
                            ProgressView.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "No data from API", Toast.LENGTH_LONG).show();
                            Log.e("OnResponse CryptoList","response unsuccessful");

                        }

                    }
                    @Override
                    public void onFailure(Call<List<CryptoLive>> call, Throwable t) {
                        Log.e("onFailure error",t.getMessage());

                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView,null,0);
                filter(s.toString());

            }
        });
        upwardFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView,null,0);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                if (dy==0)
                {
                    upwardFAB.setEnabled(false);
                }
                else
                {
                    upwardFAB.setEnabled(true);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        return view;
    }



    @Override
    public void onCryptoClick(int position) {
        CryptoLive currentCrypto;


        if (isListFiltered)
        {
            currentCrypto=filteredList.get(position);
        }
        else
        {
            currentCrypto=cryptoList.get(position);
        }

        SQLiteDatabase database=dbHelper.getWritableDatabase();
        boolean isFav=dbHelper.checkIfFavourite(currentCrypto.getName());

        Crypto crypto = new Crypto(currentCrypto.getName(), currentCrypto.getAsset_Id(), isFav);

        Intent intent=new Intent(getActivity(),CryptoActivity.class);
        intent.putExtra("cryptoObject", crypto);
        startActivity(intent);

    }

    public void filter(String text){

        filteredList = new ArrayList<>();
        for (CryptoLive cryptoLive : cryptoList) {
            if (cryptoLive.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(cryptoLive);
            }

        }
        if (text.isEmpty()||text==null)
        {
            isListFiltered=false;
        }
        else
            isListFiltered=true;
        if (filteredList.isEmpty())
        {
            Toast toast=  Toast.makeText(getContext(),"No Results Found",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP,0,0);
            toast.show();
        }

        mAdapter.filterList(filteredList);

    }


    public int isCryptoAvailable(CryptoLive cryptoLive)
    {
        final int[] k = new int[1];
        String abbreviation=cryptoLive.getAsset_Id();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://rest.coinapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
        Call<CryptoRate> call=cryptoApi.getRate(abbreviation,"USD");
        call.enqueue(new Callback<CryptoRate>() {
            @Override
            public void onResponse(Call<CryptoRate> call, Response<CryptoRate> response) {
                if (response.isSuccessful())
                    k[0] =1;
                else
                    k[0]=0;

            }
            @Override
            public void onFailure(Call<CryptoRate> call, Throwable t) {
                k[0]=0;

            }
        });
        return k[0];
    }
    public List<CryptoLive> getCurrencyList(){
        return notCryptoList;
    }
}
