package com.meetrahs.pokenavigation.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.meetrahs.pokenavigation.R;
import com.meetrahs.pokenavigation.data.model.Pokemon;
import com.meetrahs.pokenavigation.util.Formato;

/**
 * El Adapter es el "puente" entre la lista de datos y el RecyclerView:
 * crea las tarjetas (item_pokemon.xml) y les pone los datos de cada Pokémon.
 * Se usa en Inicio y también en Favoritos.
 */
public class PokemonAdapter
        extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    // "Contrato" para avisarle al Fragment qué tarjeta se tocó
    public interface OnPokemonClickListener {
        void onPokemonClick(Pokemon pokemon);
    }

    private final List<Pokemon> pokemonList = new ArrayList<>();
    private final OnPokemonClickListener listener;

    public PokemonAdapter(OnPokemonClickListener listener) {
        this.listener = listener;
    }

    /** Reemplaza los datos que se muestran y redibuja la lista. */
    public void actualizarDatos(List<Pokemon> nuevosPokemon) {
        pokemonList.clear();

        if (nuevosPokemon != null) {
            pokemonList.addAll(nuevosPokemon);
        }

        notifyDataSetChanged();
    }

    // 1) Crea una tarjeta vacía. Se llama pocas veces: luego las tarjetas se reciclan.
    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);

        return new PokemonViewHolder(view);
    }

    // 2) Llena una tarjeta con los datos del Pokémon de esa posición
    @Override
    public void onBindViewHolder(
            @NonNull PokemonViewHolder holder,
            int position
    ) {
        Pokemon pokemon = pokemonList.get(position);
        holder.bind(pokemon);
    }

    // 3) Le dice al RecyclerView cuántos elementos hay
    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    /**
     * El ViewHolder guarda las referencias a las vistas de UNA tarjeta,
     * así no hay que buscarlas con findViewById cada vez que se recicla.
     */
    class PokemonViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPokemon;
        private final TextView tvPokemonName;
        private final TextView tvPokemonUrl;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPokemon = itemView.findViewById(R.id.ivPokemon);
            tvPokemonName = itemView.findViewById(R.id.tvPokemonName);
            tvPokemonUrl = itemView.findViewById(R.id.tvPokemonUrl);
        }

        public void bind(Pokemon pokemon) {
            tvPokemonName.setText(Formato.capitalizar(pokemon.getName()));
            tvPokemonUrl.setText(pokemon.getUrl());

            // Glide descarga la imagen desde internet y la coloca en el ImageView
            Glide.with(itemView)
                    .load(pokemon.getImagenUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(ivPokemon);

            // El clic se asocia al OBJETO Pokemon y no a una posición guardada
            itemView.setOnClickListener(
                    view -> listener.onPokemonClick(pokemon)
            );
        }
    }
}
