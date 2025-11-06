package com.example.lab06_20206311.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab06_20206311.R;
import com.example.lab06_20206311.models.Tarea;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {

    private List<Tarea> tareasList;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnToggleClickListener toggleClickListener;

    public interface OnEditClickListener {
        void onEditClick(Tarea tarea);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Tarea tarea);
    }

    public interface OnToggleClickListener {
        void onToggleClick(Tarea tarea);
    }

    public TareasAdapter(List<Tarea> tareasList, OnEditClickListener editClickListener,
                         OnDeleteClickListener deleteClickListener, OnToggleClickListener toggleClickListener) {
        this.tareasList = tareasList;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
        this.toggleClickListener = toggleClickListener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareasList.get(position);

        holder.tvTitulo.setText(tarea.getTitulo());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvFechaLimite.setText("Fecha límite: " + sdf.format(tarea.getFechaLimite()));

        holder.cbEstado.setChecked(tarea.isEstado());
        holder.tvEstado.setText(tarea.isEstado() ? "Completada" : "Pendiente");

        holder.cbEstado.setOnClickListener(v -> {
            if (toggleClickListener != null) {
                toggleClickListener.onToggleClick(tarea);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(tarea);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(tarea);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tareasList.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFechaLimite, tvEstado;
        CheckBox cbEstado;
        ImageButton btnEdit, btnDelete;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFechaLimite = itemView.findViewById(R.id.tvFechaLimite);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            cbEstado = itemView.findViewById(R.id.cbEstado);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}