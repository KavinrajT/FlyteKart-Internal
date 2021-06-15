package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;

import java.util.List;

public class CategoryRecyclerListAdapter extends RecyclerView.Adapter<CategoryRecyclerListAdapter.CategoryViewHolder> {

    private List<Category> categories;

    public CategoryRecyclerListAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_store, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        if (category != null) {
            holder.getTextView().setText(category.getName());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public CategoryViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_store_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
