package com.flytekart.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flytekart.R;
import com.flytekart.models.Organisation;
import com.flytekart.ui.activity.CreateOrgActivity;
import com.flytekart.ui.adapters.OrganisationRecyclerListAdapter;
import com.flytekart.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganisationListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganisationListFragment extends Fragment {

    private static final String ARG_ITEM_ORGANISATIONS = "organisations";

    private LinearLayout llNoRecordsFound;
    private RecyclerView rvOrganisationList;
    private OrganisationRecyclerListAdapter adapter;

    private List<Organisation> organisations;

    public OrganisationListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param organisations list of all organisations created by the user.
     * @return A new instance of fragment OrganisationListFragment.
     */
    public static OrganisationListFragment newInstance(List<Organisation> organisations) {
        OrganisationListFragment fragment = new OrganisationListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEM_ORGANISATIONS, (ArrayList<? extends Parcelable>) organisations);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            organisations = getArguments().getParcelableArrayList(ARG_ITEM_ORGANISATIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organisation_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llNoRecordsFound = view.findViewById(R.id.ll_no_records_found);
        rvOrganisationList = view.findViewById(R.id.rv_stores_list);

        setData();
    }

    private void setData() {
        if (organisations == null || organisations.isEmpty()) {
            rvOrganisationList.setVisibility(View.GONE);
            llNoRecordsFound.setVisibility(View.VISIBLE);
            llNoRecordsFound.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CreateOrgActivity.class);
                startActivityForResult(intent, Constants.CREATE_ORG_ACTIVITY_REQUEST_CODE);
            });
        } else {
            llNoRecordsFound.setVisibility(View.GONE);
            rvOrganisationList.setVisibility(View.VISIBLE);

            adapter = new OrganisationRecyclerListAdapter(organisations);
            rvOrganisationList.setAdapter(adapter);
            rvOrganisationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}