package com.meetrahs.pokenavigation.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.meetrahs.pokenavigation.data.model.Pokemon;

/**
 * ACTIVIDAD PROPUESTA: guarda los Pokémon favoritos EN EL TELÉFONO.
 *
 * Usa SharedPreferences: un pequeño archivo de pares clave/valor que Android
 * conserva aunque cierres la app. Como SharedPreferences solo guarda textos y
 * números, la lista de favoritos se convierte a texto JSON con Gson al guardarla
 * y se convierte de nuevo en lista al leerla.
 */
public class FavoritesRepository {

    private static final String NOMBRE_ARCHIVO = "pokenavigation_favoritos";
    private static final String CLAVE_FAVORITOS = "lista_favoritos";

    private final SharedPreferences preferencias;
    private final Gson gson = new Gson();

    public FavoritesRepository(Context context) {
        preferencias = context.getApplicationContext()
                .getSharedPreferences(NOMBRE_ARCHIVO, Context.MODE_PRIVATE);
    }

    /** Devuelve los favoritos guardados (una lista vacía si todavía no hay ninguno). */
    public List<Pokemon> obtenerFavoritos() {
        String json = preferencias.getString(CLAVE_FAVORITOS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            Pokemon[] arreglo = gson.fromJson(json, Pokemon[].class);
            if (arreglo == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(arreglo));
        } catch (JsonSyntaxException e) {
            // Si el texto guardado estuviera dañado, empezamos con la lista vacía
            return new ArrayList<>();
        }
    }

    /** true si el Pokémon con ese nombre ya está guardado. */
    public boolean esFavorito(String nombre) {
        for (Pokemon pokemon : obtenerFavoritos()) {
            if (Objects.equals(pokemon.getName(), nombre)) {
                return true;
            }
        }
        return false;
    }

    /** Agrega un Pokémon a favoritos (si no estaba ya). */
    public void agregarFavorito(Pokemon pokemon) {
        if (esFavorito(pokemon.getName())) {
            return;
        }
        List<Pokemon> favoritos = obtenerFavoritos();
        favoritos.add(pokemon);
        guardar(favoritos);
    }

    /** Quita de favoritos el Pokémon con ese nombre. */
    public void eliminarFavorito(String nombre) {
        List<Pokemon> favoritos = obtenerFavoritos();
        for (int i = 0; i < favoritos.size(); i++) {
            if (Objects.equals(favoritos.get(i).getName(), nombre)) {
                favoritos.remove(i);
                break;
            }
        }
        guardar(favoritos);
    }

    /** Convierte la lista a JSON y la escribe en SharedPreferences. */
    private void guardar(List<Pokemon> favoritos) {
        String json = gson.toJson(favoritos);
        preferencias.edit()
                .putString(CLAVE_FAVORITOS, json)
                .apply(); // apply() guarda en segundo plano, sin congelar la pantalla
    }
}
