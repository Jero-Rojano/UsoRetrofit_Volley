package com.meetrahs.pokenavigation.util;

import androidx.annotation.ColorRes;

import com.meetrahs.pokenavigation.R;

/**
 * La API envía los tipos en inglés ("fire", "water"...).
 * Esta clase los traduce al español y les asigna un color
 * (los colores están en res/values/colors_tipos.xml).
 */
public final class TiposPokemon {

    private TiposPokemon() {
        // Clase de utilidades: no se crean objetos de ella
    }

    public static String traducir(String tipo) {
        if (tipo == null) {
            return "";
        }
        switch (tipo) {
            case "normal": return "Normal";
            case "fire": return "Fuego";
            case "water": return "Agua";
            case "electric": return "Eléctrico";
            case "grass": return "Planta";
            case "ice": return "Hielo";
            case "fighting": return "Lucha";
            case "poison": return "Veneno";
            case "ground": return "Tierra";
            case "flying": return "Volador";
            case "psychic": return "Psíquico";
            case "bug": return "Bicho";
            case "rock": return "Roca";
            case "ghost": return "Fantasma";
            case "dragon": return "Dragón";
            case "dark": return "Siniestro";
            case "steel": return "Acero";
            case "fairy": return "Hada";
            default: return Formato.capitalizar(tipo);
        }
    }

    @ColorRes
    public static int obtenerColor(String tipo) {
        if (tipo == null) {
            return R.color.tipo_normal;
        }
        switch (tipo) {
            case "fire": return R.color.tipo_fuego;
            case "water": return R.color.tipo_agua;
            case "electric": return R.color.tipo_electrico;
            case "grass": return R.color.tipo_planta;
            case "ice": return R.color.tipo_hielo;
            case "fighting": return R.color.tipo_lucha;
            case "poison": return R.color.tipo_veneno;
            case "ground": return R.color.tipo_tierra;
            case "flying": return R.color.tipo_volador;
            case "psychic": return R.color.tipo_psiquico;
            case "bug": return R.color.tipo_bicho;
            case "rock": return R.color.tipo_roca;
            case "ghost": return R.color.tipo_fantasma;
            case "dragon": return R.color.tipo_dragon;
            case "dark": return R.color.tipo_siniestro;
            case "steel": return R.color.tipo_acero;
            case "fairy": return R.color.tipo_hada;
            default: return R.color.tipo_normal;
        }
    }
}
