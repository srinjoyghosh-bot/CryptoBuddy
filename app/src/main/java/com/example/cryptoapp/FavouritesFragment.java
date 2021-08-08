package com.example.cryptoapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavouritesFragment extends Fragment {
    private EmptyRecyclerView mRecyclerView;
    private FavouritesAdapter mAdapter;
    private DatabaseReference favouritesReference;
    private TextView EmptyView;




    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_favourites,container,false);
        getActivity().setTitle("Favourites");
        setHasOptionsMenu(true);
        mRecyclerView=view.findViewById(R.id.favourites_rv);
        favouritesReference= FirebaseDatabase.getInstance().getReference().child("favourites");

        mRecyclerView.setLayoutManager(new LayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false));
        EmptyView=view.findViewById(R.id.empty_view);
        mRecyclerView.setEmptyView(EmptyView);







        FirebaseRecyclerOptions<Crypto> options=new FirebaseRecyclerOptions.Builder<Crypto>()
                .setQuery(favouritesReference,Crypto.class)
                .build();


        mAdapter=new FavouritesAdapter(options);


        mRecyclerView.setAdapter(mAdapter);









        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int index=viewHolder.getAdapterPosition();
                Crypto crypto=mAdapter.getItem(index);

                mAdapter.deleteItem(index);
                CryptoDbHelper cryptoDbHelper=new CryptoDbHelper(getContext(),null);
                cryptoDbHelper.removeFromFavourite(crypto.getCryptoName(),crypto.getCryptoAbbreviation());
                Toast.makeText(getContext(), "Item Removed From Favourites", Toast.LENGTH_SHORT).show();




            }

            @Override
            public void onChildDrawOver(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange))
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .create()
                        .decorate();
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DataSnapshot dataSnapshot, int position) {
                Crypto crypto=dataSnapshot.getValue(Crypto.class);
                Intent intent=new Intent(getActivity(),CryptoActivity.class);
                intent.putExtra("cryptoObject",crypto);
                startActivity(intent);
            }
        });


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





}
