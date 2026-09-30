package com.meetrahs.pokenavigation.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

import com.meetrahs.pokenavigation.R;
import com.meetrahs.pokenavigation.data.model.Pokemon;
import com.meetrahs.pokenavigation.data.model.PokemonDetail;
import com.meetrahs.pokenavigation.data.remote.RetrofitClient;
import com.meetrahs.pokenavigation.data.repository.FavoritesRepository;
import com.meetrahs.pokenavigation.data.repository.PokemonRepository;
import com.meetrahs.pokenavigation.util.Formato;
import com.meetrahs.pokenavigation.util.TiposPokemon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ACTIVIDAD PROPUESTA: pantalla de detalle de un Pokémon.
 * Consume el endpoint pokemon/{name} y muestra la imagen oficial, altura, peso,
 * experiencia base y tipos. El botón inferior lo guarda en Favoritos o lo quita.
 */
public class DetailFragment extends Fragment {

    // Clave con la que viaja el nombre del Pokémon dentro de los argumentos del Fragment
    private static final String ARG_NOMBRE = "nombre_pokemon";

    private MaterialToolbar toolbar;
    private NestedScrollView scrollContenido;
    private ImageView ivImagenOficial;
    private TextView tvNumero;
    private TextView tvNombre;
    private ChipGroup chipGroupTipos;
    private TextView tvAltura;
    private TextView tvPeso;
    private TextView tvExperiencia;
    private MaterialButton btnFavorito;
    private CircularProgressIndicator progressIndicator;
    private LinearLayout errorContainer;
    private TextView tvError;
    private MaterialButton btnRetry;

    private PokemonRepository pokemonRepository;
    private FavoritesRepository favoritesRepository;
    private Call<PokemonDetail> currentCall;

    private String nombrePokemon;
    private PokemonDetail detalleActual;

    public DetailFragment() {
        super(R.layout.fragment_detail);
    }

    /**
     * Forma recomendada de crear el Fragment enviándole datos: se guardan en un
     * Bundle (los "argumentos"), que Android conserva aunque se gire la pantalla.
     */
    public static DetailFragment newInstance(String nombrePokemon) {
        DetailFragment fragment = new DetailFragment();
        Bundle argumentos = new Bundle();
        argumentos.putString(ARG_NOMBRE, nombrePokemon);
        fragment.setArguments(argumentos);
        return fragment;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        nombrePokemon = requireArguments().getString(ARG_NOMBRE);

        initObjects(view);

        pokemonRepository = new PokemonRepository();
        favoritesRepository = new FavoritesRepository(requireContext());

        // Flecha de la barra superior: saca este Fragment de la pila y vuelve atrás
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnRetry.setOnClickListener(v -> cargarDetalle());
        btnFavorito.setOnClickListener(v -> cambiarEstadoFavorito());

        cargarDetalle();
    }

