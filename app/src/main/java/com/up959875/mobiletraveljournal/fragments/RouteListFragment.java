package com.up959875.mobiletraveljournal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import com.up959875.mobiletraveljournal.databinding.FragmentRouteListBinding;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.R;
//Fragment to display the list of markers made during tracking.
public class RouteListFragment extends BaseFragment implements View.OnClickListener {

    private FragmentRouteListBinding binding;

    static RouteListFragment newInstance() {
        return new RouteListFragment();
}

    private void setListeners() {
        binding.routeListArrowButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.route_list_arrow_button:
                if (getParentFragmentManager().getBackStackEntryCount() > 0)
                    getParentFragmentManager().popBackStack();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRouteListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setListeners();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}
