package pe.upn.AppJotDownPlanner.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserService {

    private  final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private  final FirebaseFirestore fireDb = FirebaseFirestore.getInstance();

    private static final String collectionName = "users";

    /**
     * Registers a user with the given email and password.
     *
     * @param username the username for the user
     * @param email    the email of the user to register
     * @param password the password of the user to register
     * @param callback callback listener for success or error
     */
    public void registerUserByEmail(String username, String email, String password, RegisterCallback callback) {

        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Save the username
                            this.saveUsername(user.getUid(), username, new RegisterUsername() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess(user);
                                }

                                @Override
                                public void onError(String msg) {
                                    callback.onError(msg);
                                }
                            });
                        } else {
                            callback.onError("Error registrando usuario");
                        }
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    /**
     * Saves the username in Firestore using the user's UID as the document ID.
     *
     * @param uid      the user's UID
     * @param username the username to save
     * @param callback callback for Firestore operations
     */
    private void saveUsername(String uid, String username, RegisterUsername callback) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);

        this.fireDb.collection(UserService.collectionName).document(uid)
                .set(userMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    /**
     * Logs in a user with the given email and password.
     *
     * @param email    the email of the user to log in
     * @param password the password of the user to log in
     * @param callback callback listener for success or error
     */
    public void loginUserByEmail(String email, String password, LoginCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onError("Email and password cannot be empty");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onError("User not found");
                        }
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    public void getUsername(String uid, GetUsernameCallback callback) {
        fireDb.collection(UserService.collectionName).document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the username from Firestore
                        String username = documentSnapshot.getString("username");
                        if (username != null) {
                            callback.onSuccess(username);
                        } else {
                            callback.onError("Username not found");
                        }
                    } else {
                        callback.onError("User not found in database");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface GetUsernameCallback {
        void onSuccess(String username);
        void onError(String msg);
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);
        void onError(String msg);
    }

    public interface RegisterCallback {
        void onSuccess(FirebaseUser user);
        void onError(String msg);
    }

    public interface RegisterUsername {
        void onSuccess();
        void onError(String msg);
    }
}
