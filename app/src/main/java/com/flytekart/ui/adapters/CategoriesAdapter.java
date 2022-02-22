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

    private CategoryClickListener discountClickListener;
    private List<Category> categories;

    public CategoriesAdapter(CategoryClickListener discountClickListener, List<Category> categories) {
        this.discountClickListener = discountClickListener;
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
        holder.llCategoryName.setOnClickListener(new CategoryNameClickListener(position));
        holder.ivCategoryEdit.setOnClickListener(new EditClickListener(position));
        holder.ivCategoryDelete.setOnClickListener(new DeleteClickListener(position));
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
            discountClickListener.onCategoryClicked(position);
        }
    }

    private class EditClickListener implements View.OnClickListener {
        private int position;

        public EditClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            discountClickListener.onEdit(position);
        }
    }

    private class DeleteClickListener implements View.OnClickListener {
        private int position;

        public DeleteClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            discountClickListener.onDelete(position);
        }
    }

    public interface CategoryClickListener {
        void onCategoryClicked(int position);
        void onEdit(int position);
        void onDelete(int position);
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
