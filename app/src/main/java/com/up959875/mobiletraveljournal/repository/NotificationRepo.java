package com.up959875.mobiletraveljournal.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.up959875.mobiletraveljournal.models.Notification;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.up959875.mobiletraveljournal.other.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.MutableLiveData;


public class NotificationRepo {

    //private FirebaseFirestore rootRef;
    private CollectionReference notificationsRef;
    //private CollectionReference usersRef;
    private Context context;

    private NotificationRepo() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        notificationsRef = rootRef.collection(Constants.NOTIFICATIONS);
        //usersRef = rootRef.collection(Constants.USERS);
    }

    public NotificationRepo(Context context) {
        this();
        this.context = context;
    }

    public MutableLiveData<List<Notification>> getNotification(List<String> notificationsIds) {
        MutableLiveData<List<Notification>> notificationsListData = new MutableLiveData<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : notificationsIds)
            tasks.add(notificationsRef.document(id).get());
        Task<List<DocumentSnapshot>> finalTask = Tasks.whenAllSuccess(tasks);
        finalTask.addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                List<Notification> notifications = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    notifications.add(documentSnapshot.toObject(Notification.class));
                }
                notificationsListData.setValue(notifications);
            }
        });
        return notificationsListData;
    }

    public MutableLiveData<Notification> getNotification(String id) {
        MutableLiveData<Notification> notificationData = new MutableLiveData<>();
        notificationsRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Notification notification = document.toObject(Notification.class);
                    if (notification != null) {
                        notificationData.setValue(notification);
                    }
                }
            }
        });
        return notificationData;
    }

    public MutableLiveData<String> removeNotification(String id) {
        MutableLiveData<String> notificationResponse = new MutableLiveData<>();
        notificationsRef.document(id).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationResponse.setValue(context.getResources().getString(R.string.message_remove_notif_success));
            } else {
                notificationResponse.setValue(context.getResources().getString(R.string.message_error_failed_remove_notification));
            }
        });
        return notificationResponse;
    }

    public MutableLiveData<String> sendNotification(User from, User to, Integer type) {
        MutableLiveData<String> notificationResponse = new MutableLiveData<>();
        Notification newNotification = new Notification(from.getUid(), to.getUid(), type);
        DocumentReference notificationRef = notificationsRef.document();
        newNotification.setId(notificationRef.getId());
        notificationRef.set(newNotification).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notificationResponse.setValue(newNotification.getId());
            } else {
                notificationResponse.setValue(context.getResources().getString(R.string.friend_add_failed));
            }
        });
        return notificationResponse;
    }




}
