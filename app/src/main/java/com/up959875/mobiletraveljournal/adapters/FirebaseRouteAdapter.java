package com.up959875.mobiletraveljournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.UserDisplayBinding;
import com.up959875.mobiletraveljournal.interfaces.OnItemClick;
import com.up959875.mobiletraveljournal.models.Route;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Privacy;

import java.util.List;
import java.util.Objects;

//Function to help display the route data from the Firebase Database.
public class FirebaseRouteAdapter extends FirestorePagingAdapter<Route, FirebaseRouteAdapter.UserViewHolder>{

    private OnItemClick onItemClick;
    private Context context;
    private List<Route> routes;


    //Constructor for the class
    public FirebaseRouteAdapter(FirestorePagingOptions<Route> options, Context context) {

        super(options);
        this.context = context;
    }


    @NonNull
    //Called to create a view for the firebase route.
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserDisplayBinding binding = UserDisplayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    //Activates a listener for when an item is clicked.
    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }


    /**
     * Load in the image and replace the placeholder image.
     * 
     * @param holder The ViewHolder for the view being bound.
     * @param position The position of the item within the adapter's data set.
     * @param model The model object containing the data that should be used to populate the view.
     */
    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Route model) {
        holder.binding.userDisplayUsername.setText(model.getTitle());

        Glide.with(context)
                .load(model.getImage())
                .placeholder(R.drawable.ic_avatar)
                .into(holder.binding.userDisplayImage);
        holder.binding.userDisplay.setOnClickListener(view -> onItemClick.onItemClick(
                Objects.requireNonNull(getItem(position)).toObject(Route.class),
                position,
                holder.binding.userDisplay));
        holder.binding.userItemDeleteButton.setVisibility(View.GONE);
    }


    //OnClickListener for each item
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }


    static class UserViewHolder extends RecyclerView.ViewHolder {

        private UserDisplayBinding binding;
        UserViewHolder(UserDisplayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
        }




