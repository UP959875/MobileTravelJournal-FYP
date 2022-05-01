package com.up959875.mobiletraveljournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.databinding.FragmentProfileLoginBinding;
import com.up959875.mobiletraveljournal.databinding.FragmentRouteStartBinding;

public class RouteStartFragment extends BaseFragment implements View.OnClickListener {

    private FragmentRouteStartBinding binding;
    private TextView buttonStartTracking;

    public static RouteStartFragment newInstance() {

        return new RouteStartFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRouteStartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setListeners();
        return view;
    }

    private void setListeners() {

        binding.routeStartButton.setOnClickListener(this);


    }

    private void findViews(View view) {
        buttonStartTracking = view.findViewById(R.id.route_start_button);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_start_button:
                changeFragment(RouteFragment.newInstance());
        }
    }

    private void changeFragment(Fragment next) {
        getNavigationInteractions().changeFragment(this, next, true);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
