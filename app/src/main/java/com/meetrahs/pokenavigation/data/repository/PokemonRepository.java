package com.meetrahs.pokenavigation.data.repository;

import com.meetrahs.pokenavigation.data.model.PokemonDetail;
import com.meetrahs.pokenavigation.data.model.PokemonResponse;
import com.meetrahs.pokenavigation.data.remote.PokeApiService;
import com.meetrahs.pokenavigation.data.remote.RetrofitClient;
import retrofit2.Call;

/**
 * El repositorio separa la OBTENCIÓN de los datos de las PANTALLAS.
 * Los Fragments no saben de dónde salen los datos (internet, base de datos...):
 * solo se los piden al repositorio.
 */
public class PokemonRepository {

    private final PokeApiService service;

    public PokemonRepository() {
        service = RetrofitClient.getService();
    }

    // Lista de Pokémon (pantalla Inicio)
    public Call<PokemonResponse> obtenerPokemon(int limit, int offset) {
        return service.getPokemon(limit, offset);
    }

    // ACTIVIDAD PROPUESTA: detalle de un Pokémon a partir de su nombre
    public Call<PokemonDetail> obtenerDetalle(String nombre) {
        return service.getPokemonDetail(nombre);
    }
}
