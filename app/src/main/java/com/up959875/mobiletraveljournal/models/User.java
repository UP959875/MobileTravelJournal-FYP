package com.up959875.mobiletraveljournal.models;
import androidx.annotation.Nullable;

import com.up959875.mobiletraveljournal.other.Privacy;
import com.up959875.mobiletraveljournal.other.Constants;
import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
//import com.up959875.mobiletraveljournal.BR;
//import androidx.databinding.library.baseAdapters.BR;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import com.bumptech.glide.Glide;
import com.up959875.mobiletraveljournal.R;
import android.widget.ImageView;

//User model class, contains all constructors and initialises variables for all the data.
//Contains different constructors for different uses, and getters and setters for all variables.
public class User extends BaseObservable implements Serializable {
    private String uid;
    private String username;
    private String email;
    private String photo;
    private String bio;
    private String location;
    private List<String> notifications;
    private List<String> prefs;
    private List<String> friends;
    private Map<String, Integer> privacy;
    private List<Markers> markers;

    public User() {
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;

        this.privacy = new HashMap<>();
        defineDefaultPrivacy();
    }

    private void defineDefaultPrivacy() {
        privacy.put(Constants.EMAIL, Privacy.PUBLIC.ordinal());
        privacy.put(Constants.LOCATION, Privacy.PUBLIC.ordinal());
        privacy.put(Constants.PREFERENCES, Privacy.PUBLIC.ordinal());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;

    }

    public List<String> getPrefs() {
        return prefs;
    }

    public void setPrefs(List<String> prefs) {
        this.prefs = prefs;
    }

    @Bindable
    public List<String> getFriend() {
        return friends;
    }

    public void setFriend(List<String> friends) {
        this.friends = friends;
    }

    public boolean hasFriend(User loggedUser) {
        return loggedUser != null && this.friends != null && this.friends.contains(loggedUser.uid);
    }

    public Map<String, Integer> getPrivacy() {
        return privacy;
    }


    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;

    }

    @Bindable
    public List<Markers> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Markers> markers) {
        this.markers = markers;

    }

    @SuppressWarnings("ConstantConditions")

    public int getPrivacyEmail() {
        if (this.privacy != null && this.privacy.get(Constants.EMAIL) != null)
            return this.privacy.get(Constants.EMAIL);
        else return 2;
    }

    @SuppressWarnings("ConstantConditions")

    private int getPrivacyLocation() {
        if (this.privacy != null && this.privacy.get(Constants.LOCATION) != null)
            return this.privacy.get(Constants.LOCATION);
        else return 2;
    }

    @SuppressWarnings("ConstantConditions")

    private int getPrivacyPreferences() {
        if (this.privacy != null && this.privacy.get(Constants.PREFERENCES) != null)
            return this.privacy.get(Constants.PREFERENCES);
        else return 2;
    }

    public boolean isEmailAvailableForUser (User loggedInUser) {
        return (!isUserProfile(loggedInUser) && this.getPrivacyEmail() == 0) || isUserProfile(loggedInUser);
    }


    public boolean isPreferencesAvailableForUser (User loggedInUser) {
        return ((!isUserProfile(loggedInUser) && this.getPrivacyPreferences() == 0) || isUserProfile(loggedInUser)) && this.prefs!= null;
    }


    public boolean isLocationAvailableForUser (User loggedInUser) {
        return ((!isUserProfile(loggedInUser) && this.getPrivacyLocation() == 0) || isUserProfile(loggedInUser)) && this.location != null;
    }



    public boolean isUserProfile (User loggedInUser) {
        return loggedInUser != null && this.uid.equals(loggedInUser.getUid());
    }

    public boolean hasNotifications() {
        return notifications != null && !notifications.isEmpty();
    }


    public void setPrivacy(Map<String, Integer> privacy) {
        this.privacy = privacy;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj == null || getClass() != obj.getClass())
                return false;

            User u = (User) obj;
            return ((uid.equals(u.getUid()))
                    && (username.equals(u.getUsername()))
                    && (email.equals(u.getEmail()))
                    && (photo.equals(u.getPhoto()) || (photo == null && u.getPhoto() == null))
                    && (bio.equals(u.getBio()) || (bio == null && u.getBio() == null))
                    && (location.equals(u.getLocation()) || (location == null && u.getLocation() == null))
                    && ((prefs != null && prefs.equals(u.getPrefs())) || (prefs == null && u.getPrefs() == null))
                    && (friends != null && (friends.equals(u.getFriend())) || (friends == null && u.getFriend() == null))
                    && (privacy.equals(u.getPrivacy())));
        } catch (Exception ex) {
            return false;
        }
    }

    @BindingAdapter("imageUrl")
    // This is a method that is used to load an image into an image view.
    public static void loadImage(ImageView iv, String imgUrl){
        Glide.with(iv.getContext())
                .load(imgUrl)
                .placeholder(R.drawable.ic_avatar)
                .into(iv);
    }

}
