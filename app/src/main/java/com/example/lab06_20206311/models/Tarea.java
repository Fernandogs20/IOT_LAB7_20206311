package com.example.lab06_20206311.models;

import java.io.Serializable;

/**
 * Modelo de datos para una Tarea
 * Serializable para poder pasar entre Activities/Fragments
 */
public class Tarea implements Serializable {

    private String id;
    private String titulo;
    private String descripcion;
    private long fechaLimite; // Timestamp en milisegundos
    private boolean estado; // false = pendiente, true = completada
    private String userId; // ID del usuario propietario

    /**
     * Constructor vacío requerido por Firebase
     * Firebase necesita este constructor para deserializar los datos
     */
    public Tarea() {
    }

    /**
     * Constructor con parámetros
     */
    public Tarea(String titulo, String descripcion, long fechaLimite, boolean estado, String userId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
        this.userId = userId;
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(long fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaLimite=" + fechaLimite +
                ", estado=" + estado +
                ", userId='" + userId + '\'' +
                '}';
    }
}