package com.up959875.mobiletraveljournal.adapters;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import com.asksira.loopingviewpager.LoopingPagerAdapter;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;
import android.widget.Toast;
import com.up959875.mobiletraveljournal.databinding.RouteExploreBinding;
import com.up959875.mobiletraveljournal.models.Route;


//Class for loading routes into the app
public class RouteExploreAdapter extends LoopingPagerAdapter<Route> {

    private RouteExploreBinding binding;
    private List<Route> route;

    //Constructor for the class.
    public RouteExploreAdapter(boolean isInfinite, List<Route> listRoute, Context context) {
        super(context, listRoute, isInfinite);
        route = listRoute;
    }

    //Sets up the binding for the view.
    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        binding = RouteExploreBinding.inflate(LayoutInflater.from(context), container, false);
        return binding.getRoot();
    }

    //Get the amount of routes
    @Override
    public int getCount() {
        return route.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }



    @Override
    protected void bindView(View convertView, int position, int viewType) {

        binding.exploreRouteImage.setImageResource(route.get(position).getImageInt());
        binding.exploreRouteTitle.setText(route.get(position).getTitle());
        binding.exploreRouteDesc.setText(route.get(position).getDesc());


        convertView.setOnClickListener(v -> Toast.makeText(context, route.get(position).getTitle(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
