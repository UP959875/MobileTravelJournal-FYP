package com.up959875.mobiletraveljournal.repository;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Constants;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;


//Handles getting user data, and a list of users.
public class UserRepo {

    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(Constants.USERS);

    /**
     * "Get a user's data from the database and return it as a MutableLiveData object."
     * 
     * The function takes a user's uid as a parameter and returns a MutableLiveData object
     * 
     * @param uid The user's unique ID.
     * @return A MutableLiveData object that contains a User object.
     */
    public MutableLiveData<User> getUserData(String uid) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        DocumentReference userReference = usersRef.document(uid);
        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    userData.setValue(user);
                }
            }
        });
        return userData;
    }

    /**
     * It takes a list of user ids, and returns a list of users
     * 
     * @param usersIds List of user ids
     * @return A list of users.
     */
    public MutableLiveData<List<User>> getUsers(List<String> usersIds) {
        Log.d("l39 getusers", usersIds.toString());
        MutableLiveData<List<User>> usersData = new MutableLiveData<>();
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : usersIds)
            tasks.add(usersRef.document(id).get());
        Task<List<DocumentSnapshot>> finalTask = Tasks.whenAllSuccess(tasks);
        finalTask.addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                List<User> users = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    users.add(documentSnapshot.toObject(User.class));
                }
                usersData.setValue(users);
            }
        });
        return usersData;
    }

    /**
     * This function takes a user object and a map of key-value pairs and updates the user's document
     * in the database with the key-value pairs in the map
     * 
     * @param user The user object that you want to update.
     * @param map A map of the fields to update.
     */
    public void updateUser(User user, Map<String, Object> map) {
        DocumentReference userReference = usersRef.document(user.getUid());
        userReference.update(map);
    }

}
