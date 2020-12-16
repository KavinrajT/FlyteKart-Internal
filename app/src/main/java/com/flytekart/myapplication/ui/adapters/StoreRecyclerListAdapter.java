package com.flytekart.myapplication.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.myapplication.R;
import com.flytekart.myapplication.models.Store;

import java.util.List;

public class StoreRecyclerListAdapter extends RecyclerView.Adapter<StoreRecyclerListAdapter.StoreViewHolder> {

    private List<Store> stores;

    public StoreRecyclerListAdapter(List<Store> stores) {
        this.stores = stores;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);
        if (store != null) {
            holder.getTextView().setText(store.getName());
        }
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public StoreViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_store_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
