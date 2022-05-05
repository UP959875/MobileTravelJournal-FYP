package com.up959875.mobiletraveljournal.adapters;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.UserDisplayBinding;
import com.up959875.mobiletraveljournal.interfaces.OnItemClick;
import com.up959875.mobiletraveljournal.other.Privacy;
import com.up959875.mobiletraveljournal.models.User;
//User adapter to load local data into view
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {


    private List<User> users;
    private OnItemClick onItemClick;
    private boolean isUserProfile;
    private Context context;

    //Constructor for the class
    public UserAdapter(Context context, List<User> users, boolean isUserProfile) {
        this.context = context;
        this.users = users;
        this.isUserProfile = isUserProfile;
    }

    private User getItem(int position) {
        return this.users.get(position);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserDisplayBinding binding = UserDisplayBinding.inflate(LayoutInflater.from(context), parent, false);
        return new UserHolder(binding);
    }

    //Replace placeholder info with the user details.
    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        final User user = users.get(position);
        holder.binding.userDisplayUsername.setText(user.getUsername());
        if (user.getPrivacyEmail() == Privacy.PUBLIC.ordinal()) {
            holder.binding.userDisplayEmail.setText(user.getEmail());
        } else {
            holder.binding.userDisplayEmail.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(user.getPhoto())
                .placeholder(R.drawable.default_icon)
                .into(holder.binding.userDisplayImage);
        holder.binding.userDisplay.setOnClickListener(view -> onItemClick.onItemClick(
                Objects.requireNonNull(getItem(position)),
                position,
                holder.binding.userDisplay));
        holder.binding.userItemDeleteButton.setOnClickListener(view -> onItemClick.onItemClick(
                Objects.requireNonNull(getItem(position)),
                position,
                holder.binding.userItemDeleteButton));
        if (!isUserProfile)
            holder.binding.userItemDeleteButton.setVisibility(View.GONE);
    }

    public void remove (int position) {
        users.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    //OnClickListener for items
    public void setOnItemClickListener(OnItemClick onItemClick2) {
        this.onItemClick = onItemClick2;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    static class UserHolder extends RecyclerView.ViewHolder {
        private UserDisplayBinding binding;
        UserHolder(UserDisplayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
