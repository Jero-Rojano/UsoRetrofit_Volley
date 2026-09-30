package com.meetrahs.pokenavigation.data.model;

/**
 * Representa un Pokémon tal como llega en la LISTA de la PokéAPI.
 *
 * Ejemplo del JSON que envía la API:
 *   { "name": "pikachu", "url": "https://pokeapi.co/api/v2/pokemon/25/" }
 *
 * Gson (el convertidor que usa Retrofit) copia cada campo del JSON en el
 * atributo que tiene el MISMO nombre: name -> name, url -> url.
 */
public class Pokemon {

    private String name;
    private String url;

    // Constructor vacío: lo usa Gson para crear el objeto antes de llenarlo.
    public Pokemon() {
    }

    // Constructor completo: lo usamos nosotros al guardar un favorito.
    public Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Obtiene el número (id) del Pokémon a partir de su URL.
     * "https://pokeapi.co/api/v2/pokemon/25/"  ->  25
     */
    public int getId() {
        if (url == null) {
            return 0;
        }
        String[] partes = url.split("/");
        if (partes.length == 0) {
            return 0;
        }
        try {
            return Integer.parseInt(partes[partes.length - 1]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * URL de la imagen pequeña (sprite) del Pokémon, armada con su id.
     * Se usa para mostrar una miniatura en las tarjetas de la lista.
     */
    public String getImagenUrl() {
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
                + getId() + ".png";
    }
}
