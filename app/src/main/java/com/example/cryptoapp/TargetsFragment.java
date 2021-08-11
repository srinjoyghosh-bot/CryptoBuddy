package com.example.cryptoapp;

import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TargetsFragment extends Fragment {
    private EmptyRecyclerView mRecyclerView;
    private TargetsAdapter mAdapter;
    private DatabaseReference targetReference;
    private TextView emptyView;
    private List<CryptoTargets> targetCryptoList;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_targets,container,false);
        getActivity().setTitle("Targets");
        setHasOptionsMenu(true);

        mRecyclerView=view.findViewById(R.id.targets_rv);
        emptyView=view.findViewById(R.id.empty_text);

        targetReference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("targets");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setEmptyView(emptyView);


        FirebaseRecyclerOptions<CryptoTargets> options=new FirebaseRecyclerOptions.Builder<CryptoTargets>()
                .setQuery(targetReference,CryptoTargets.class)
                .build();

        mAdapter=new TargetsAdapter(options);
        mRecyclerView.setAdapter(mAdapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int index=viewHolder.getAdapterPosition();
                CryptoTargets crypto=mAdapter.getItem(viewHolder.getAdapterPosition());
                mAdapter.deleteItem(viewHolder.getAdapterPosition());

                Snackbar.make(mRecyclerView,"Removed from Targets", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAdapter.addItem(crypto,index);
                            }
                        }).show();
            }

            @Override
            public void onChildDrawOver(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.orange))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mRecyclerView);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delete_menu,menu);
         super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.delete_all)
        {
            targetReference.removeValue();
            mAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
    private double getCurrentRate(String assetCode,String assetQuote){
        final double[] roundedRate = new double[1];

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://rest.coinapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApi cryptoApi=retrofit.create(CryptoApi.class);
        Call<CryptoRate> call=cryptoApi.getRate(assetCode,assetQuote);
        call.enqueue(new Callback<CryptoRate>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<CryptoRate> call, Response<CryptoRate> response) {
                if (!response.isSuccessful())
                {
                    Log.e("OnResponse showGraph","response not successful");
                    roundedRate[0]=-1;
                }
                else
                {
                    CryptoRate cryptoRate;
                    cryptoRate =response.body();
                    double rate= cryptoRate.getRate();
                    roundedRate[0] = Math.round(rate * 100.0) / 100.0;


                }
            }

            @Override
            public void onFailure(Call<CryptoRate> call, Throwable t) {
                roundedRate[0]=-1;
                Log.e("OnFailure","failure");

            }
        });
        return roundedRate[0];
    }
}
