package com.flytekart.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Organisation;

import java.util.List;

public class OrganisationRecyclerListAdapter extends RecyclerView.Adapter<OrganisationRecyclerListAdapter.OrganisationViewHolder> {

    private List<Organisation> organisations;

    public OrganisationRecyclerListAdapter(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    @NonNull
    @Override
    public OrganisationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_store, parent, false);
        return new OrganisationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganisationViewHolder holder, int position) {
        Organisation organisation = organisations.get(position);
        if (organisation != null) {
            holder.getTextView().setText(organisation.getName());
        }
    }

    @Override
    public int getItemCount() {
        return organisations.size();
    }

    public static class OrganisationViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public OrganisationViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_store_name);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
