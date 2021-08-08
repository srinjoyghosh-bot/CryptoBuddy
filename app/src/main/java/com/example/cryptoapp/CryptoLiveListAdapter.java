package com.example.cryptoapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CryptoLiveListAdapter extends RecyclerView.Adapter<CryptoLiveListAdapter.ViewHolder>{

    private List<CryptoLive> cryptoLiveList;
    private OnCryptoListener mOnCryptoListener;

    public CryptoLiveListAdapter(List<CryptoLive> cryptoLives, OnCryptoListener mOnCryptoListener)
    {
        cryptoLiveList=cryptoLives;
        this.mOnCryptoListener=mOnCryptoListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);


        View contactView = inflater.inflate(R.layout.list_item, parent, false);


        ViewHolder viewHolder = new ViewHolder(contactView,mOnCryptoListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoLiveListAdapter.ViewHolder holder, int position) {
        CryptoLive currentCrypto=cryptoLiveList.get(position);
        String name=currentCrypto.getName();
        String abbreviation=currentCrypto.getAsset_Id();
        String nameText=name+"("+abbreviation+")";
        TextView textView=holder.nameTextView;
        textView.setText(nameText);

    }

    @Override
    public int getItemCount() {
        return cryptoLiveList.size();
    }
    public void filterList(List<CryptoLive> filteredList)
    {
        cryptoLiveList=filteredList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nameTextView;
        public OnCryptoListener onCryptoListener;
        public ViewHolder(View itemView, OnCryptoListener onCryptoListener){
            super(itemView);
            nameTextView=(TextView) itemView.findViewById(R.id.asset_name);
            this.onCryptoListener=onCryptoListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onCryptoListener.onCryptoClick(position);

        }
    }
    public interface OnCryptoListener{
        void onCryptoClick(int position);
    }

}
