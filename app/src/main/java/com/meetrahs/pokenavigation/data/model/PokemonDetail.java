package com.meetrahs.pokenavigation.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * ACTIVIDAD PROPUESTA
 * Respuesta del endpoint pokemon/{name}. Solo declaramos los campos que usamos;
 * Gson ignora el resto del JSON (que es muy grande).
 *
 * Forma resumida del JSON:
 *   {
 *     "id": 25,
 *     "name": "pikachu",
 *     "base_experience": 112,
 *     "height": 4,        (en DECÍMETROS)
 *     "weight": 60,       (en HECTOGRAMOS)
 *     "sprites": {
 *       "front_default": "https://.../pokemon/25.png",
 *       "other": {
 *         "official-artwork": { "front_default": "https://.../official-artwork/25.png" }
 *       }
 *     },
 *     "types": [ { "slot": 1, "type": { "name": "electric", "url": "..." } } ]
 *   }
 *
 * Cada objeto anidado del JSON ({ ... }) se representa con una clase interna.
 */
public class PokemonDetail {

    private int id;
    private String name;
    private int height;
    private int weight;

    // @SerializedName: el nombre en el JSON es distinto al nombre de nuestro atributo.
    // Es Integer (y no int) porque en algunos Pokémon este dato llega como null.
    @SerializedName("base_experience")
    private Integer baseExperience;

    private Sprites sprites;
    private List<TypeSlot> types;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public Integer getBaseExperience() {
        return baseExperience;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public List<TypeSlot> getTypes() {
        return types;
    }

    /** La API da la altura en decímetros: 4 dm = 0,4 m */
    public double getAlturaEnMetros() {
        return height / 10.0;
    }

    /** La API da el peso en hectogramos: 60 hg = 6,0 kg */
    public double getPesoEnKilos() {
        return weight / 10.0;
    }

    /**
     * URL de la imagen oficial ("official-artwork").
     * Si ese Pokémon no la tiene, usa la imagen pequeña ("front_default").
     */
    public String getImagenOficial() {
        if (sprites == null) {
            return null;
        }
        Other other = sprites.getOther();
        if (other != null
                && other.getOfficialArtwork() != null
                && other.getOfficialArtwork().getFrontDefault() != null) {
            return other.getOfficialArtwork().getFrontDefault();
        }
        return sprites.getFrontDefault();
    }

    /** Convierte la lista de tipos del JSON en una lista sencilla, por ejemplo ["electric"]. */
    public List<String> getNombresDeTipos() {
        List<String> nombres = new ArrayList<>();
        if (types == null) {
            return nombres;
        }
        for (TypeSlot slot : types) {
            if (slot.getType() != null && slot.getType().getName() != null) {
                nombres.add(slot.getType().getName());
            }
        }
        return nombres;
    }

    // ------------- Clases internas: una por cada objeto anidado del JSON -------------

    /** "sprites": { ... } */
    public static class Sprites {

        @SerializedName("front_default")
        private String frontDefault;

        private Other other;

        public String getFrontDefault() {
            return frontDefault;
        }

        public Other getOther() {
            return other;
        }
    }

    /** "other": { "official-artwork": { ... } } */
    public static class Other {

        // En Java un nombre no puede llevar guion, por eso usamos @SerializedName
        @SerializedName("official-artwork")
        private OfficialArtwork officialArtwork;

        public OfficialArtwork getOfficialArtwork() {
            return officialArtwork;
        }
    }

    /** "official-artwork": { "front_default": "..." } */
    public static class OfficialArtwork {

        @SerializedName("front_default")
        private String frontDefault;

        public String getFrontDefault() {
            return frontDefault;
        }
    }

    /** Cada elemento de "types": { "slot": 1, "type": { ... } } */
    public static class TypeSlot {

        private int slot;
        private TypeInfo type;

        public int getSlot() {
            return slot;
        }

        public TypeInfo getType() {
            return type;
        }
    }

    /** "type": { "name": "electric", "url": "..." } */
    public static class TypeInfo {

        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
