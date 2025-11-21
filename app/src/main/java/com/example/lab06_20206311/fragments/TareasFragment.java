package com.example.lab06_20206311.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.lab06_20206311.R;
import com.example.lab06_20206311.adapters.TareasAdapter;
import com.example.lab06_20206311.models.Tarea;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TareasFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddTask;
    private TareasAdapter adapter;
    private List<Tarea> tareasList;
    private CollectionReference tareasRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tareas, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        tareasRef = db.collection("users").document(userId).collection("tareas");

        recyclerView = view.findViewById(R.id.recyclerViewTareas);
        fabAddTask = view.findViewById(R.id.fabAddTask);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tareasList = new ArrayList<>();
        adapter = new TareasAdapter(tareasList, this::onEditTask, this::onDeleteTask, this::onToggleTask);
        recyclerView.setAdapter(adapter);

        fabAddTask.setOnClickListener(v -> showAddEditDialog(null));

        loadTareas();

        return view;
    }

    private void loadTareas() {
        tareasRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Context ctx = getContext();
                if (ctx != null) {
                    Toast.makeText(ctx, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            tareasList.clear();
            for (DocumentSnapshot doc : snapshots) {
                Tarea tarea = doc.toObject(Tarea.class);
                if (tarea != null) {
                    tarea.setId(doc.getId());
                    tareasList.add(tarea);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void showAddEditDialog(Tarea tarea) {
        boolean isEdit = tarea != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText etTitulo = dialogView.findViewById(R.id.etTitulo);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        Button btnFechaLimite = dialogView.findViewById(R.id.btnFechaLimite);
        CheckBox cbEstado = dialogView.findViewById(R.id.cbEstado);

        Calendar calendar = Calendar.getInstance();
        long[] fechaSeleccionada = {System.currentTimeMillis()};

        if (isEdit) {
            etTitulo.setText(tarea.getTitulo());
            etDescripcion.setText(tarea.getDescripcion());
            cbEstado.setChecked(tarea.isEstado());
            fechaSeleccionada[0] = tarea.getFechaLimite();
            calendar.setTimeInMillis(tarea.getFechaLimite());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            btnFechaLimite.setText(sdf.format(calendar.getTime()));
        }

        btnFechaLimite.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(),
                    (view, year, month, day) -> {
                        calendar.set(year, month, day);
                        fechaSeleccionada[0] = calendar.getTimeInMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        btnFechaLimite.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        builder.setTitle(isEdit ? "Editar Tarea" : "Nueva Tarea")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    if (titulo.isEmpty()) {
                        Context ctx = getContext();
                        if (ctx != null) {
                            Toast.makeText(ctx, "El título es obligatorio", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    Tarea nuevaTarea = new Tarea(
                            titulo,
                            etDescripcion.getText().toString().trim(),
                            fechaSeleccionada[0],
                            cbEstado.isChecked(),
                            userId
                    );

                    if (isEdit) {
                        nuevaTarea.setId(tarea.getId());
                        tareasRef.document(tarea.getId()).set(nuevaTarea)
                                .addOnSuccessListener(aVoid -> {
                                    Context ctx = getContext();
                                    if (ctx != null) {
                                        Toast.makeText(ctx, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        tareasRef.add(nuevaTarea)
                                .addOnSuccessListener(docRef -> {
                                    Context ctx = getContext();
                                    if (ctx != null) {
                                        Toast.makeText(ctx, "Tarea agregada", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void onEditTask(Tarea tarea) {
        showAddEditDialog(tarea);
    }

    private void onDeleteTask(Tarea tarea) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    tareasRef.document(tarea.getId()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Context ctx = getContext();
                                if (ctx != null) {
                                    Toast.makeText(ctx, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void onToggleTask(Tarea tarea) {
        tarea.setEstado(!tarea.isEstado());
        tareasRef.document(tarea.getId()).set(tarea);
    }
}