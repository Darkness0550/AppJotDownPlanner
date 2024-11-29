package pe.upn.AppJotDownPlanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pe.upn.AppJotDownPlanner.services.UserService;
import pe.upn.AppJotDownPlanner.ui.HomeActividadesH;
import pe.upn.AppJotDownPlanner.ui.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usu;
    private EditText clave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is logged in, navigate to HomeActividadesH
            startActivity(new Intent(MainActivity.this, HomeActividadesH.class));
            finish();  // Close the login activity
        }

        // Vinculación de vistas
        usu = findViewById(R.id.txtUsuario);
        clave = findViewById(R.id.txtClave);

        // TextView para "Registrate aquí"
        TextView tvCreateAcc = findViewById(R.id.tvCreateAcc);
        tvCreateAcc.setOnClickListener(
                v -> startActivity(new Intent(this, RegisterActivity.class))
        );

        // Login button
        Button btnLogin = this.findViewById(R.id.btnAcceder);
        btnLogin.setOnClickListener(v -> login());
    }

    // Método llamado al hacer clic en el botón de acceso
    public void login() {
        String email = usu.getText().toString();
        String password = clave.getText().toString();


        UserService service = new UserService();
        service.loginUserByEmail(email, password, new UserService.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                startActivity(new Intent(getApplicationContext(), HomeActividadesH.class));
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
