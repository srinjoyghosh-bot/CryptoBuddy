package com.example.cryptoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class TargetsAdapter extends FirebaseRecyclerAdapter<CryptoTargets, TargetsAdapter.targetViewholder> {
    private DatabaseReference deletedRef;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TargetsAdapter(@NonNull @NotNull FirebaseRecyclerOptions<CryptoTargets> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull TargetsAdapter.targetViewholder holder, int position, @NonNull @NotNull CryptoTargets model) {
        holder.cryptoName.setText(model.getCryptoName()+" ("+model.getAsset_Id()+")");
        int index=model.getAsset_Quote().indexOf("(");
        String quote= (model.getAsset_Quote()).substring(index+1,index+4);
        holder.quoteAsset.setText(quote);
        holder.target.setText(String.valueOf(model.getTargetPrice()));
        holder.stopLoss.setText(String.valueOf(model.getStopLossPrice()));
    }

    @NonNull
    @NotNull
    @Override
    public TargetsAdapter.targetViewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.target_item,parent,false);
        return new TargetsAdapter.targetViewholder(view);
    }
    class targetViewholder extends RecyclerView.ViewHolder {
        TextView cryptoName,quoteAsset,target,stopLoss;
        public targetViewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            cryptoName=itemView.findViewById(R.id.name_asset);
            quoteAsset=itemView.findViewById(R.id.quote_asset);
            target=itemView.findViewById(R.id.target_value);
            stopLoss=itemView.findViewById(R.id.stop_loss_value);
        }
    }
    public void deleteItem(int position)
    {
        deletedRef=getSnapshots().getSnapshot(position).getRef();
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }
    public void addItem(CryptoTargets crypto,int position)
    {
        DatabaseReference favRef= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("targets");
        favRef.child(deletedRef.getKey()).setValue(crypto);
        notifyDataSetChanged();
    }

}
