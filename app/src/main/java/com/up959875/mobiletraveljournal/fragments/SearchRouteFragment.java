package com.up959875.mobiletraveljournal.fragments;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.fragment.app.Fragment;
import android.view.inputmethod.InputMethodManager;

import com.up959875.mobiletraveljournal.databinding.FragmentSearchRouteBinding;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.adapters.FirebaseRouteAdapter;
import com.up959875.mobiletraveljournal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.up959875.mobiletraveljournal.other.SearchViewListener;
import com.up959875.mobiletraveljournal.other.Constants;

public class SearchRouteFragment extends BaseFragment {

    private FragmentSearchRouteBinding binding;
    private FirebaseRouteAdapter adapter;
    private CollectionReference routeRef;
    private FirebaseUser loggedInUser;
    private PagedList.Config routePagingConfig;


    public static SearchRouteFragment newInstance() {
        return new SearchRouteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchRouteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initRouteReference();
        setListener();
        focusOnSearchView();

        return view;
    }

    private void setListener() {
        binding.searchRouteArrow.setOnClickListener(view -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0)
                getParentFragmentManager().popBackStack();
        });
        binding.searchRouteSearchView.setOnQueryTextListener(new SearchViewListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.equals("")) {
                    searchRoute(s);
                } else {
                    binding.searchRouteRecyclerView.setAdapter(null);
                    binding.searchRouteMessage.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void focusOnSearchView() {
        if (getContext() != null) {
            binding.searchRouteSearchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    private void searchRoute(String title) {
        Query query = routeRef.orderBy(Constants.TITLE).startAt(title).endAt(title + "\uf8ff");
        Log.d("l87", (routeRef.getPath()));
        FirestorePagingOptions<Route> options = new FirestorePagingOptions.Builder<Route>()
                .setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(query, routePagingConfig, Route.class)
                .build();

        adapter = new FirebaseRouteAdapter(options, getContext());
        binding.searchRouteRecyclerView.swapAdapter(adapter, true);
        setAdapterOnItemClickListener();
        setAdapterObserver();
    }

    private void initRouteReference() {
        loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        routeRef = FirebaseFirestore.getInstance().collection(Constants.ROUTES);
        routePagingConfig = new PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(3).build();
    }

    private void setAdapterOnItemClickListener() {
        adapter.setOnItemClickListener((snapshot, position, view) -> {
            Route route = (Route) snapshot;


                    //showSnackBar(getResources().getString(R.string.friend_add_yourself), Snackbar.LENGTH_SHORT);

                    hideKeyboard();
                    changeFragment(RouteMapsFragment.newInstance(route));


        });
    }


    private void setAdapterObserver() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = adapter.getItemCount();
                if(totalNumberOfItems == 0) {
                    binding.searchRouteMessage.setVisibility(View.VISIBLE);
                    binding.searchRouteMessage.setText(getResources().getString(R.string.search_friend_no_friends));
                } else {
                    binding.searchRouteMessage.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void hideKeyboard() {
        if (getActivity() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }


    private void changeFragment(Fragment next) {
        getNavigationInteractions().changeFragment(getParentFragment(), next, true);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}

