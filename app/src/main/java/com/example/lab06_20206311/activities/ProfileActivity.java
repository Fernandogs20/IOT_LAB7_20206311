package com.example.lab06_20206311.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab06_20206311.R;
import com.example.lab06_20206311.services.AuthService;
import com.example.lab06_20206311.services.CloudStorage;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final int RC_SELECT_IMAGE = 1001;

    private ImageView ivProfile;
    private TextView tvName, tvEmail, tvProfileUrl;
    private Button btnSelectImage, btnUploadImage;

    private Uri selectedImageUri;
    private CloudStorage cloudStorage;
    private AuthService authService;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvProfileUrl = findViewById(R.id.tvProfileUrl);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);

        cloudStorage = new CloudStorage(this);
        authService = new AuthService(this);
        db = FirebaseFirestore.getInstance();

        // Mostrar datos del usuario
        FirebaseUser user = authService.getCurrentUser();
        if (user != null) {
            tvName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Sin nombre");
            tvEmail.setText(user.getEmail());
            // Si existe URL guardada en Firestore, cargarla
            String uid = user.getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                if (doc != null && doc.contains("profileImageUrl")) {
                    String url = doc.getString("profileImageUrl");
                    if (url != null && !url.isEmpty()) {
                        tvProfileUrl.setText(url);
                        Picasso.get().load(url).into(ivProfile);
                    }
                }
            });
        }

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnUploadImage.setOnClickListener(v -> uploadImage());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), RC_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SELECT_IMAGE && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivProfile.setImageURI(selectedImageUri);
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = authService.getCurrentUserId();
        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "profiles/" + uid + ".jpg";
        btnUploadImage.setEnabled(false);
        btnUploadImage.setText("Subiendo...");

        cloudStorage.uploadImage(path, selectedImageUri, new CloudStorage.StorageCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                // Guardar URL en Firestore
                db.collection("users").document(uid).update("profileImageUrl", downloadUrl)
                        .addOnSuccessListener(aVoid -> {
                            runOnUiThread(() -> {
                                tvProfileUrl.setText(downloadUrl);
                                Toast.makeText(ProfileActivity.this, "Imagen subida. URL: " + downloadUrl, Toast.LENGTH_LONG).show();
                                btnUploadImage.setEnabled(true);
                                btnUploadImage.setText("Subir imagen");
                                // Cargar imagen en ImageView
                                Picasso.get().load(downloadUrl).into(ivProfile);
                            });
                        })
                        .addOnFailureListener(e -> runOnUiThread(() -> {
                            Toast.makeText(ProfileActivity.this, "Error guardando URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            btnUploadImage.setEnabled(true);
                            btnUploadImage.setText("Subir imagen");
                        }));
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Error subiendo imagen: " + error, Toast.LENGTH_LONG).show();
                    btnUploadImage.setEnabled(true);
                    btnUploadImage.setText("Subir imagen");
                });
            }
        });
    }
}
