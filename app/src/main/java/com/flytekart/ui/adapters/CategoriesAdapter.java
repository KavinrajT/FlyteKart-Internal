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

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<Category> categories;

    public CategoriesAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.getTvCategoryName().setText(category.getName());
        if (category.isIsActive()) {
            holder.getTvCategoryStatus().setText(R.string.active);
        } else {
            holder.getTvCategoryStatus().setText(R.string.inactive);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategoryName;
        private final TextView tvCategoryStatus;

        public CategoryViewHolder(View view) {
            super(view);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvCategoryStatus = view.findViewById(R.id.tv_category_status);
        }

        public TextView getTvCategoryName() {
            return tvCategoryName;
        }

        public TextView getTvCategoryStatus() {
            return tvCategoryStatus;
        }
    }
}
