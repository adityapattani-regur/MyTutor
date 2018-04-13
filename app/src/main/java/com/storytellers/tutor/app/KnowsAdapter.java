package com.storytellers.tutor.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class KnowsAdapter extends RecyclerView.Adapter<KnowsAdapter.MyViewHolder> {

    private Context context;
    private List<String> knowsList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView knowsText;

        MyViewHolder(final View itemView) {
            super(itemView);
            knowsText = itemView.findViewById(R.id.knows_text);
        }
    }

    KnowsAdapter(Context context, List<String> knowsList) {
        this.context = context;
        this.knowsList = knowsList;
    }

    @NonNull
    @Override
    public KnowsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.knows_item_list, parent, false);
        return new KnowsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull KnowsAdapter.MyViewHolder holder, int position) {
        holder.knowsText.setText(knowsList.get(position).trim());
    }

    @Override
    public int getItemCount() {
        return knowsList.size();
    }
}
