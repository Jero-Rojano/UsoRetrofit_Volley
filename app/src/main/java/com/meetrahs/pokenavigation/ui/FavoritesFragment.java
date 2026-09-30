package com.meetrahs.pokenavigation.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.meetrahs.pokenavigation.MainActivity;
import com.meetrahs.pokenavigation.R;
import com.meetrahs.pokenavigation.data.model.Pokemon;
import com.meetrahs.pokenavigation.data.repository.FavoritesRepository;
import com.meetrahs.pokenavigation.ui.adapter.PokemonAdapter;

/**
 * Pantalla "Favoritos" (ACTIVIDAD PROPUESTA): muestra los Pokémon guardados
 * en el teléfono. Reutiliza el mismo PokemonAdapter de la pantalla Inicio.
 */
public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerFavoritos;
    private TextView tvVacio;

    private PokemonAdapter adapter;
    private FavoritesRepository favoritesRepository;

    public FavoritesFragment() {
        super(R.layout.fragment_favorites);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        recyclerFavoritos = view.findViewById(R.id.recyclerFavoritos);
        tvVacio = view.findViewById(R.id.tvVacio);

        favoritesRepository = new FavoritesRepository(requireContext());
        adapter = new PokemonAdapter(this::abrirDetalle);

        recyclerFavoritos.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerFavoritos.setHasFixedSize(true);
        recyclerFavoritos.setAdapter(adapter);

        // Se lee cada vez que se crea la vista: así, al volver del detalle,
        // la lista ya refleja si agregaste o quitaste un favorito.
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        List<Pokemon> favoritos = favoritesRepository.obtenerFavoritos();
        adapter.actualizarDatos(favoritos);

        if (favoritos.isEmpty()) {
            tvVacio.setVisibility(View.VISIBLE);
            recyclerFavoritos.setVisibility(View.GONE);
        } else {
            tvVacio.setVisibility(View.GONE);
            recyclerFavoritos.setVisibility(View.VISIBLE);
        }
    }

    private void abrirDetalle(Pokemon pokemon) {
        ((MainActivity) requireActivity()).abrirDetalle(pokemon.getName());
    }

    @Override
    public void onDestroyView() {
        recyclerFavoritos.setAdapter(null);
        super.onDestroyView();
    }
}
