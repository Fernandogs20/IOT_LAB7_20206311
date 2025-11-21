package com.example.lab06_20206311.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente de Retrofit configurado
 * Define la URL base del servidor
 */
public class ApiClient {

    // ⚠️ CAMBIAR ESTA IP POR LA IP DE TU COMPUTADORA
    private static final String BASE_URL = "http://192.168.18.173:8080/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Método para cambiar la URL base en tiempo de ejecución (útil para testing)
     */
    public static void setBaseUrl(String newBaseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(newBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
