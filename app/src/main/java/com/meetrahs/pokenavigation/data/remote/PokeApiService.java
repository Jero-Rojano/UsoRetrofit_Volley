package com.meetrahs.pokenavigation.data.remote;

import com.meetrahs.pokenavigation.data.model.PokemonDetail;
import com.meetrahs.pokenavigation.data.model.PokemonResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Aquí se DECLARAN (no se programan) las peticiones que la app hace a la API.
 * Retrofit lee estas anotaciones y genera por nosotros el código que hace la petición HTTP.
 *
 * Importante: las rutas NO empiezan con "/" porque se pegan a la BASE_URL
 * ("https://pokeapi.co/api/v2/") definida en RetrofitClient.
 */
public interface PokeApiService {

    /**
     * Lista de Pokémon.
     * Petición real: GET https://pokeapi.co/api/v2/pokemon?limit=30&offset=0
     * Cada anotación Query agrega un parámetro después del "?" de la URL.
     */
    @GET("pokemon")
    Call<PokemonResponse> getPokemon(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * ACTIVIDAD PROPUESTA: detalle de un Pokémon.
     * Petición real: GET https://pokeapi.co/api/v2/pokemon/pikachu
     * La anotación Path reemplaza {name} por el valor que le pasemos.
     */
    @GET("pokemon/{name}")
    Call<PokemonDetail> getPokemonDetail(@Path("name") String name);
}
