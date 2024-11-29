package pe.upn.AppJotDownPlanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import pe.upn.AppJotDownPlanner.R;
import pe.upn.AppJotDownPlanner.services.UserService;

public class RegisterActivity extends AppCompatActivity implements UserService.RegisterCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnRegister = this.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> this.registerUser());

    }

    private void registerUser() {
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);

        TextView tvError = this.findViewById(R.id.tvError);
        tvError.setText("Contraseñas no coinciden");
        tvError.setVisibility(View.GONE);

        EditText etUsername, etEmail, etPass, etConfirmPass;
        etUsername = this.findViewById(R.id.etUsername);
        etEmail = this.findViewById(R.id.etUserEmail);
        etPass = this.findViewById(R.id.etUserPassword);
        etConfirmPass = this.findViewById(R.id.etUserConfPass);

        String username, email, password, confirmPassword;
        username = etUsername.getText().toString();
        email = etEmail.getText().toString();
        password = etPass.getText().toString();
        confirmPassword = etConfirmPass.getText().toString();

        if (password.equals(confirmPassword)) {
            // register user
            FirebaseApp.initializeApp(this);
           UserService service = new UserService();
           service.registerUserByEmail(username, email, password, this);
        } else {
            // Show error
            tvError = this.findViewById(R.id.tvError);
            tvError.setText("Contraseñas no coinciden");
            tvError.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(true);
        }
    }

    @Override
    public void onSuccess(FirebaseUser user) {
        startActivity(new Intent(this, HomeActividadesH.class));
        finish();
    }

    @Override
    public void onError(String msg) {
        // Show error
        TextView tvError = this.findViewById(R.id.tvError);
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setEnabled(true);
    }
}