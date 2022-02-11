package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Store;
import com.flytekart.utils.Utilities;

import java.util.List;

public class StoresSelectionAdapter extends RecyclerView.Adapter<StoresSelectionAdapter.StoreViewHolder> {

    private List<Store> stores;

    public StoresSelectionAdapter(List<Store> stores) {
        this.stores = stores;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);
        holder.tvStoreName.setText(store.getName());
        String addressString = Utilities.getAddressString(store.getAddress());
        holder.tvStoreAddress.setText(addressString);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStoreName;
        private TextView tvStoreAddress;

        public StoreViewHolder(View view) {
            super(view);
            tvStoreName = view.findViewById(R.id.tv_store_name);
            tvStoreAddress = view.findViewById(R.id.tv_store_address);
        }
    }
}
