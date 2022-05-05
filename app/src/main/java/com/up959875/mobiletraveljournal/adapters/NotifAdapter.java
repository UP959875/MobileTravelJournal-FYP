package com.up959875.mobiletraveljournal.adapters;

import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import android.content.Context;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.up959875.mobiletraveljournal.R;
import com.up959875.mobiletraveljournal.databinding.ListNotificationsBinding;
import com.up959875.mobiletraveljournal.interfaces.OnItemClick;
import com.up959875.mobiletraveljournal.models.Notification;

//Notification adapter to display notifications
public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifHolder> {

    private List<Notification> notifs;
    private OnItemClick onItemClick;
    private Context context;

    //Constructor for the class
    public NotifAdapter(Context context, List<Notification> notifs) {
        this.context = context;
        this.notifs = notifs;
    }

    //View created when class is called.
    @NonNull
    @Override
    public NotifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListNotificationsBinding binding = ListNotificationsBinding.inflate(LayoutInflater.from(context), parent, false);
        return new NotifHolder(binding);
    }


    //Get a specific item from the notifs
    private Notification getItem(int position) {
        return this.notifs.get(position);
    }

    //OnClickListener for the items
    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    //Binds the information to the layout file and replaces the placeholders.
    @Override
    public void onBindViewHolder(@NonNull NotifHolder holder, int position) {
        final Notification notification = notifs.get(position);
        Glide.with(context)
                .load(notification.getUserFrom().getPhoto())
                .placeholder(R.drawable.default_icon)
                .into(holder.binding.notifItemUserImage);
        holder.binding.notifItemUserUsername.setText(notification.getUserFrom().getUsername());
        holder.binding.notifItemTimestamp.setText(getTime(notification.getTimestamp().toDate()));
        holder.binding.notifItem.setOnClickListener(view -> onItemClick.onItemClick(
                getItem(position),
                position,
                holder.binding.notifItem));
        holder.binding.notifItemAcceptButton.setOnClickListener(view -> onItemClick.onItemClick(
                getItem(position),
                position,
                holder.binding.notifItemAcceptButton));
        holder.binding.notifItemDiscardButton.setOnClickListener(view -> onItemClick.onItemClick(
                getItem(position),
                position,
                holder.binding.notifItemDiscardButton));

        if (notification.getType() == com.up959875.mobiletraveljournal.other.Notification.FRIEND.ordinal()) {
            holder.binding.notifItemMessage.setText(context.getResources().getString(R.string.notif_request_message));
        } else {
            holder.binding.notifItemMessage.setText(context.getResources().getString(R.string.notif_new_route));
            holder.binding.notifItemAcceptButton.setVisibility(View.GONE);
            holder.binding.notifItemDiscardButton.setText(context.getResources().getString(R.string.notif_remove));
        }
    }

    //Get the date and time for the notification.
    private String getTime(Date date) {
        Calendar currentDate = Calendar.getInstance();
        Calendar notificationDate = Calendar.getInstance();
        notificationDate.setTime(date);

        int cMin = currentDate.get(Calendar.MINUTE);
        int cHour = currentDate.get(Calendar.HOUR_OF_DAY);
        int cDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int cMonth = currentDate.get(Calendar.MONTH);
        int cYear = currentDate.get(Calendar.YEAR);

        int nMin = notificationDate.get(Calendar.MINUTE);
        int nHour = notificationDate.get(Calendar.HOUR_OF_DAY);
        int nDay = notificationDate.get(Calendar.DAY_OF_MONTH);
        int nMonth = notificationDate.get(Calendar.MONTH);
        int nYear = notificationDate.get(Calendar.YEAR);

        if (cYear == nYear)
            if (cMonth == nMonth)
                if (cDay == nDay)
                    if (cHour == nHour)
                        if (cMin == nMin) return "Now";
                        else return cMin - nMin + " min ago";
                    else return cHour - nHour + (cHour - nHour == 1 ? " hour ago" : " hours ago");
                else return cDay - nDay + (cDay - nDay == 1 ? " day ago" : " days ago");
            else return cMonth - nMonth + (cMonth - nMonth == 1 ? " month ago" : " months ago");
        else return cYear - nYear + (cYear - nYear == 1 ? " year ago" : " years ago");
    }

    static class NotifHolder extends RecyclerView.ViewHolder {
        private ListNotificationsBinding binding;
        NotifHolder(ListNotificationsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    public void remove (int position) {
        notifs.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }




}
