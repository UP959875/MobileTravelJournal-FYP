package com.up959875.mobiletraveljournal.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import android.app.Application;
import java.util.List;
import com.up959875.mobiletraveljournal.models.Notification;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.repository.NotificationRepo;

public class NotificationViewModel extends AndroidViewModel {

    private NotificationRepo notificationRepo;
    private LiveData<String> notificationResponse;
    private LiveData<List<Notification>> notificationListLiveData;
    private LiveData<Notification> notificationLiveData;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        notificationRepo = new NotificationRepo(application.getApplicationContext());
    }

    public LiveData<String> getNotificationResponse() {
        return notificationResponse;
    }

    public void getNotificationData(String id) {
        notificationLiveData = notificationRepo.getNotification(id);
    }

    public void getNotificationsListData(List<String> notificationsIds) {
        notificationListLiveData = notificationRepo.getNotification(notificationsIds);
    }

    public void removeNotification(String id) {
        notificationResponse = notificationRepo.removeNotification(id);
    }


    public LiveData<List<Notification>> getNotificationsList() {
        return notificationListLiveData;
    }

    public LiveData<Notification> getNotification() {
        return notificationLiveData;
    }

    public void sendNotification(User from, User to, Integer type) {
        notificationResponse = notificationRepo.sendNotification(from, to, type);
    }


}
