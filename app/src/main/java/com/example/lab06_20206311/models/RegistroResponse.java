package com.example.lab06_20206311.models;

import com.google.gson.annotations.SerializedName;

/**
 * Modelo para la respuesta del microservicio de registro
 */
public class RegistroResponse {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("dni")
    private String dni;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("mensaje")
    private String mensaje;
    
    @SerializedName("error")
    private String error;
    
    @SerializedName("success")
    private boolean success;

    public RegistroResponse() {
    }

    public RegistroResponse(String dni, String correo, String mensaje) {
        this.dni = dni;
        this.correo = correo;
        this.mensaje = mensaje;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "RegistroResponse{" +
                "id='" + id + '\'' +
                ", dni='" + dni + '\'' +
                ", correo='" + correo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", error='" + error + '\'' +
                ", success=" + success +
                '}';
    }
}
