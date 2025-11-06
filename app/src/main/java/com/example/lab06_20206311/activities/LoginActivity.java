package com.example.lab06_20206311.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab06_20206311.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogleSignIn;
    private LoginButton btnFacebookLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Inicialización del SDK de Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAdvertiserIDCollectionEnabled(false);

        setContentView(R.layout.activity_login);

        // ✅ Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicialización de vistas y configuración
        initViews();
        setupGoogleSignIn();
        setupFacebookLogin();
        setupListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // ✅ Mover esta validación aquí evita micro-lags en onCreate
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupGoogleSignIn() {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
    }

    private void setupFacebookLogin() {
        mCallbackManager = CallbackManager.Factory.create();
        btnFacebookLogin.setPermissions("email", "public_profile");

        btnFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> loginWithEmail());
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Intentando login con: " + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Login exitoso con UID: " + mAuth.getCurrentUser().getUid());
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(this::goToMainActivity, 300);
                    } else {
                        Log.e(TAG, "Fallo en login", task.getException());
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión con Facebook exitoso", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(this::goToMainActivity, 300);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(this::goToMainActivity, 300);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
