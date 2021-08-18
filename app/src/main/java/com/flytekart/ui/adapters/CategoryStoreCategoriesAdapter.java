package com.flytekart.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.CategoryStoreCategoryDTO;

import java.util.List;

public class CategoryStoreCategoriesAdapter extends RecyclerView.Adapter<CategoryStoreCategoriesAdapter.CategoryViewHolder> {

    private Context context;
    private CategoryClickListener productClickListener;
    private List<CategoryStoreCategoryDTO> categories;

    public CategoryStoreCategoriesAdapter(CategoryClickListener productClickListener, List<CategoryStoreCategoryDTO> categories) {
        this.productClickListener = productClickListener;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryStoreCategoryDTO category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());
        boolean isActive = false;
        if (category.getStoreCategoryId() != null && category.getStoreCategoryDeletedAt() == null) {
            isActive = true;
        }

        holder.ivCategoryEnabled.setImageResource(R.drawable.ic_check_filled);
        if (isActive) {
            holder.ivCategoryEnabled.setColorFilter(ContextCompat.getColor(context, R.color.phone_green));
            holder.tvCategoryStatus.setText(R.string.available);
            CategoryNameClickListener categoryNameClickListener = new CategoryNameClickListener(position);
            holder.llCategoryName.setOnClickListener(categoryNameClickListener);
            holder.ivCategoryNext.setOnClickListener(categoryNameClickListener);
            holder.ivCategoryNext.setVisibility(View.VISIBLE);
        } else {
            holder.ivCategoryEnabled.setColorFilter(ContextCompat.getColor(context, R.color.dark_grey));
            holder.tvCategoryStatus.setText(R.string.not_available);
            holder.llCategoryName.setOnClickListener(null);
            holder.ivCategoryNext.setOnClickListener(null);
            holder.ivCategoryNext.setVisibility(View.INVISIBLE);
        }


        holder.ivCategoryEnabled.setOnClickListener(new EditClickListener(position));
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
            productClickListener.onCategoryClicked(position);
        }
    }

    private class EditClickListener implements View.OnClickListener {
        private int position;

        public EditClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            productClickListener.onEdit(position);
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
        private final ImageView ivCategoryEnabled;
        private final View ivCategoryNext;

        public CategoryViewHolder(View view) {
            super(view);
            llCategoryName = view.findViewById(R.id.ll_category_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvCategoryStatus = view.findViewById(R.id.tv_category_status);
            ivCategoryEnabled = view.findViewById(R.id.iv_category_enabled);
            ivCategoryNext = view.findViewById(R.id.iv_category_next);
        }
    }
}
