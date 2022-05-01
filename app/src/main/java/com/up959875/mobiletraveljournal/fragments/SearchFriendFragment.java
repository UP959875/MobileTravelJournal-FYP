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
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.adapters.FirebaseUserAdapter;
import com.up959875.mobiletraveljournal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.up959875.mobiletraveljournal.databinding.FragmentSearchFriendBinding;
import com.up959875.mobiletraveljournal.base.BaseFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.CollectionReference;
import com.up959875.mobiletraveljournal.other.SearchViewListener;
import com.up959875.mobiletraveljournal.other.Constants;

public class SearchFriendFragment extends BaseFragment {

    private FragmentSearchFriendBinding binding;
    private FirebaseUserAdapter adapter;
    private CollectionReference usersRef;
    private FirebaseUser loggedInUser;
    private PagedList.Config usersPagingConfig;


    public static SearchFriendFragment newInstance() {
        return new SearchFriendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchFriendBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        initUserReference();
        setListener();
        focusOnSearchView();

        return view;
    }

    private void setListener() {
        binding.searchFriendArrow.setOnClickListener(view -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0)
                getParentFragmentManager().popBackStack();
        });
        binding.searchFriendSearchView.setOnQueryTextListener(new SearchViewListener() {
            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.equals("")) {
                    searchUser(s);
                } else {
                    binding.searchFriendRecyclerView.setAdapter(null);
                    binding.searchFriendMessage.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void focusOnSearchView() {
        if (getContext() != null) {
            binding.searchFriendSearchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    private void searchUser(String username) {
        Query query = usersRef.orderBy(Constants.USERNAME).startAt(username).endAt(username + "\uf8ff");
        Log.d("l87", (usersRef.getPath()));
        FirestorePagingOptions<User> options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(getViewLifecycleOwner())
                .setQuery(query, usersPagingConfig, User.class)
                .build();

        adapter = new FirebaseUserAdapter(options, getContext());
        binding.searchFriendRecyclerView.swapAdapter(adapter, true);
        setAdapterOnItemClickListener();
        setAdapterObserver();
    }

    private void initUserReference() {
        loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseFirestore.getInstance().collection(Constants.USERS);
        usersPagingConfig = new PagedList.Config.Builder().setInitialLoadSizeHint(10).setPageSize(3).build();
    }

    private void setAdapterOnItemClickListener() {
        adapter.setOnItemClickListener((snapshot, position, view) -> {
            User user = (User) snapshot;
            if (user != null) {
                if (loggedInUser != null && user.getUid().equals(loggedInUser.getUid())) {
                    //showSnackBar(getResources().getString(R.string.friend_add_yourself), Snackbar.LENGTH_SHORT);
                } else {
                    hideKeyboard();
                    changeFragment(ProfileFragment.newInstance(user));
                }
            }
        });
    }


    private void setAdapterObserver() {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int totalNumberOfItems = adapter.getItemCount();
                if(totalNumberOfItems == 0) {
                    binding.searchFriendMessage.setVisibility(View.VISIBLE);
                    binding.searchFriendMessage.setText(getResources().getString(R.string.search_friend_no_friends));
                } else {
                    binding.searchFriendMessage.setVisibility(View.GONE);
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
