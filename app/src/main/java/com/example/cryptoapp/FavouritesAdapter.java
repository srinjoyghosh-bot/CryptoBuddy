package com.example.cryptoapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import okhttp3.internal.cache.DiskLruCache;

public class FavouritesAdapter extends FirebaseRecyclerAdapter<Crypto,FavouritesAdapter.favouritesViewHolder> {
    private OnItemClickListener listener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FavouritesAdapter(@NonNull @NotNull FirebaseRecyclerOptions<Crypto> options) {
        super(options);
    }



    @NonNull
    @NotNull
    @Override
    public favouritesViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item,parent,false);
        return new FavouritesAdapter.favouritesViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull @NotNull favouritesViewHolder holder, int position, @NonNull @NotNull Crypto model) {
        holder.cName.setText(model.getCryptoName());
        holder.cAsset.setText(model.getCryptoAbbreviation());

    }

    class favouritesViewHolder extends RecyclerView.ViewHolder {
        TextView cName,cAsset;
        public favouritesViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cName=itemView.findViewById(R.id.crypto_name);
            cAsset=itemView.findViewById(R.id.asset_code);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION&&listener!=null)
                    {
                        listener.OnItemClick(getSnapshots().getSnapshot(position),position);
                    }


                }
            });
        }
    }
    public interface  OnItemClickListener{
        void OnItemClick(DataSnapshot dataSnapshot,int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public void deleteItem(int position)
    {
        getSnapshots().getSnapshot(position).getRef().removeValue();


    }



}
