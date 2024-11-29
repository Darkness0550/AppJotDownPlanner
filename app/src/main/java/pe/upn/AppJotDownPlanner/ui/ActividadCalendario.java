package pe.upn.AppJotDownPlanner.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.models.TaskModel;
import pe.upn.AppJotDownPlanner.services.TaskService;

public class ActividadCalendario extends AppCompatActivity implements TaskService.SaveTaskCallback{

    private CalendarView calendarView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        // Back button
        ImageButton backButton = this.findViewById(R.id.btnCalendarBack);
        backButton.setOnClickListener(v -> this.finish());

        // Vinculamos el CalendarView
        calendarView = findViewById(R.id.calendar_view);

        // Get the current logged user
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        // Listener para cuando se selecciona una fecha
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                showAddActivityDialog(year, month, dayOfMonth);
            }
        });
    }

    public void showAddActivityDialog(int year, int month, int day) {
        // Inflate the custom layout
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_activity_layout, null);

        // Find the EditText and TimePicker
        EditText activityName = dialogView.findViewById(R.id.activity_name);
        EditText activityDesc = dialogView.findViewById(R.id.activity_desc);
        TimePicker taskTimePicker = dialogView.findViewById(R.id.taskTimePicker);

        // Create and configure the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView)
                .setCancelable(false)

                // Set cancel button
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })

                // Set save button
                .setPositiveButton("Agregar", (dialog, which) -> {
                    // Get the selected time from the TimePicker
                    int hour = taskTimePicker.getHour();  // For API 23+ (getHour() method)
                    int minute = taskTimePicker.getMinute();  // For API 23+ (getMinute() method)

                    // Now you can use the hour and minute values along with activity name and description
                    String taskName = activityName.getText().toString();
                    String taskDesc = activityDesc.getText().toString();

                    // Format date and create model
                    LocalDateTime taskTime = LocalDateTime.of(year, month + 1, day, hour, minute);
                    TaskModel taskModel = new TaskModel(
                            this.user.getUid(),
                            taskName, taskDesc,
                            TaskModel.convertToString(taskTime)
                    );

                    // save the activity or task
                    TaskService taskService = new TaskService();
                    taskService.saveTask(taskModel, this);
                });

        // Show the dialog
        builder.show();
    }

    @Override
    public void onSuccess(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
