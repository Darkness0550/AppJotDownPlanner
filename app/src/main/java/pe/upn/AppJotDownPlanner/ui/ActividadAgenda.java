package pe.upn.AppJotDownPlanner.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.adapters.TaskAdapter;
import pe.upn.AppJotDownPlanner.models.TaskModel;
import pe.upn.AppJotDownPlanner.services.TaskService;

public class ActividadAgenda extends AppCompatActivity {

    private TaskAdapter todayTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        // Setup adapter & recyclerview
        this.todayTasksAdapter = new TaskAdapter(new ArrayList<>(), this);
        RecyclerView rvToday = this.findViewById(R.id.rv_today_tasks);
        rvToday.setLayoutManager(new LinearLayoutManager(this));
        rvToday.setAdapter(this.todayTasksAdapter);

        // Attach ItemTouchHelper to the adapter
        new ItemTouchHelper(completedCallback).attachToRecyclerView(rvToday);

        // Fetch tasks
        this.fetchTasks();
    }

    private void fetchTasks() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TaskService taskService = new TaskService();
        taskService.fetchAllTasksForUser(Objects.requireNonNull(user).getUid() ,new TaskService.FetchTasksCallback() {
            @Override
            public void onSuccess(List<TaskModel> taskModels) {
                // Filter task, display today's tasks
                List<TaskModel> todayTasks = new ArrayList<>();

                LocalDateTime now = LocalDateTime.now();

                for (TaskModel taskModel : taskModels) {
                    String datetime = taskModel.getDateTime();
                    LocalDateTime taskDate = TaskModel.convertToLocalDateTime(datetime);

                    boolean isYearEquals = now.getYear() == taskDate.getYear();
                    boolean isMonthEquals = now.getMonthValue() == taskDate.getMonthValue();
                    boolean isDayEquals = now.getDayOfMonth() == taskDate.getDayOfMonth();

                    if (isDayEquals && isMonthEquals && isYearEquals && !taskModel.isCompleted()) {
                        todayTasks.add(taskModel);
                    }
                }
                todayTasksAdapter.tasks.addAll(todayTasks);
                todayTasksAdapter.notifyItemRangeInserted(0, todayTasks.size());
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    ItemTouchHelper.SimpleCallback completedCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            TaskModel task = todayTasksAdapter.tasks.get(position);

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // Delete task
                    deleteTask(task.getUid(), todayTasksAdapter, position);
                    break;
                case ItemTouchHelper.RIGHT:
                    // Mark task as completed
                    markTaskAsCompleted(task.getUid(), position);
                    break;
                default:
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
                // Remove item from screen
                todayTasksAdapter.tasks.remove(position);
                todayTasksAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}