    private void initObjects(View view) {
        toolbar = view.findViewById(R.id.toolbarDetalle);
        scrollContenido = view.findViewById(R.id.scrollContenido);
        ivImagenOficial = view.findViewById(R.id.ivImagenOficial);
        tvNumero = view.findViewById(R.id.tvNumero);
        tvNombre = view.findViewById(R.id.tvNombre);
        chipGroupTipos = view.findViewById(R.id.chipGroupTipos);
        tvAltura = view.findViewById(R.id.tvAltura);
        tvPeso = view.findViewById(R.id.tvPeso);
        tvExperiencia = view.findViewById(R.id.tvExperiencia);
        btnFavorito = view.findViewById(R.id.btnFavorito);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        errorContainer = view.findViewById(R.id.errorContainer);
        tvError = view.findViewById(R.id.tvError);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    /** Pide a la API el detalle: GET pokemon/{name} */
    private void cargarDetalle() {
        mostrarCargando();

        currentCall = pokemonRepository.obtenerDetalle(nombrePokemon);

        currentCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    @NonNull Call<PokemonDetail> call,
                    @NonNull Response<PokemonDetail> response
            ) {
                if (!isAdded()) {
                    return;
                }

                PokemonDetail body = response.body();

                if (response.isSuccessful() && body != null) {
                    detalleActual = body;
                    mostrarDetalle(body);
                    mostrarContenido();
                } else {
                    mostrarError(getString(R.string.error_detalle_http, response.code()));
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<PokemonDetail> call,
                    @NonNull Throwable throwable
            ) {
                if (call.isCanceled() || !isAdded()) {
                    return;
                }

                mostrarError(getString(R.string.error_conexion));
            }
        });
    }

    /** Pone en pantalla los datos que llegaron de la API. */
    private void mostrarDetalle(PokemonDetail detalle) {
        tvNumero.setText(getString(R.string.formato_numero, detalle.getId()));
        tvNombre.setText(Formato.capitalizar(detalle.getName()));

        // La API usa decímetros y hectogramos; aquí ya llegan convertidos a m y kg
        tvAltura.setText(getString(R.string.formato_altura, detalle.getAlturaEnMetros()));
        tvPeso.setText(getString(R.string.formato_peso, detalle.getPesoEnKilos()));

        Integer experiencia = detalle.getBaseExperience();
        if (experiencia != null) {
            tvExperiencia.setText(getString(R.string.formato_experiencia, experiencia));
        } else {
            tvExperiencia.setText(R.string.sin_dato);
        }

        // Imagen oficial con Glide (la descarga y la muestra)
        Glide.with(this)
                .load(detalle.getImagenOficial())
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(ivImagenOficial);

        List<String> tipos = detalle.getNombresDeTipos();
        mostrarTipos(tipos);
        pintarCirculoDeFondo(tipos);

        actualizarBotonFavorito();
    }

    /** Crea un Chip (etiqueta de color) por cada tipo del Pokémon. */
    private void mostrarTipos(List<String> tipos) {
        chipGroupTipos.removeAllViews();

        for (String tipo : tipos) {
            int color = ContextCompat.getColor(requireContext(), TiposPokemon.obtenerColor(tipo));

            Chip chip = new Chip(requireContext());
            chip.setText(TiposPokemon.traducir(tipo));
            chip.setTextColor(Color.WHITE);
            chip.setChipBackgroundColor(ColorStateList.valueOf(color));
            chip.setChipStrokeWidth(0f);
            chip.setCheckable(false);
            chip.setClickable(false);

            chipGroupTipos.addView(chip);
        }
    }

    /** Pinta el círculo detrás de la imagen con el color suave del tipo principal. */
    private void pintarCirculoDeFondo(List<String> tipos) {
        if (tipos.isEmpty()) {
            return;
        }

        int color = ContextCompat.getColor(requireContext(), TiposPokemon.obtenerColor(tipos.get(0)));
        int colorSuave = ColorUtils.setAlphaComponent(color, 60); // 60 de 255: bastante transparente

        GradientDrawable circulo = new GradientDrawable();
        circulo.setShape(GradientDrawable.OVAL);
        circulo.setColor(colorSuave);
        ivImagenOficial.setBackground(circulo);
    }

    /** Guarda o quita el Pokémon de Favoritos, según su estado actual. */
    private void cambiarEstadoFavorito() {
        if (detalleActual == null) {
            return;
        }

        String nombreVisible = Formato.capitalizar(nombrePokemon);

        if (favoritesRepository.esFavorito(nombrePokemon)) {
            favoritesRepository.eliminarFavorito(nombrePokemon);
            mostrarMensaje(getString(R.string.favorito_eliminado, nombreVisible));
        } else {
            // Se guarda con la misma forma que tienen los Pokémon de la lista (name + url)
            String url = RetrofitClient.BASE_URL + "pokemon/" + detalleActual.getId() + "/";
            favoritesRepository.agregarFavorito(new Pokemon(nombrePokemon, url));
            mostrarMensaje(getString(R.string.favorito_agregado, nombreVisible));
        }

        actualizarBotonFavorito();
    }

    /** Cambia el texto y el ícono del botón según si ya es favorito o no. */
    private void actualizarBotonFavorito() {
        if (favoritesRepository.esFavorito(nombrePokemon)) {
            btnFavorito.setText(R.string.quitar_favorito);
            btnFavorito.setIconResource(R.drawable.baseline_favorite_24);
        } else {
            btnFavorito.setText(R.string.guardar_favorito);
            btnFavorito.setIconResource(R.drawable.ic_favorite_border_24);
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    // ----- Los tres estados de la pantalla: cargando, contenido y error -----

    private void mostrarCargando() {
        progressIndicator.setVisibility(View.VISIBLE);
        scrollContenido.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
    }

    private void mostrarContenido() {
        progressIndicator.setVisibility(View.GONE);
        scrollContenido.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
    }

    private void mostrarError(String mensaje) {
        progressIndicator.setVisibility(View.GONE);
        scrollContenido.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        tvError.setText(mensaje);
    }

    @Override
    public void onDestroyView() {
        if (currentCall != null) {
            currentCall.cancel();
        }
        super.onDestroyView();
    }
}
