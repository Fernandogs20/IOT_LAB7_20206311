package com.example.lab06_20206311.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.lab06_20206311.R;
import com.example.lab06_20206311.models.Tarea;

import java.util.ArrayList;
import java.util.List;

public class ResumenFragment extends Fragment {

    private PieChart pieChart;
    private TextView tvTotalTareas, tvTareasCompletadas, tvTareasPendientes;
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumen, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        pieChart = view.findViewById(R.id.pieChart);
        tvTotalTareas = view.findViewById(R.id.tvTotalTareas);
        tvTareasCompletadas = view.findViewById(R.id.tvTareasCompletadas);
        tvTareasPendientes = view.findViewById(R.id.tvTareasPendientes);

        loadResumen();

        return view;
    }

    private void loadResumen() {
        db.collection("users").document(userId).collection("tareas")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int total = 0;
                    int completadas = 0;
                    int pendientes = 0;

                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots) {
                            Tarea tarea = doc.toObject(Tarea.class);
                            if (tarea != null) {
                                total++;
                                if (tarea.isEstado()) {
                                    completadas++;
                                } else {
                                    pendientes++;
                                }
                            }
                        }
                    }

                    updateUI(total, completadas, pendientes);
                });
    }

    private void updateUI(int total, int completadas, int pendientes) {
        tvTotalTareas.setText("Total de Tareas: " + total);
        tvTareasCompletadas.setText("Completadas: " + completadas);
        tvTareasPendientes.setText("Pendientes: " + pendientes);

        List<PieEntry> entries = new ArrayList<>();

        if (completadas > 0) {
            entries.add(new PieEntry(completadas, "Completadas"));
        }
        if (pendientes > 0) {
            entries.add(new PieEntry(pendientes, "Pendientes"));
        }

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "Sin tareas"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Estado de Tareas");

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(76, 175, 80));  // Verde para completadas
        colors.add(Color.rgb(255, 152, 0));  // Naranja para pendientes
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.invalidate();
    }
}