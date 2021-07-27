package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.SubCategory;

import java.util.List;

public class SubCategoryRecyclerListAdapter extends RecyclerView.Adapter<SubCategoryRecyclerListAdapter.SubCategoryViewHolder> {

    private List<SubCategory> subCategories;

    public SubCategoryRecyclerListAdapter(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {
        SubCategory subCategory = subCategories.get(position);
        if (subCategory != null) {
            holder.getTextView().setText(subCategory.getName());
        }
    }

    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    public static class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public SubCategoryViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_store_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
