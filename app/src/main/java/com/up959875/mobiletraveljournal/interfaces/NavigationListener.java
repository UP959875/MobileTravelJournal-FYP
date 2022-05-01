package com.up959875.mobiletraveljournal.interfaces;
import androidx.fragment.app.Fragment;

public interface NavigationListener {
    void changeFragment (Fragment previous, Fragment next, Boolean addToBackStack);

    void changeNavigationBarItem(int id, Fragment fragment);
}
