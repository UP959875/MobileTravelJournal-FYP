package com.up959875.mobiletraveljournal.repository;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.up959875.mobiletraveljournal.models.DataWrapper;
import com.up959875.mobiletraveljournal.models.User;
import com.up959875.mobiletraveljournal.other.Status;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.up959875.mobiletraveljournal.other.Constants;
import com.google.firebase.auth.UserProfileChangeRequest;

//Handles authentication for email and Google. Deals with sign ups, verification, resetting passwords, and changing details.
public class AuthRepo {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(Constants.USERS);

    public AuthRepo() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


   /**
    * It creates a new user with the given email and password, and then updates the user's profile with
    * the given username
    * 
    * @param email String
    * @param password String
    * @param username String
    * @return A MutableLiveData object that contains a DataWrapper object that contains a User object.
    */
    public MutableLiveData<DataWrapper<User>> signUpWithEmail(String email, String password, String username) {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                    firebaseUser.updateProfile(profileUpdates);

                    User user = new User(firebaseUser.getUid(), username, email);
                    userLiveData.setValue(new DataWrapper<>(user, Status.LOADING,
                            "Sending verification email now"));

                }
            } else  {handleUserDataErrors(authTask, userLiveData);
        }
    });
        return userLiveData;
}

    /**
     * It sends a verification email to the user's email address
     * 
     * @return A MutableLiveData object that contains a DataWrapper object.
     */
    public MutableLiveData<DataWrapper<User>> sendVerificationMail() {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userLiveData.setValue(new DataWrapper<>(null, Status.SUCCESS,
                            "A verification email has been sent. Verify your account to continue"));
                } else {
                    handleUserDataErrors(task, userLiveData);
                }
            });
        } else {
            userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                    "ERROR: User identity error. Please try again later"));
        }
            return userLiveData;
        }


    /**
     * It takes a Google AuthCredential, signs in with it, and returns a MutableLiveData object that
     * contains a DataWrapper object that contains a User object
     * 
     * @param googleAuthCredential The GoogleAuthCredential object that you get from the
     * GoogleSignInAccount object.
     * @return A MutableLiveData object that contains a DataWrapper object.
     */
    public MutableLiveData<DataWrapper<User>> signInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    String email = firebaseUser.getEmail();

                    User user = new User(uid, name, email);
                    usersRef.document(uid).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            boolean isAdded;
                            isAdded = document != null && document.exists();
                            userLiveData.setValue(new DataWrapper<>(user, Status.SUCCESS,
                                    "Authorization successful", true, isAdded, true));
                        } else {
                            handleUserDataErrors(task, userLiveData);
                        }
                    });
                } else {
                    userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                            "ERROR: User identity error. Please try again later"));
                }
            } else {
                handleUserDataErrors(authTask, userLiveData);
            }
        });

        return userLiveData;
    }


   /**
    * It checks if a user exists in the database, if not, it adds the user to the database
    * 
    * @param user DataWrapper<User>
    * @return A LiveData object that contains a DataWrapper object that contains a User object.
    */
    public LiveData<DataWrapper<User>> addUserToDatabase(DataWrapper<User> user) {
        MutableLiveData<DataWrapper<User>> newUserLiveData = new MutableLiveData<>();
        DocumentReference uidRef = usersRef.document(user.getData().getUid());
        uidRef.get().addOnCompleteListener(uidTask -> {
            if (uidTask.isSuccessful()) {
                user.setAdded(true);
                DocumentSnapshot document = uidTask.getResult();
                if (document != null && !document.exists()) {
                    uidRef.set(user.getData()).addOnCompleteListener(addingTask -> {
                        if (addingTask.isSuccessful()) {

                            newUserLiveData.setValue(user);
                        } else if (addingTask.getException() != null){
                            newUserLiveData.setValue(new DataWrapper<>(null, Status.ERROR, "Error occured: " + addingTask.getException().getMessage()));
                        }
                    });
                } else {
                    newUserLiveData.setValue(user);
                }
            } else if (uidTask.getException() != null) {
                    newUserLiveData.setValue(new DataWrapper<>(null, Status.ERROR, "Error occurred: " + uidTask.getException().getMessage()));
            }
        });
        return newUserLiveData;
    }

    /**
     * It takes in an email and password, and returns a LiveData object that contains a DataWrapper
     * object that contains a User object
     * 
     * @param email The email address of the user.
     * @param password The password for the email address.
     * @return A LiveData object that contains a DataWrapper object.
     */
    public LiveData<DataWrapper<User>> logInWithEmail(String email, String password) {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    String name = firebaseUser.getDisplayName();
                    boolean isNew = (authTask.getResult() != null && authTask.getResult().getAdditionalUserInfo() != null)
                            && authTask.getResult().getAdditionalUserInfo().isNewUser();
                    boolean isVerified = firebaseUser.isEmailVerified();
                    User user = new User(uid, name, email);
                    userLiveData.setValue(new DataWrapper<>(user, Status.SUCCESS,
                            "Authorization successful!", true, true, isVerified));
                }
            } else {
                handleUserDataErrors(authTask, userLiveData);
            }
        });
        return userLiveData;
    }


    /**
     * "This function sends a password reset email to the user, and returns a LiveData object that
     * contains the status of the operation."
     * 
     * The function takes in a string, which is the user's email address. It then creates a
     * MutableLiveData object, which is a LiveData object that can be modified. 
     * 
     * The function then calls the sendPasswordResetEmail() function from the FirebaseAuth class. This
     * function takes in the user's email address, and sends a password reset email to the user. 
     * 
     * The function then adds an OnCompleteListener to the task returned by the
     * sendPasswordResetEmail() function. This listener is called when the task is completed. 
     * 
     * If the task is successful, the function sets the value of the MutableLiveData object to a
     * DataWrapper object that contains a null User object, a Status.SUCCESS status, and a message. 
     * 
     * If the task is not successful, the error is given to the user.
     * 
     * @param email The email address of the user.
     * @return A LiveData object that contains a DataWrapper object.
     */
    public LiveData<DataWrapper<User>> sendPasswordResetEmail(String email) {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userLiveData.setValue(new DataWrapper<>(null, Status.SUCCESS,
                        "Password reset email has been sent. Click the link in the email to reset your password"));
            } else {
                handleUserDataErrors(task, userLiveData);
            }
        });
        return userLiveData;
    }

    /**
     * This function gets a user from the database and returns a LiveData object that contains the user
     * data
     * 
     * @param uid The user's unique ID
     * @return A LiveData object that contains a DataWrapper object that contains a User object.
     */
    public LiveData<DataWrapper<User>> getUser(String uid) {
        MutableLiveData<DataWrapper<User>> userLiveData = new MutableLiveData<>();
        DocumentReference userReference = usersRef.document(uid);
        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    User user = document.toObject(User.class);
                    userLiveData.setValue(new DataWrapper<>(user, Status.SUCCESS,
                            "Getting user data was successful", true, true, true));
                } else {
                    userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                            "ERROR: This user does not exist in the database", true, false, true));
                }
            } else {
                handleUserDataErrors(task, userLiveData);
            }
        });
        return userLiveData;
    }

    /**
     * This function takes a new username as a parameter and updates the user's display name in the
     * Firebase Authentication database
     * 
     * @param newUsername The new username that the user wants to change to.
     * @return A LiveData object that contains a String.
     */
    public LiveData<String> changeUsername(String newUsername) {
        MutableLiveData<String> status = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();
            user.updateProfile(profileUpdates);
            status.setValue("Username successfully changed!");
        } else
            status.setValue("ERROR: User identity error. Please try again later");
        return status;
    }

    /**
     * It takes the current password and the new email as parameters, and returns a LiveData object
     * that contains the status of the operation. If successful, the password will be changed at the end.
     * 
     * @param currentPassword The user's current password.
     * @param newEmail The new email address you want to update to.
     * @return A LiveData object that contains a String.
     */
    public LiveData<String> changeEmail(String currentPassword, String newEmail) {
        MutableLiveData<String> status = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updateEmail(newEmail).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            status.setValue("Email has been changed");
                        } else if(task1.getException() != null) {
                            status.setValue("ERROR: " + task1.getException().getMessage());
                        }
                    });
                } else if(task.getException() != null) {
                    status.setValue("ERROR: " + task.getException().getMessage());
                }
            });
        }
        return status;
    }

    /**
     * It takes the current password and the new password as parameters, and returns a LiveData object
     * that contains the status of the operation
     * 
     * @param currentPassword The user's current password.
     * @param newPassword The new password for the user.
     * @return A LiveData object that contains a String.
     */
    public LiveData<String> changePassword(String currentPassword, String newPassword) {
        MutableLiveData<String> status = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            status.setValue("Your password has been changed");
                        } else if(task1.getException() != null) {
                            status.setValue("ERROR: " + task1.getException().getMessage());
                        }
                    });
                } else if(task.getException() != null) {
                    status.setValue("ERROR: " + task.getException().getMessage());
                }
            });
        }
        return status;
    }


    /**
     * If the task is successful, then set the userLiveData to the user object, otherwise set the
     * userLiveData to an error message
     * 
     * @param authTask The Task object returned from the FirebaseAuth.signInWithCredential() method.
     * @param userLiveData This is the MutableLiveData object that will be updated with the user data.
     */
    private void handleUserDataErrors(Task authTask, MutableLiveData<DataWrapper<User>> userLiveData) {
        if (authTask.getException() != null) {
            try {
                throw authTask.getException();
            } catch (FirebaseNetworkException e) {
                userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                        "Error: Please check your network connection"));
            } catch (Exception e) {
                userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                        "Error: " + e.getMessage()));
            }
        } else {
            userLiveData.setValue(new DataWrapper<>(null, Status.ERROR,
                    "Error: Unhandled authorization error"));
        }
    }

}
