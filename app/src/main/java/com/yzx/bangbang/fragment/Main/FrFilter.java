package com.yzx.bangbang.fragment.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.yzx.bangbang.R;
import com.yzx.bangbang.model.main.AssignmentFilter;
import com.yzx.bangbang.utils.FrMetro;
import com.yzx.bangbang.utils.sql.SA;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FrFilter extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_fr_filter_assignment, container, false);
        init();
        return v;
    }

    private void init() {
        ButterKnife.bind(this, v);
        // filter = (AssignmentFilter) getActivity().getIntent().getSerializableExtra("filter");
        filter = (AssignmentFilter) SA.query(SA.TYPE_ASSIGNMENT_FILTER);
        checkbox_employer.setChecked(filter.employer);
        checkbox_employer.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (!isChecked && !checkbox_freelancer.isChecked()) {
                toast();
                checkbox_employer.setChecked(true);
            }
        });
        checkbox_freelancer.setChecked(filter.freelancer);
        checkbox_freelancer.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (!isChecked && !checkbox_employer.isChecked()) {
                toast();
                checkbox_freelancer.setChecked(true);
            }
        });
    }

    private void toast() {
        Toast.makeText(getActivity(), "至少选择一个选项", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_apply)
    void apply() {
        filter.employer = checkbox_employer.isChecked();
        filter.freelancer = checkbox_freelancer.isChecked();
        back();
    }

    @OnClick(R.id.button_back)
    void back() {
        getActivity().finish();
    }

    //  CheckBox[] checkBoxes;
    @BindView(R.id.checkbox_employer)
    CheckBox checkbox_employer;
    @BindView(R.id.checkbox_freelancer)
    CheckBox checkbox_freelancer;
    AssignmentFilter filter;
    // public FrMetro fm;
    View v;
}
