package com.up959875.mobiletraveljournal.base;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.up959875.mobiletraveljournal.interfaces.NavigationListener;

//BaseFragment Class, which other fragments extend from. Used to add extra functionality to each fragment, and extends of the default fragment.
public class BaseFragment extends Fragment{

    private NavigationListener navigationListener;

    protected NavigationListener getNavigationInteractions() {
        return navigationListener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationListener)
            navigationListener = (NavigationListener) context;
    }

    protected void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}
