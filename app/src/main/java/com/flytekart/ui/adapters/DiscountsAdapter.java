package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Category;
import com.flytekart.models.Discount;

import java.util.List;

public class DiscountsAdapter extends RecyclerView.Adapter<DiscountsAdapter.DiscountViewHolder> {

    private DiscountClickListener discountClickListener;
    private List<Discount> discounts;

    public DiscountsAdapter(DiscountClickListener discountClickListener, List<Discount> discounts) {
        this.discountClickListener = discountClickListener;
        this.discounts = discounts;
    }

    @NonNull
    @Override
    public DiscountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_discount, parent, false);
        return new DiscountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscountViewHolder holder, int position) {
        Discount discount = discounts.get(position);
        holder.tvCategoryName.setText(discount.getName());
        if (discount.isIsActive()) {
            holder.tvCategoryStatus.setText(R.string.active);
        } else {
            holder.tvCategoryStatus.setText(R.string.inactive);
        }
        holder.llCategoryName.setOnClickListener(new CategoryNameClickListener(position));
    }

    @Override
    public int getItemCount() {
        return discounts.size();
    }

    private class CategoryNameClickListener implements View.OnClickListener {
        private int position;

        public CategoryNameClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            discountClickListener.onDiscountClicked(position);
        }
    }

    public interface DiscountClickListener {
        void onDiscountClicked(int position);
    }

    public static class DiscountViewHolder extends RecyclerView.ViewHolder {
        private final View llCategoryName;
        private final TextView tvCategoryName;
        private final TextView tvCategoryStatus;
        private final View ivCategoryEdit;
        private final View ivCategoryDelete;

        public DiscountViewHolder(View view) {
            super(view);
            llCategoryName = view.findViewById(R.id.ll_category_name);
            tvCategoryName = view.findViewById(R.id.tv_category_name);
            tvCategoryStatus = view.findViewById(R.id.tv_category_status);
            ivCategoryEdit = view.findViewById(R.id.iv_category_edit);
            ivCategoryDelete = view.findViewById(R.id.iv_category_delete);
        }
    }
}
