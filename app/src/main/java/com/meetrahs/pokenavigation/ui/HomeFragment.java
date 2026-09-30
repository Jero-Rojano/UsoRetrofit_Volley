package com.meetrahs.pokenavigation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

import com.meetrahs.pokenavigation.MainActivity;
import com.meetrahs.pokenavigation.R;
import com.meetrahs.pokenavigation.data.model.Pokemon;
import com.meetrahs.pokenavigation.data.model.PokemonResponse;
import com.meetrahs.pokenavigation.data.repository.PokemonRepository;
import com.meetrahs.pokenavigation.ui.adapter.PokemonAdapter;
import com.meetrahs.pokenavigation.util.Formato;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla "Inicio": pide 30 Pokémon a la API con Retrofit y los muestra en un RecyclerView.
 */
public class HomeFragment extends Fragment {

    private static final int CANTIDAD_POKEMON = 30;

    private RecyclerView recyclerPokemon;
    private CircularProgressIndicator progressIndicator;
    private LinearLayout errorContainer;
    private TextView tvError;
    private MaterialButton btnRetry;

    private PokemonAdapter adapter;
    private PokemonRepository repository;
    private Call<PokemonResponse> currentCall;

    // Lista ya descargada: al volver del detalle se muestra sin llamar otra vez a la API
    private List<Pokemon> pokemonDescargados;

    public HomeFragment() {
        // Se indica qué layout usa este Fragment
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        recyclerPokemon = view.findViewById(R.id.recyclerPokemon);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        errorContainer = view.findViewById(R.id.errorContainer);
        tvError = view.findViewById(R.id.tvError);
        btnRetry = view.findViewById(R.id.btnRetry);

        adapter = new PokemonAdapter(this::mostrarPokemonSeleccionado);
        repository = new PokemonRepository();

        recyclerPokemon.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        recyclerPokemon.setHasFixedSize(true);
        recyclerPokemon.setAdapter(adapter);

        btnRetry.setOnClickListener(v -> cargarPokemon());

        if (pokemonDescargados != null) {
            adapter.actualizarDatos(pokemonDescargados);
            mostrarContenido();
        } else {
            cargarPokemon();
        }
    }

    private void cargarPokemon() {
        mostrarCargando();

        // 1. Se prepara la llamada (todavía no se ha enviado nada por internet)
        currentCall = repository.obtenerPokemon(CANTIDAD_POKEMON, 0);

        // 2. enqueue() la envía en segundo plano; la respuesta llega a onResponse u onFailure
        currentCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull Call<PokemonResponse> call,
                    @NonNull Response<PokemonResponse> response
            ) {
                // Si el usuario ya salió de esta pantalla, no se tocan las vistas
                if (!isAdded()) {
                    return;
                }

                PokemonResponse body = response.body();

                if (response.isSuccessful()
                        && body != null
                        && body.getResults() != null) {

                    pokemonDescargados = body.getResults();
                    adapter.actualizarDatos(pokemonDescargados);
                    mostrarContenido();
                } else {
                    // El servidor respondió, pero con un código de error (404, 500...)
                    mostrarError(getString(R.string.error_http, response.code()));
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<PokemonResponse> call,
                    @NonNull Throwable throwable
            ) {
                // Si la cancelamos nosotros (en onDestroyView) no es un error real
                if (call.isCanceled() || !isAdded()) {
                    return;
                }

                // No hubo respuesta: sin internet, servidor caído, etc.
                mostrarError(getString(R.string.error_conexion));
            }
        });
    }

    /** Se ejecuta al tocar una tarjeta: muestra el Toast y abre el detalle. */
    private void mostrarPokemonSeleccionado(Pokemon pokemon) {
        Toast.makeText(
                requireContext(),
                getString(R.string.seleccion_pokemon, Formato.capitalizar(pokemon.getName())),
                Toast.LENGTH_SHORT
        ).show();

        // ACTIVIDAD PROPUESTA: abrir el Fragment de detalle
        ((MainActivity) requireActivity()).abrirDetalle(pokemon.getName());
    }

    // ----- Los tres estados de la pantalla: cargando, contenido y error -----

    private void mostrarCargando() {
        progressIndicator.setVisibility(View.VISIBLE);
        recyclerPokemon.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
    }

    private void mostrarContenido() {
        progressIndicator.setVisibility(View.GONE);
        recyclerPokemon.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
    }

    private void mostrarError(String mensaje) {
        progressIndicator.setVisibility(View.GONE);
        recyclerPokemon.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        tvError.setText(mensaje);
    }

    @Override
    public void onDestroyView() {
        // Se cancela la petición para que su respuesta no modifique vistas que ya no existen
        if (currentCall != null) {
            currentCall.cancel();
        }

        recyclerPokemon.setAdapter(null);
        super.onDestroyView();
    }
}
