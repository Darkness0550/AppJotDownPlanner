package pe.upn.AppJotDownPlanner.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.List;
import java.util.Objects;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.models.TaskModel;
import pe.upn.AppJotDownPlanner.services.TaskService;

public class ActividadEstadisticas extends AppCompatActivity {

    private PieChart taskChartPie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Initialize the PieChart
        this.taskChartPie  = findViewById(R.id.task_chart_pie);

        // Fetch tasks from FireBase
        this.fetchTasks();
    }

    private void addDataToPieChart(PieChart pieChart, int completed, int uncompleted) {
        // Add slices with constant values
        pieChart.addPieSlice(new PieModel("Completadas", completed, Color.parseColor("#4CAF50"))); // Green
        pieChart.addPieSlice(new PieModel("Pendientes", uncompleted, Color.parseColor("#FF9800"))); // Orange

        // Update TextView data
        TextView tvCompleted, tvUncompleted;
        tvCompleted = findViewById(R.id.tv_completed_count);
        tvUncompleted = findViewById(R.id.tv_uncompleted_count);

        String completedData = "Completadas: " + completed,
                uncompletedData = "Pendientes: " + uncompleted;

        tvCompleted.setText(completedData);
        tvUncompleted.setText(uncompletedData);

        // Start animation
        pieChart.startAnimation();
    }

    private void fetchTasks() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TaskService taskService = new TaskService();
        taskService.fetchAllTasksForUser(Objects.requireNonNull(user).getUid(), new TaskService.FetchTasksCallback() {
            @Override
            public void onSuccess(List<TaskModel> taskModels) {
                int completed = 0;

                // Count tasks
                for (TaskModel taskModel : taskModels) {
                    if (taskModel.isCompleted()) completed++;
                }

                addDataToPieChart(taskChartPie, completed, taskModels.size() - completed);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
