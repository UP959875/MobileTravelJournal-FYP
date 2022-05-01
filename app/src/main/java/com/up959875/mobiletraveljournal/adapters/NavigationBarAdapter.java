package com.up959875.mobiletraveljournal.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class NavigationBarAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    public NavigationBarAdapter(List<Fragment> list, FragmentManager manager) {
        super(manager);
        fragmentList = list;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position <fragmentList.size())
            return fragmentList.get(position);
        return new Fragment();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        Fragment fragment = (Fragment) object;
        int position = fragmentList.indexOf(fragment);
        if (position >= 0) {
            return super.getItemPosition(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }


    public void changeItem(int position, Fragment fragment) {
        fragmentList.set(position, fragment);
    }

}
