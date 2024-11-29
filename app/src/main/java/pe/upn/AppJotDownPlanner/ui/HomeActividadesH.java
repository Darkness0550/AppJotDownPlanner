package pe.upn.AppJotDownPlanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pe.upn.AppJotDownPlanner.MainActivity;
import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.models.TaskModel;
import pe.upn.AppJotDownPlanner.services.TaskService;
import pe.upn.AppJotDownPlanner.services.UserService;

import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeActividadesH extends AppCompatActivity {

    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_home_actividadesh); // Verifica que el nombre del layout sea correcto

        tvProgress = findViewById(R.id.tv_progress);

        // Configuración de secciones
        CardView calendarSection = findViewById(R.id.calendar_section);
        calendarSection.setOnClickListener(this::openCalendar);

        CardView agendaSection = findViewById(R.id.agenda_section);
        agendaSection.setOnClickListener(this::openAgenda);

        CardView organizerSection = findViewById(R.id.organizer_section);
        organizerSection.setOnClickListener(this::openOrganizer);

        CardView statsSection = findViewById(R.id.stats_section);
        statsSection.setOnClickListener(this::openStatistics);

        // Fetch tasks
        this.fetchTasks();

        // Close session
        ImageButton btnCloseSession = this.findViewById(R.id.btn_close_session);
        btnCloseSession.setOnClickListener(v -> this.closeSession());

        // Get username of current user
        this.getCurrentUsername();
    }

    private void getCurrentUsername() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserService userService = new UserService();

        String userUid = Objects.requireNonNull(firebaseUser).getUid();

        userService.getUsername(userUid, new UserService.GetUsernameCallback() {
            @Override
            public void onSuccess(String username) {
                TextView tvUserName = findViewById(R.id.tv_user_name);
                String msg = String.format(Locale.getDefault(), "Bienvenido(a) %s", username);
                tvUserName.setText(msg);
            }

            @Override
            public void onError(String msg) {
                TextView tvUserName = findViewById(R.id.tv_user_name);
                String msgWelcome = "Bienvenido(a), Desconocido";
                tvUserName.setText(msgWelcome);
            }
        });
    }

    private void closeSession() {
        // Close session from Application
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        // Redirect to login screen
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.fetchTasks();
    }


    public void openCalendar(View view) {
        Log.d("HomeActividadesH", "Abriendo Calendario"); //
        Intent intent = new Intent(this, ActividadCalendario.class);
        startActivity(intent);
    }

    public void openAgenda(View view) {
        Intent intent = new Intent(this, ActividadAgenda.class);
        startActivity(intent);
    }

    public void openOrganizer(View view) {
        Intent intent = new Intent(this, ActividadOrganizador.class);
        startActivity(intent);
    }

    public void openStatistics(View view) {
        Intent intent = new Intent(this, ActividadEstadisticas.class);
        startActivity(intent);
    }

    private void fetchTasks() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TaskService taskService = new TaskService();
        taskService.fetchAllTasksForUser(Objects.requireNonNull(user).getUid(), new TaskService.FetchTasksCallback() {
            @Override
            public void onSuccess(List<TaskModel> taskModels) {
                double completed = 0;

                // Count tasks
                for (TaskModel taskModel : taskModels) {
                    if (taskModel.isCompleted()) completed++;
                }

                // Avoid division by zero
                double progress = taskModels.isEmpty() ? 0 : (completed * 100) / taskModels.size();

                String progressData = String.format(Locale.getDefault(), "Progreso: %.2f %%", progress);
                tvProgress.setText(progressData);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
