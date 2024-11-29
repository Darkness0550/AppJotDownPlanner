package pe.upn.AppJotDownPlanner.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.adapters.TaskAdapter;
import pe.upn.AppJotDownPlanner.models.TaskModel;
import pe.upn.AppJotDownPlanner.services.TaskService;

public class ActividadOrganizador extends AppCompatActivity {

    private TaskAdapter uncompletedAdapter;
    private TaskAdapter completedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizador);

        // Setup recyclerview and adapter
        this.uncompletedAdapter = new TaskAdapter(new ArrayList<>(), this);
        RecyclerView rvUncompleted = this.findViewById(R.id.rv_uncompleted_tasks);
        rvUncompleted.setLayoutManager(new LinearLayoutManager(this));
        rvUncompleted.setAdapter(this.uncompletedAdapter);

        // Completed tasks
        this.completedAdapter = new TaskAdapter(new ArrayList<>(), this);
        RecyclerView rvCompleted = this.findViewById(R.id.rv_completed_tasks);
        rvCompleted.setLayoutManager(new LinearLayoutManager(this));
        rvCompleted.setAdapter(this.completedAdapter);

        // Attach ItemTouchHelper for completed tasks
        new ItemTouchHelper(completedCallback).attachToRecyclerView(rvCompleted);

        // Attach ItemTouchHelper for uncompleted tasks
        new ItemTouchHelper(uncompletedCallback).attachToRecyclerView(rvUncompleted);

        // Fetch all tasks
        this.fetchTasks();
    }

    ItemTouchHelper.SimpleCallback completedCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TaskModel task = completedAdapter.tasks.get(position);

            // Delete task
            deleteTask(task.getUid(), completedAdapter, position);
        }
    };

    ItemTouchHelper.SimpleCallback uncompletedCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TaskModel task = uncompletedAdapter.tasks.get(position);

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // Delete task
                    deleteTask(task.getUid(), uncompletedAdapter, position);
                    break;

                case ItemTouchHelper.RIGHT:
                    // Mark as completed
                    markTaskAsCompleted(task.getUid(), position);
                    break;
            }
        }
    };

    private void deleteTask(String taskUid, TaskAdapter taskAdapter, int taskPosition) {
        TaskService taskService = new TaskService();
        taskService.deleteTask(taskUid, new TaskService.DeleteTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                taskAdapter.tasks.remove(taskPosition);
                taskAdapter.notifyItemRemoved(taskPosition);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markTaskAsCompleted(String taskUid, int position) {
        // Change data on FireStore
        TaskService taskService = new TaskService();
        taskService.markTaskAsCompleted(taskUid, new TaskService.UpdateTaskCallback() {
            @Override
            public void onSuccess(String msg) {
                // Move from uncompleted to completed list
                TaskModel updatedTask = uncompletedAdapter.tasks.get(position);
                updatedTask.setCompleted(true);

                // Remove from uncompleted
                uncompletedAdapter.tasks.remove(position);
                uncompletedAdapter.notifyItemRemoved(position);

                // Add to completed list
                completedAdapter.tasks.add(updatedTask);
                completedAdapter.notifyItemInserted(completedAdapter.tasks.size());

                // If uncompleted task is empty show lottie animation
                LottieAnimationView uncompletedView = findViewById(R.id.lottie_uncompleted);
               if (uncompletedAdapter.tasks.isEmpty()) uncompletedView.setVisibility(View.VISIBLE);
               else uncompletedView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTasks() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TaskService taskService = new TaskService();
        taskService.fetchAllTasksForUser(Objects.requireNonNull(user).getUid(), new TaskService.FetchTasksCallback() {
            @Override
            public void onSuccess(List<TaskModel> taskModels) {

                // List of tasks
                List<TaskModel> completedTasks = new ArrayList<>();
                List<TaskModel> uncompletedTasks = new ArrayList<>();

                // Lottie animations
                LottieAnimationView uncompletedView = findViewById(R.id.lottie_uncompleted);

                // Filter completed tasks
                for (TaskModel taskModel : taskModels) {
                    if (taskModel.isCompleted()) {
                        completedTasks.add(taskModel);
                    } else {
                        uncompletedTasks.add(taskModel);
                    }
                }

                // Hide progress bar
                ProgressBar pbUncompleted = findViewById(R.id.pb_uncompleted_tasks);
                pbUncompleted.setVisibility(View.GONE);

                // Uncompleted tasks
                if (!uncompletedTasks.isEmpty()) {
                    uncompletedAdapter.tasks = uncompletedTasks;
                    uncompletedAdapter.notifyItemRangeInserted(0, uncompletedTasks.size());
                    uncompletedView.setVisibility(View.GONE); // Hide lottie animation
                } else {
                    uncompletedView.setVisibility(View.VISIBLE);
                }

                // Completed tasks
                completedAdapter.tasks = completedTasks;
                completedAdapter.notifyItemRangeInserted(0, completedTasks.size());

                // Hide progress bar
                ProgressBar pbCompleted = findViewById(R.id.pb_completed_tasks);
                pbCompleted.setVisibility(View.GONE);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
