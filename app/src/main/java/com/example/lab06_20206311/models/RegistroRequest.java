package com.example.lab06_20206311.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para la solicitud de registro al microservicio
 */
public class RegistroRequest {
    
    @SerializedName("dni")
    private String dni;
    
    @SerializedName("correo")
    private String correo;

    public RegistroRequest(String dni, String correo) {
        this.dni = dni;
        this.correo = correo;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
