package com.up959875.mobiletraveljournal.fragments;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import androidx.lifecycle.ViewModelProvider;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.adapters.UserAdapter;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;
import android.view.Window;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import com.up959875.mobiletraveljournal.R;
import android.annotation.SuppressLint;
import android.app.Dialog;
import com.google.android.material.button.MaterialButton;
import com.up959875.mobiletraveljournal.other.Constants;
import java.util.Map;
import java.util.HashMap;
import com.up959875.mobiletraveljournal.databinding.FragmentFriendListBinding;
import com.up959875.mobiletraveljournal.base.BaseFragment;

//Fragment to handle the friend list, which is accessed from the users profile. They will get a list of their friends, which is retrieved from the database.
public class FriendListFragment extends BaseFragment{

    private FragmentFriendListBinding binding;
    private User user;
    private User loggedInUser;
    private UserAdapter adapter;
    private UserViewModel userViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initViewModels();
        setListeners();
        initUser();
        initLoggedInUser();
        Log.d("l45", String.valueOf(user));
//        Log.d("l46", String.valueOf((user.getFriend().isEmpty())));
//        Log.d("l47", String.valueOf(user.getFriend().isEmpty()));
        initFriend();
        return view;
    }

    static FriendListFragment newInstance(User user) {
        return new FriendListFragment(user);
    }


    private void setListeners() {
        binding.friendListArrowButton.setOnClickListener(view -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0)
                getParentFragmentManager().popBackStack();
        });
    }

    public FriendListFragment() {
        super();
    }

    private void changeFragment(BaseFragment next) {
        if (user.isUserProfile(loggedInUser))
            getNavigationInteractions().changeFragment(this, next, true);
        else
            getNavigationInteractions().changeFragment(getParentFragment(), next, true);
    }


    private FriendListFragment(User user) {
        super();
        this.user = user;
    }

    private void initViewModels() {
        if (getActivity() != null) {
            userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        }
    }

    private void initUser() {
        if (user == null) {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void initTitle() {
        if (user != null && loggedInUser != null) {
            if (user.isUserProfile(loggedInUser)) {
                binding.friendListTitle.setText("My " + getResources().getString(R.string.friend_list_friends));
            } else {
                binding.friendListTitle.setText(user.getUsername() + "\'s\n" +
                        getResources().getString(R.string.friend_list_friends));
            }
        }
    }

    private void initFriendList(List<User> friends) {
        adapter = new UserAdapter(getContext(), friends, user.isUserProfile(loggedInUser));
        binding.friendListRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((object, position, view) -> {
            User userItem = (User) object;
            if (userItem != null) {
                switch (view.getId()) {
                    case R.id.user_display:
                        if (loggedInUser != null && loggedInUser.isUserProfile(userItem))
                            //showSnackBar(getResources().getString(R.string.message_you, Snackbar.LENGTH_SHORT);
                            Log.d("initFriendList", "ok");
                        else
                            changeFragment(ProfileFragment.newInstance(userItem));
                        break;
                    case R.id.user_item_delete_button:
                        showDeleteDialog(userItem, position);
                        break;
                }
            }
        });
    }

    private void initLoggedInUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    this.loggedInUser = user;
                    initTitle();
                } else {
                    //showSnackBar(getResources().getString(R.string.messages_error_current_user_not_available), Snackbar.LENGTH_LONG);
                    Log.d("initLoggedInUser", getResources().getString(R.string.message_error_user_identity));
                }

            });
        }
    }




    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friend_list_arrow_button:
                if (getParentFragmentManager().getBackStackEntryCount() > 0)
                    getParentFragmentManager().popBackStack();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showDeleteDialog(User user, int position) {
        if (getContext() != null && getActivity() != null) {
            Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_contact);

            TextView title = dialog.findViewById(R.id.dialog_contact_title);
            TextView message = dialog.findViewById(R.id.dialog_contact_desc);
            MaterialButton buttonPositive = dialog.findViewById(R.id.dialog_contact_button_positive);
            MaterialButton buttonNegative = dialog.findViewById(R.id.dialog_contact_button_negative);

            title.setText(getResources().getString(R.string.dialog_remove_friend_title));
            message.setText(getResources().getString(R.string.dialog_remove_friend_desc) + " " + user.getUsername() + "?");
            buttonPositive.setText(getResources().getString(R.string.notif_remove));
            buttonPositive.setOnClickListener(v -> {
                dialog.dismiss();
                removeFriend(user, position);
            });
            buttonNegative.setText(getResources().getString(R.string.dialog_button_cancel));
            buttonNegative.setOnClickListener(v -> dialog.dismiss());

            dialog.show();
        }
    }

    private void initFriend() {
        Log.d("l185", String.valueOf(user.getUid()));
        Log.d("l186", String.valueOf(user.getFriend()));
        if (user != null && user.getFriend() != null && !user.getFriend().isEmpty()) {
            Log.d("l186", "beginning of initFriend");
            binding.friendListMessage.setVisibility(View.INVISIBLE);
            userViewModel.getUsersListData(user.getFriend());

            userViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
                Log.d("l188,", String.valueOf(user));
                if (users != null) {
                    Log.d("l190", "initFriendList");
                    initFriendList(users);
                }
            });
        } else {
            Log.d("l194", "not initFriendList");
            binding.friendListRecyclerView.setVisibility(View.INVISIBLE);
            binding.friendListMessage.setVisibility(View.VISIBLE);
        }
    }

    private void removeFriend(User friend, int position) {


        Map<String, Object> changesFriendUser = new HashMap<>();
        friend.getFriend().remove(loggedInUser.getUid());
        changesFriendUser.put(Constants.DB_FRIENDS, friend.getFriend());
        userViewModel.updateUser(friend, changesFriendUser);

        Map<String, Object> changesLoggedUser = new HashMap<>();
        loggedInUser.getFriend().remove(friend.getUid());
        changesLoggedUser.put(Constants.DB_FRIENDS, loggedInUser.getFriend());

        userViewModel.updateUser(loggedInUser, changesLoggedUser);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                this.loggedInUser = user;
                userViewModel.setUser(user);
                adapter.remove(position);
                if (adapter.getItemCount() == 0) {
                    binding.friendListRecyclerView.setVisibility(View.INVISIBLE);
                    binding.friendListMessage.setVisibility(View.VISIBLE);
                }
                //showSnackBar(getResources().getString(R.string.messages_remove_friend_success), Snackbar.LENGTH_SHORT);
                Log.d("l223", String.valueOf(R.string.message_remove_friend_success));
            } else {
                //showSnackBar(getResources().getString(R.string.messages_error_failed_remove_friend), Snackbar.LENGTH_LONG);
                Log.d("l227", String.valueOf(R.string.message_failed_remove_friend));
            }

        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
