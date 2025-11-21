package com.example.lab06_20206311.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab06_20206311.R;
import com.example.lab06_20206311.services.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private AuthService authService;
    private EditText etNombre, etDni, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar AuthService
        authService = new AuthService(this);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> registerUser());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.isEmpty() || dni.length() < 8) {
            Toast.makeText(this, "Ingresa un DNI válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "El correo es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deshabilitar botón mientras se procesa
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        // Usar AuthService para registrar
        authService.registerUser(nombre, email, password, dni, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(true);
                btnRegister.setText("Registrarse");
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}