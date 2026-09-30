package com.meetrahs.consumoapivolley;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String URL_API = "https://jsonplaceholder.typicode.com/todos";
    private static final String REQUEST_TAG = "GET_TODOS";

    private ListView lvTodos;
    private ProgressBar progressBar;
    private TextView tvEstado;
    private RequestQueue requestQueue;
    private final List<String> listaTodos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVistas();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        consumirApi();
    }

    private void inicializarVistas() {
        lvTodos = findViewById(R.id.lvTodos);
        progressBar = findViewById(R.id.progressBar);
        tvEstado = findViewById(R.id.tvEstado);
    }

    private void mostrarCargando(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        lvTodos.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }

    private void mostrarError(String mensaje) {
        mostrarCargando(false);
        tvEstado.setText(mensaje);
        tvEstado.setVisibility(View.VISIBLE);
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void procesarError(VolleyError error) {
        String mensaje = error.getMessage();

        if (mensaje == null || mensaje.trim().isEmpty()) {
            mensaje = "Verifique la conexión a internet.";
        }

        mostrarError("Error en la solicitud: " + mensaje);
    }

    private void consumirApi() {
        mostrarCargando(true);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    listaTodos.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);

                            int id = item.getInt("id");
                            String titulo = item.getString("title");
                            boolean completado = item.getBoolean("completed");

                            String texto = "ID: " + id
                                    + "\nTítulo: " + titulo
                                    + "\nCompletado: "
                                    + (completado ? "Sí" : "No");

                            listaTodos.add(texto);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_list_item_1,
                                listaTodos
                        );

                        lvTodos.setAdapter(adapter);
                        mostrarCargando(false);
                        tvEstado.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        mostrarError("No fue posible procesar la respuesta.");
                    }
                },
                this::procesarError
        );

        request.setTag(REQUEST_TAG);
        requestQueue.add(request);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(REQUEST_TAG);
        }
    }
}