package com.up959875.mobiletraveljournal.fragments;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.location.Geocoder;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;

import com.up959875.mobiletraveljournal.databinding.FragmentProfileBinding;
import com.up959875.mobiletraveljournal.models.Address;
import com.up959875.mobiletraveljournal.viewmodel.AddressViewModel;
import android.text.style.ForegroundColorSpan;
import com.google.firebase.auth.FirebaseAuth;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.base.BaseFragment;

import java.util.Objects;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.content.Intent;
import android.view.Gravity;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.up959875.mobiletraveljournal.other.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AlertDialog;

//import com.up959875.mobiletraveljournal.other.Notification;
import com.up959875.mobiletraveljournal.viewmodel.NotificationViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.up959875.mobiletraveljournal.adapters.NotifAdapter;
import com.up959875.mobiletraveljournal.models.Notification;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.viewmodel.UserViewModel;

//ProfileFragment, related to displaying the user's profile when they are logged in. Will check authentication and location is valid, before loading other data such as friends.
public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private User user;
    private Dialog notificationsDialog;
    private User loggedInUser;
    private NotifAdapter notifAdapter;
    private RecyclerView notificationsRecyclerView;
    private NotificationViewModel notificationViewModel;
    private AddressViewModel addressViewModel;
    private Geocoder geocoder;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public static ProfileFragment newInstance(User user) {
        return new ProfileFragment(user);
    }


    public ProfileFragment() {
        super();
    }


    private ProfileFragment(User user) {
        super();
        this.user = user;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initUserViewModel();
        setListeners();
        initGeocoder();
        initUser();
        observeUserChange();
        getCurrentUser();
        return view;
    }

    private void observeUserChange() {
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null && user.isUserProfile(this.user)) {
                this.user = user;
                binding.setUser(user);
                initPrefs();
                initLocalArea();
            }
        });
        getCurrentUser();
    }


    private void getCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            startProgressBar();
            userViewModel.getUserData(firebaseAuth.getCurrentUser().getUid());
            userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {

                    binding.setLoggedInUser(user);
                    this.loggedInUser = user;
                    isNotUpdatedAccount();
                } else {
                    showSnackBar("ERROR: user doesn't exist in the database, try again", Snackbar.LENGTH_LONG);
                }
                stopProgressBar();
            });
        } else {
            showSnackBar("ERROR: Current user is not available, try again later", Snackbar.LENGTH_LONG);
        }
    }

    private void initGeocoder() {
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
    }


    private void initUserViewModel() {
       if (getActivity() != null) {
           userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
           addressViewModel = new ViewModelProvider(getActivity()).get(AddressViewModel.class);
           notificationViewModel = new ViewModelProvider(getActivity()).get(NotificationViewModel.class);
       }
    }

    private void initUser() {
        if (user != null) {
            binding.setUser(user);
            initPrefs();
            initLocalArea();
        } else if (getParentFragmentManager().getBackStackEntryCount() > 0) {
            getParentFragmentManager().popBackStack();
        }
    }

    private void initPrefs() {
        if (user != null && user.getPrefs() != null) {
            binding.profilePreferences.setData(user.getPrefs(), item -> {
                SpannableString spannableString = new SpannableString(item);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString;
            });
        }
    }

    private void initLocalArea() {
        if (user != null) {
            if (user.getLocation() != null && !user.getLocation().equals("")) {
                startProgressBar();
                addressViewModel.getAddress(user.getLocation());
                addressViewModel.getAddressData().observe(getViewLifecycleOwner(), address -> {
                    if (address != null) {
                        getLocationArea(address);
                    } else {
                        binding.setLocation(null);
                    }
                    stopProgressBar();
                });
            } else
                stopProgressBar();
        }
    }


    private void getLocationArea(Address address) {
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0)
                binding.setLocation(addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName());
            else
                binding.setLocation(null);

        } catch (IOException ignored) { binding.setLocation(address.getAddress().substring(address.getAddress().lastIndexOf(',') + 1));}
    }

    private void isNotUpdatedAccount() {
        if (user != null && user.getUid().equals(loggedInUser.getUid())) {
            accountNotUpdated();
        }
    }

    private void accountNotUpdated() {
        if (user.getLocation() == null && user.getPrefs() == null && user.getBio() == null
                && user.getPhoto() == null)
            updateAccountDialog();
    }

    private void updateAccountDialog() {
        if (getContext() != null) {
            TextView title = new TextView(getActivity());
            title.setText(getString(R.string.dialog_button_update_account_title));
            title.setPadding(0, 32, 0, 0);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(getResources().getColor(R.color.main_blue));
            title.setTextSize(18);

            final AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                    .setCustomTitle(title)
                    .setMessage(getString(R.string.dialog_button_update_account_desc))
                    .setPositiveButton(getString(R.string.dialog_button_now), (dialogInterface, i) -> {
                        dialogInterface.cancel();
                        openSettings();
                    })
                    .setNegativeButton(getString(R.string.dialog_button_later), null)
                    .show();

            TextView messageText = dialog.findViewById(android.R.id.message);
            Objects.requireNonNull(messageText).setGravity(Gravity.CENTER);
        }
    }


    private void setListeners() {
        binding.profileSignOutButton.setOnClickListener(this);
        binding.profileNotifications.setOnClickListener(this);
        binding.profileEdit.setOnClickListener(this);
        binding.profileContact.setOnClickListener(this);
        binding.profileRoute.setOnClickListener(this);
        binding.profileArrowButton.setOnClickListener(this);
        binding.profileFriends.setOnClickListener(this);
        binding.profileSettingsButton.setOnClickListener(this);
        binding.profileSeeAllPreferences.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_sign_out_button:
                signOut();
                return;
            case R.id.profile_notifications:
                getNotifs();
                return;
            case R.id.profile_edit:
                showSnackBar("clicked: edit", Snackbar.LENGTH_SHORT);
                return;
            case R.id.profile_see_all_preferences:
                seeAllPreferences();
            case R.id.profile_contact:
                getContactInfo();
                return;
            case R.id.profile_route:
                changeFragment(RouteListFragment.newInstance());
                return;
            case R.id.profile_friends:
                openFriendScreen();
                return;
            case R.id.profile_settings_button:
                openSettings();
                return;
            case R.id.profile_arrow_button:
                if (getParentFragmentManager().getBackStackEntryCount() > 0)
                    getParentFragmentManager().popBackStack();
                break;
        }
    }

    private void openSettings() {
        Fragment settingsFragment = ProfileSettingsFragment.newInstance();
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER, user);
        settingsFragment.setArguments(args);
        getNavigationInteractions().changeFragment(this, settingsFragment, true);

    }

    private void getNotifs() {

        if (getContext() != null) {
            if (user.getNotifications()!=null && !user.getNotifications().isEmpty()) {
                startProgressBar();
                notificationViewModel.getNotificationsListData(user.getNotifications());
                notificationViewModel.getNotificationsList().observe(getViewLifecycleOwner(), list -> {
                    if (list != null) {
                        getNotifUser(list);
                    }
                });
            } else {
                showNotifDialog(new ArrayList<>());
            }
        }
    }



    private void seeAllPreferences() {
        ConstraintLayout.LayoutParams constraintLayout = (ConstraintLayout.LayoutParams) binding.profilePreferences.getLayoutParams();
        String seePreferences;
        if (binding.profilePreferences.getLayoutParams().height == ConstraintLayout.LayoutParams.WRAP_CONTENT) {
            constraintLayout.height = Constants.HASHTAG_HEIGHT;
            seePreferences = getResources().getString(R.string.profile_see_all_prefs);
        } else {
            constraintLayout.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            seePreferences = getResources().getString(R.string.profile_see_less_prefs);
        }
        binding.profileSeeAllPreferences.setPaintFlags(binding.profileSeeAllPreferences.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.profileSeeAllPreferences.setText(seePreferences);
        binding.profilePreferences.setLayoutParams(constraintLayout);
    }

    private void signOut() {
        startProgressBar();
        FirebaseAuth.getInstance().signOut();
        showSnackBar("You have been signed out successfully", Snackbar.LENGTH_SHORT);
        stopProgressBar();
        getNavigationInteractions().changeNavigationBarItem(2, ProfileLoginFragment.newInstance());
    }

    private void getNotifUser(List<Notification> notifications) {
        List<String> usersIds = new ArrayList<>();
        for (Notification notification : notifications) {
            usersIds.add(notification.getIdFrom());
        }
        userViewModel.getUsersListData(usersIds);
        userViewModel.getUsersList().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                for (int i=0; i<notifications.size(); i++) {
                    notifications.get(i).setUserFrom(users.get(i));
                }
                showNotifDialog(notifications);
                stopProgressBar();
            }
        });
    }

    private void updateUser(Map<String, Object> changes, String messageSuccess, String messageError) {
        userViewModel.updateUser(user, changes);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                binding.setUser(user);
                if (user.isUserProfile(loggedInUser)) {
                    this.loggedInUser = user;
                    binding.setLoggedInUser(user);
                }
                if (messageSuccess != null)
                    showSnackBar(messageSuccess, Snackbar.LENGTH_SHORT);
            } else if (messageError != null)
                showSnackBar(messageError, Snackbar.LENGTH_LONG);
            stopProgressBar();
        });
    }

    private void startProgressBar() {
        binding.profileProgressbarLayout.setVisibility(View.VISIBLE);
        binding.profileProgressbar.start();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), false);
    }


    private void stopProgressBar() {
        binding.profileProgressbarLayout.setVisibility(View.INVISIBLE);
        binding.profileProgressbar.stop();
        enableDisableViewGroup((ViewGroup) binding.getRoot(), true);
    }

    private void showNotifDialog(List<Notification> notifications) {
        if (getContext() != null) {
            notificationsDialog = new Dialog(getContext());
            notificationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            notificationsDialog.setCancelable(true);
            notificationsDialog.setContentView(R.layout.dialog_notifs);
            notificationsDialog.findViewById(R.id.dialog_notifications_ok_button).setOnClickListener(v -> notificationsDialog.dismiss());

            if (!notifications.isEmpty()) {
                notificationsDialog.findViewById(R.id.dialog_notifications_no_results).setVisibility(View.INVISIBLE);
                setNotificationsRecyclerView(notifications);
            } else {
                notificationsDialog.findViewById(R.id.dialog_notifications_recycler_view).setVisibility(View.INVISIBLE);
            }

            notificationsDialog.show();
        }
    }

    private void sendFriendRequest() {
        notificationViewModel.sendNotification(loggedInUser, user, com.up959875.mobiletraveljournal.other.Notification.FRIEND.ordinal());
        notificationViewModel.getNotificationResponse().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                if (!status.contains(Constants.ERROR)) {
                    HashMap<String, Object> changes = new HashMap<>();
                    List<String> notifications = user.getNotifications() == null ? new ArrayList<>() : user.getNotifications();
                    notifications.add(status);
                    changes.put(Constants.NOTIFICATIONS.toLowerCase(), notifications);
                    disableFriendButton();
                    updateUser(changes, getResources().getString(R.string.message_notif_sent), getResources().getString(R.string.message_error_failed_add_notification));
                } else {
                    showSnackBar(status, Snackbar.LENGTH_LONG);
                    stopProgressBar();
                }
            }
        });
    }

    private void changeFragment(BaseFragment next) {
        if (user.isUserProfile(loggedInUser))
            getNavigationInteractions().changeFragment(this, next, true);
        else
            getNavigationInteractions().changeFragment(getParentFragment(), next, true);
    }


    private void getContactInfo() {
        if (user != null) {
            if (loggedInUser != null && user.isUserProfile(loggedInUser)) {
                if (getContext() != null) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle(getResources().getString(R.string.dialog_button_myemail))
                            .setMessage(user.getEmail())
                            .setPositiveButton(getString(R.string.dialog_button_ok), null)
                            .show();
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
                startActivity(Intent.createChooser(intent, ""));
            }
        }
    }



    private void checkRequestExists() {
        if (user.getNotifications() != null && !user.getNotifications().isEmpty()) {
            AtomicBoolean exists = new AtomicBoolean(false);
            AtomicInteger counter = new AtomicInteger(0);
            for (String id : user.getNotifications()) {
                notificationViewModel.getNotificationData(id);
                notificationViewModel.getNotification().observe(getViewLifecycleOwner(), notification -> {
                    if (notification != null && notification.getIdFrom().equals(loggedInUser.getUid())) {
                        exists.set(true);
                    }
                    counter.getAndIncrement();
                    if (exists.get()) {

                        disableFriendButton();
                    } else if (counter.get() == user.getNotifications().size()) {
                        sendFriendRequest();
                    }
                });
            }
        } else {
            sendFriendRequest();
        }
    }

    private void setNotificationsAdapter(List<Notification> notifications) {
        notifAdapter = new NotifAdapter(getContext(), notifications);
        notificationsRecyclerView.setAdapter(notifAdapter);
        notifAdapter.setOnItemClickListener((object, position, view) -> {
            Notification notification = (Notification) object;
            if (notification != null) {
                switch (view.getId()) {
                    case R.id.notif_item:
                        notificationsDialog.dismiss();
                        changeFragment(ProfileFragment.newInstance(notification.getUserFrom()));
                        break;
                    case R.id.notif_item_accept_button:
                        removeNotification(notification, position);
                        addToFriends(notification);
                        break;
                    case R.id.notif_item_discard_button:
                        removeNotification(notification, position);
                        break;
                }
            }
        });
    }

    private List<String> getFilteredList(List<String> list, String statement) {
        List<String> filtered = new ArrayList<>();
        for (String obj : list)
            if (!obj.equals(statement))
                filtered.add(obj);
        return filtered;
    }

    private void setNotificationsRecyclerView(List<Notification> notifications) {
        notificationsRecyclerView = notificationsDialog.findViewById(R.id.dialog_notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setNotificationsAdapter(notifications);
    }



    private void openRouteScreen() {
        getNavigationInteractions().changeFragment(getParentFragment(), RouteListFragment.newInstance(), true);
    }

    private void removeNotification(Notification notification, int position) {
        Map<String, Object> changes = new HashMap<>();
        changes.put(Constants.NOTIFICATIONS.toLowerCase(), getFilteredList(user.getNotifications(), notification.getId()));
        updateUser(changes, null, getResources().getString(R.string.message_error_failed_remove_notification));
        notifAdapter.remove(position);
        notificationViewModel.removeNotification(notification.getId());
        if (notifAdapter.getItemCount() == 0)
            notificationsDialog.findViewById(R.id.dialog_notifications_no_results).setVisibility(View.VISIBLE);
    }

    private void disableFriendButton() {
        binding.profileFriends.setEnabled(false);
        binding.profileFriends.setAlpha(0.4f);
    }

    private void addToFriends(Notification notification) {
        Map<String, Object> changesLoggedUser = new HashMap<>();
        List<String> loggedUserFriends = user.getFriend() != null
                ? new ArrayList<>(user.getFriend()) : new ArrayList<>();
        loggedUserFriends.add(notification.getIdFrom());
        changesLoggedUser.put(Constants.DB_FRIENDS, loggedUserFriends);
        String successMessage = getResources().getString(R.string.message_friends_success) + " " + notification.getUserFrom().getUsername() + "!";
        updateUser(changesLoggedUser, successMessage, getResources().getString(R.string.message_error_failed_add_friends));

        Map<String, Object> changesFriendUser = new HashMap<>();
        List<String> friendUserFriends = notification.getUserFrom().getFriend() != null
                ? new ArrayList<>(notification.getUserFrom().getFriend()) : new ArrayList<>();
        friendUserFriends.add(notification.getIdTo());
        changesFriendUser.put(Constants.DB_FRIENDS, friendUserFriends);
        userViewModel.updateUser(notification.getUserFrom(), changesFriendUser);
    }



    private void openFriendScreen() {
        if (loggedInUser != null && user != null) {
            if (user.isUserProfile(loggedInUser)) {
                Log.d("user", String.valueOf(user.getFriend()));
                getNavigationInteractions().changeFragment(this, FriendListFragment.newInstance(user), true);
            } else if (user.hasFriend(loggedInUser)) {
                getNavigationInteractions().changeFragment(getParentFragment(), FriendListFragment.newInstance(user), true);
            } else {
                checkRequestExists();
            }
        }
    }




    private void showSnackBar(String message, int duration) {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), message, duration);
        snackbar.setAnchorView(Objects.requireNonNull(getActivity()).findViewById(R.id.bottom_navigation_view));
        TextView textView = snackbar.getView().findViewById(R.id.snackbar_text);
        textView.setMaxLines(3);
        snackbar.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
