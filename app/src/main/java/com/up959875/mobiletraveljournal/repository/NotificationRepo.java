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


//Handles the notification section of the database.
public class NotificationRepo {

    //private FirebaseFirestore rootRef;
    private CollectionReference notificationsRef;
    //private CollectionReference usersRef;
    private Context context;

    //Constructor for the class
    private NotificationRepo() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        notificationsRef = rootRef.collection(Constants.NOTIFICATIONS);
        //usersRef = rootRef.collection(Constants.USERS);
    }

    //Constructor for the class with context.
    public NotificationRepo(Context context) {
        this();
        this.context = context;
    }

    /**
     * It takes a list of notification ids, creates a list of tasks to get each notification, then
     * creates a final task that waits for all the tasks to complete, and then returns a
     * MutableLiveData object that contains the list of notifications
     * 
     * @param notificationsIds List of notification ids
     * @return A MutableLiveData object that contains a list of Notification objects.
     */
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

    /**
     * It returns a MutableLiveData object that contains a Notification object
     * 
     * @param id The id of the notification
     * @return A MutableLiveData object that contains a Notification object.
     */
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

    /**
     * It removes a notification from the database
     * 
     * @param id The id of the notification to be removed
     * @return A MutableLiveData object.
     */
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

    /**
     * I'm trying to create a notification object, and then add it to the database
     * 
     * @param from The user who sent the notification
     * @param to The user to send the notification to
     * @param type 1 = friend request, 2 = friend request accepted, 3 = friend request declined, 4 =
     * friend request cancelled
     * @return A MutableLiveData object.
     */
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
