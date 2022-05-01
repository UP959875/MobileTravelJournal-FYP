package com.up959875.mobiletraveljournal.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;
import android.view.View;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.up959875.mobiletraveljournal.adapters.NavigationBarAdapter;
import com.up959875.mobiletraveljournal.fragments.HomeFragment;
import com.up959875.mobiletraveljournal.fragments.ProfileLoginFragment;
import com.up959875.mobiletraveljournal.fragments.RouteFragment;
import android.os.Handler;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.fragments.RouteStartFragment;
import com.up959875.mobiletraveljournal.other.ViewPagerListener;
import com.up959875.mobiletraveljournal.interfaces.NavigationListener;
import com.up959875.mobiletraveljournal.databinding.ActivityMainBinding;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.fragments.ProfileFragment;
import com.up959875.mobiletraveljournal.other.Constants;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.FragmentManager;
import com.up959875.mobiletraveljournal.interfaces.BackPress;


public class MainActivity extends AppCompatActivity implements NavigationListener {

    //private BubbleNavigationLinearView bottomNavigationBar;
    //private ViewPager fragmentsViewPager;
    private BubbleNavigationLinearView navigation;
    private ViewPager viewPager;
    private NavigationBarAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean backPressedOnce = false;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        User user = getUserFromIntent();
        initNavAdapter(user);
        setListeners();


    }

    private User getUserFromIntent() {
        return (User) getIntent().getSerializableExtra(Constants.USER);
    }


    private void initNavAdapter(User user) {
        fragmentList.add(0, HomeFragment.newInstance());
        fragmentList.add(1, RouteStartFragment.newInstance());
        if (user != null)
            fragmentList.add(2, ProfileFragment.newInstance(user));
        else
            fragmentList.add(2, ProfileLoginFragment.newInstance());

        adapter = new NavigationBarAdapter(fragmentList, getSupportFragmentManager());
        binding.viewPager.setAdapter(adapter);
    }

    private void setListeners() {
        binding.viewPager.addOnPageChangeListener(new ViewPagerListener() {

            @Override
            public void onPageSelected(int i) {
                binding.bottomNavigationView.setCurrentActiveItem(i);
            }

        });

        binding.bottomNavigationView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                binding.viewPager.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = adapter.getItem(binding.bottomNavigationView.getCurrentActiveItemPosition())
                .getChildFragmentManager().findFragmentById(R.id.fragment_profile);
        if (fragment instanceof BackPress && ((BackPress) fragment).whenBackPressed()) {
            return;
        }

        if (adapter.getItem(binding.bottomNavigationView.getCurrentActiveItemPosition())
                .getChildFragmentManager().getBackStackEntryCount() >= 1) {
            adapter.getItem(binding.bottomNavigationView.getCurrentActiveItemPosition())
                    .getChildFragmentManager().popBackStack();
            return;
        }
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.backPressedOnce = true;
        Toast.makeText(this, "Please go back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public void changeFragment(Fragment previous, Fragment next, Boolean addToBackStack) {
        if (previous.getView() != null) {
            FragmentTransaction fragmentTransaction = previous.getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(previous.getView().getId(), next);
            if (addToBackStack) fragmentTransaction.addToBackStack(next.getClass().getName());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void changeNavigationBarItem(int id, Fragment fragment) {
        adapter.getItem(id).getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        adapter.changeItem(id, fragment);
        adapter.notifyDataSetChanged();
    }
}
