package com.up959875.mobiletraveljournal.activities;

//Import statements
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

//MainActivity is called on startup to get the currently logged in user and sets up the navbar at the bottom of the screen
public class MainActivity extends AppCompatActivity implements NavigationListener {

    //private BubbleNavigationLinearView bottomNavigationBar;
    //private ViewPager fragmentsViewPager;

    //Declared variables
    private BubbleNavigationLinearView navigation;
    private ViewPager viewPager;
    private NavigationBarAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean backPressedOnce = false;
    private ActivityMainBinding binding;

    /**
     * This function is called when the app starts up, and sets the binding and layout of the starting page.
     * 
     * @param savedInstanceState A Bundle object containing the activity's previously saved state. If
     * the activity has never existed before, the value of the Bundle object is null.
     */
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


    /**
     * This function initializes the navigation bar adapter with the fragments that will be displayed
     * in the navigation bar
     * 
     * @param user The user object that is passed in from the login screen.
     */
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

    // This is setting the listeners for the viewpager and the bottom navigation bar.
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

   /**
    * If the current fragment is an instance of BackPress, then call whenBackPressed() on it. If it
    * returns true, then return. If the current fragment has a backstack, then pop it. If the user has
    * pressed the back button once, then call super.onBackPressed(). If the user has not pressed the
    * back button once, then set backPressedOnce to true, show a toast, and set backPressedOnce to
    * false after 2 seconds
    */
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


    /**
     * Replace the current fragment with the next fragment, and if addToBackStack is true, add the next
     * fragment to the back stack.
     * 
     * @param previous The fragment that is currently being displayed.
     * @param next The fragment you want to change to.
     * @param addToBackStack If you want to add the fragment to the backstack, set this to true.
     */
    @Override
    public void changeFragment(Fragment previous, Fragment next, Boolean addToBackStack) {
        if (previous.getView() != null) {
            FragmentTransaction fragmentTransaction = previous.getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(previous.getView().getId(), next);
            if (addToBackStack) fragmentTransaction.addToBackStack(next.getClass().getName());
            fragmentTransaction.commit();
        }
    }

    /**
     * This function changes the fragment in the navigation bar.
     * 
     * @param id The id of the item you want to change.
     * @param fragment The fragment you want to change to.
     */
    @Override
    public void changeNavigationBarItem(int id, Fragment fragment) {
        adapter.getItem(id).getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        adapter.changeItem(id, fragment);
        adapter.notifyDataSetChanged();
    }
}
