package com.meetrahs.pokenavigation.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Crea UNA sola instancia de Retrofit para toda la app (patrón Singleton)
 * y entrega el servicio PokeApiService listo para usar.
 */
public class RetrofitClient {

    // URL principal de la API. Debe terminar en "/"
    public static final String BASE_URL = "https://pokeapi.co/api/v2/";

    // Instancia única de Retrofit
    private static Retrofit retrofit;

    // Constructor privado para evitar crear objetos de esta clase
    private RetrofitClient() {
    }

    // Método para obtener el servicio de la API
    public static PokeApiService getService() {
        // Retrofit se crea solamente la primera vez
        if (retrofit == null) {
            // Permite ver en Logcat las peticiones HTTP (busca "pokeapi.co" en Logcat)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // BASIC muestra: método, URL, código de respuesta y tiempo
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            // Cliente HTTP (OkHttp) encargado de enviar las peticiones por internet
            OkHttpClient client = new OkHttpClient.Builder()
                    // Se agrega el interceptor de logs
                    .addInterceptor(logging)
                    .build();

            // Configuración de Retrofit
            retrofit = new Retrofit.Builder()
                    // URL base de la API
                    .baseUrl(BASE_URL)
                    // Se utiliza el cliente OkHttp configurado
                    .client(client)
                    // Convierte el JSON de la API en objetos Java (y al revés)
                    .addConverterFactory(GsonConverterFactory.create())
                    // Construye Retrofit
                    .build();
        }
        // Retrofit implementa automáticamente la interfaz PokeApiService
        return retrofit.create(PokeApiService.class);
    }
}
