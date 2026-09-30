package com.meetrahs.pokenavigation.data.model;

import java.util.List;

/**
 * La PokéAPI NO devuelve directamente una lista. Devuelve un OBJETO así:
 *
 *   {
 *     "count": (total de Pokémon en la API),
 *     "next": "https://pokeapi.co/api/v2/pokemon?offset=30&limit=30",
 *     "previous": null,
 *     "results": [ { "name": "bulbasaur", "url": "..." }, ... ]
 *   }
 *
 * Por eso necesitamos esta clase "contenedora": la lista está dentro de "results".
 * Si intentáramos recibir List<Pokemon> directamente, Gson lanzaría el error
 * "Expected BEGIN_ARRAY but was BEGIN_OBJECT".
 */
public class PokemonResponse {

    private int count;
    private String next;
    private String previous;
    private List<Pokemon> results;

    public int getCount() {
        return count;
    }

    public String getNext() {
        return next;
    }

    public String getPrevious() {
        return previous;
    }

    public List<Pokemon> getResults() {
        return results;
    }
}
