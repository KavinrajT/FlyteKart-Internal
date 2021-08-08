package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.CategoryStoreCategoryDTO;

import java.util.List;

public class CategoryStoreCategoriesAdapter extends RecyclerView.Adapter<CategoryStoreCategoriesAdapter.CategoryViewHolder> {

    private CategoryClickListener categoryClickListener;
    private List<CategoryStoreCategoryDTO> categories;

    public CategoryStoreCategoriesAdapter(CategoryClickListener categoryClickListener, List<CategoryStoreCategoryDTO> categories) {
        this.categoryClickListener = categoryClickListener;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryStoreCategoryDTO category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());
        if (category.isActive()) {
            holder.tvCategoryStatus.setText(R.string.active);
        } else {
            holder.tvCategoryStatus.setText(R.string.inactive);
        }
        holder.llCategoryName.setOnClickListener(new CategoryNameClickListener(position));
        holder.ivCategoryEdit.setOnClickListener(new EditClickListener(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    private class CategoryNameClickListener implements View.OnClickListener {
        private int position;

        public CategoryNameClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            categoryClickListener.onCategoryClicked(position);
        }
    }

    private class EditClickListener implements View.OnClickListener {
        private int position;

        public EditClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            categoryClickListener.onEdit(position);
        }
    }

    public interface CategoryClickListener {
        void onCategoryClicked(int position);
        void onEdit(int position);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final View llCategoryName;
        private final TextView tvCategoryName;
        private final TextView tvCategoryStatus;
        private final View ivCategoryEdit;

        public CategoryViewHolder(View view) {
            super(view);
            llCategoryName = view.findViewById(R.id.ll_category_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvCategoryStatus = view.findViewById(R.id.tv_category_status);
            ivCategoryEdit = view.findViewById(R.id.iv_category_edit);
        }
    }
}
