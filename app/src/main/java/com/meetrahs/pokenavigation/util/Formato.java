package com.meetrahs.pokenavigation.util;

import java.util.Locale;

/**
 * Ayudas pequeñas para mostrar textos en pantalla.
 */
public final class Formato {

    private Formato() {
        // Clase de utilidades: no se crean objetos de ella
    }

    /** "pikachu"  ->  "Pikachu" */
    public static String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        return texto.substring(0, 1).toUpperCase(Locale.ROOT) + texto.substring(1);
    }
}
