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

public class CategoriesSelectionAdapter extends RecyclerView.Adapter<CategoriesSelectionAdapter.CategoryViewHolder> {

    private List<Category> categories;

    public CategoriesSelectionAdapter(List<Category> categories) {
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
        holder.tvCategoryName.setText(category.getName());
        if (category.isIsActive()) {
            holder.tvCategoryStatus.setText(R.string.active);
        } else {
            holder.tvCategoryStatus.setText(R.string.inactive);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final View llCategoryName;
        private final TextView tvCategoryName;
        private final TextView tvCategoryStatus;
        private final View ivCategoryEdit;
        private final View ivCategoryDelete;

        public CategoryViewHolder(View view) {
            super(view);
            llCategoryName = view.findViewById(R.id.ll_category_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvCategoryStatus = view.findViewById(R.id.tv_category_status);
            ivCategoryEdit = view.findViewById(R.id.iv_category_edit);
            ivCategoryDelete = view.findViewById(R.id.iv_category_delete);
        }
    }
}
