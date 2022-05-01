package com.up959875.mobiletraveljournal.fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.up959875.mobiletraveljournal.databinding.FragmentHomeBinding;
import com.up959875.mobiletraveljournal.adapters.RouteExploreAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.up959875.mobiletraveljournal.R;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private FragmentHomeBinding binding;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setListener();
        initExploreRouteAdapter();
        return view;
    }

    private void initExploreRouteAdapter() {
        List<Route> routes = new ArrayList<>();
        routes.add(new Route(R.drawable.ic_avatar, "Placeholder Route Title", "Placeholder Route Description"));
        routes.add(new Route(R.drawable.ic_avatar, "title2", "desc2"));
        routes.add(new Route(R.drawable.ic_avatar, "title3", "desc3"));

        RouteExploreAdapter adapter = new RouteExploreAdapter(true, routes, getContext());
        binding.homeExploreViewpager.setAdapter(adapter);
        binding.homeExploreViewpager.setPadding(50, 0, 50, 0);
    }

    private boolean isLoggedInUser() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void setListener() {
        binding.homeSearchFriendsButton.setOnClickListener(this);
        binding.homeOthersMap.setOnClickListener(this);
        binding.homeSearchRouteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_search_friends_button:
                if (isLoggedInUser())
                    changeFragment(SearchFriendFragment.newInstance());
                else
                    Log.d("l57", "ok");
                break;
            case R.id.home_others_map:
                changeFragment(ExploreMapFragment.newInstance());
                break;
            case R.id.home_search_route_button:
                changeFragment(SearchRouteFragment.newInstance());
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
