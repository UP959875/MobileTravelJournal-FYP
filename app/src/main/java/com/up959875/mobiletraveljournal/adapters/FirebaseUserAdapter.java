package com.up959875.mobiletraveljournal.adapters;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.up959875.mobiletraveljournal.interfaces.OnItemClick;
import com.google.firebase.firestore.DocumentSnapshot;
import com.up959875.mobiletraveljournal.models.User;
import java.util.List;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.UserDisplayBinding;
import com.up959875.mobiletraveljournal.other.Privacy;

import java.util.Objects;


public class FirebaseUserAdapter extends FirestorePagingAdapter<User, FirebaseUserAdapter.UserViewHolder>{

    private OnItemClick onItemClick;
    private Context context;
    private List<User> users;

    public FirebaseUserAdapter(FirestorePagingOptions<User> options, Context context) {

        super(options);
        this.context = context;
    }


    @NonNull

    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserDisplayBinding binding = UserDisplayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
        holder.binding.userDisplayUsername.setText(model.getUsername());
        if (model.getPrivacyEmail() == Privacy.PUBLIC.ordinal()) {
            holder.binding.userDisplayEmail.setText(model.getEmail());
        } else {
            holder.binding.userDisplayEmail.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(model.getPhoto())
                .placeholder(R.drawable.ic_avatar)
                .into(holder.binding.userDisplayImage);
        holder.binding.userDisplay.setOnClickListener(view -> onItemClick.onItemClick(
                Objects.requireNonNull(getItem(position)).toObject(User.class),
                position,
                holder.binding.userDisplay));
        holder.binding.userItemDeleteButton.setVisibility(View.GONE);
    }

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




