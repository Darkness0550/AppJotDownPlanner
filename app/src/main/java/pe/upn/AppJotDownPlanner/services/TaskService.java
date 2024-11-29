package pe.upn.AppJotDownPlanner.services;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

import pe.upn.AppJotDownPlanner.models.TaskModel;

public class TaskService {

    // FireStore instance
    private final FirebaseFirestore db;

    // Collection name
    private final String COLLECTION_NAME = "tasks";

    // Constructor to initialize FireStore
    public TaskService() {
        db = FirebaseFirestore.getInstance();
    }

    // Method to save a task to FireStore
    public void saveTask(TaskModel task, SaveTaskCallback callback) {

        // Save the task to FireStore
        CollectionReference tasksCollection = db.collection(COLLECTION_NAME);
        tasksCollection.add(task)
                .addOnSuccessListener(documentReference -> {
                    // Successfully saved task
                    callback.onSuccess("Task added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Failed to save task
                    callback.onError("Error adding task: " + e.getMessage());
                });
    }

    // Method to fetch all tasks for a specific user
    public void fetchAllTasksForUser(String userUid, FetchTasksCallback callback) {
        CollectionReference tasksCollection = db.collection(COLLECTION_NAME);

        // Query to filter tasks by userUid
        tasksCollection.whereEqualTo("userUid", userUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<TaskModel> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String taskUid = document.getId();
                        TaskModel task = document.toObject(TaskModel.class);
                        if (task != null) {
                            task.setUid(taskUid);
                        }

                        tasks.add(task);
                    }
                    callback.onSuccess(tasks);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching tasks: " + e.getMessage()));
    }


    /**
     * Deletes a task given the uid
     *
     * @param taskUid  taskUid to delete
     * @param callback callback listener for success or error
     */
    public void deleteTask(String taskUid, DeleteTaskCallback callback) {
        db.collection(COLLECTION_NAME).document(taskUid)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess("Task deleted successfully"))
                .addOnFailureListener(e -> callback.onError("Error deleting task: " + e.getMessage()));
    }

    /**
     * Updates the isCompleted attribute of a task to true given the uid.
     *
     * @param taskUid  taskUid to update
     * @param callback callback listener for success or error
     */
    public void markTaskAsCompleted(String taskUid, UpdateTaskCallback callback) {
        db.collection(COLLECTION_NAME).document(taskUid)
                .update("completed", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Task marked as completed successfully"))
                .addOnFailureListener(e -> callback.onError("Error marking task as completed: " + e.getMessage()));
    }

    public interface SaveTaskCallback {
        void onSuccess(String msg);
        void onError(String msg);
    }

    public interface FetchTasksCallback {
        void onSuccess(List<TaskModel> taskModels);
        void onError(String msg);
    }

    public interface DeleteTaskCallback {
        void onSuccess(String msg);
        void onError(String msg);
    }

    public interface UpdateTaskCallback {
        void onSuccess(String msg);
        void onError(String msg);
    }
}
