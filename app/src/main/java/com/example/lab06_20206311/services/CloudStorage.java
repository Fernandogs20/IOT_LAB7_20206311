package com.example.lab06_20206311.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;

/**
 * Servicio para subir y obtener archivos desde Firebase Storage
 */
public class CloudStorage {

    private static final String TAG = "CloudStorage";
    private FirebaseStorage storage;
    private StorageReference rootRef;

    public interface StorageCallback {
        void onSuccess(String downloadUrl);
        void onError(String error);
    }

    public CloudStorage(Context context) {
        // Inicializa instancia de Firebase Storage con el bucket correcto
        try {
            // Usar el bucket correcto directamente: gs://lab520206311.appspot.com
            final String CORRECT_BUCKET = "gs://lab520206311.appspot.com";
            
            storage = FirebaseStorage.getInstance(CORRECT_BUCKET);
            android.util.Log.d(TAG, "Initialized FirebaseStorage with correct bucket: " + CORRECT_BUCKET);
            
        } catch (Exception ex) {
            android.util.Log.w(TAG, "Failed to init FirebaseStorage with explicit bucket: " + ex.getMessage());
            // Fallback a instancia por defecto
            storage = FirebaseStorage.getInstance();
        }

        rootRef = storage.getReference();
        android.util.Log.d(TAG, "Storage reference root: " + (rootRef != null ? rootRef.toString() : "null"));
    }

    /**
     * Sube una imagen a la ruta indicada y devuelve la URL de descarga
     * @param path ruta en storage (ej: "profiles/{uid}.jpg")
     * @param fileUri Uri del archivo a subir
     * @param callback callback con resultado
     */
    public void uploadImage(String path, Uri fileUri, StorageCallback callback) {
        if (fileUri == null) {
            callback.onError("Archivo inválido");
            return;
        }

        StorageReference ref = rootRef.child(path);

        UploadTask uploadTask = ref.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtener URL de descarga
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d(TAG, "Upload exitoso. URL=" + downloadUrl);
                callback.onSuccess(downloadUrl);
            }).addOnFailureListener(e -> {
                String err = e.getMessage() != null ? e.getMessage() : "Error obteniendo URL";
                Log.e(TAG, err, e);
                callback.onError(err);
            });
        }).addOnFailureListener(e -> {
            String err = e.getMessage() != null ? e.getMessage() : "Error subiendo archivo";
            Log.e(TAG, err, e);
            callback.onError(err);
        });
    }

    /**
     * Devuelve la referencia raíz (si se necesita)
     */
    public StorageReference getRootReference() {
        return rootRef;
    }
}
