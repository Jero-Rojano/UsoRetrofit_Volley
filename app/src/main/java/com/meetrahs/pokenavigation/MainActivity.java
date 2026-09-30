package com.meetrahs.pokenavigation;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.meetrahs.pokenavigation.ui.DetailFragment;
import com.meetrahs.pokenavigation.ui.FavoritesFragment;
import com.meetrahs.pokenavigation.ui.HomeFragment;
import com.meetrahs.pokenavigation.ui.InfoFragment;

/**
 * Única Activity de la app.
 * Contiene el FragmentContainerView (donde se muestran las pantallas)
 * y el BottomNavigationView (menú inferior).
 */
public class MainActivity extends AppCompatActivity {

    // 11.1 Declaración de atributos
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ajustarBordesDelSistema();

        initObjects();
        configurarBottomNavigation();

        // savedInstanceState es null solo la primera vez que se abre la pantalla
        // (al girar el teléfono Android restaura solo el Fragment que estaba visible).
        if (savedInstanceState == null) {
            cargarFragment(new HomeFragment());
        }
    }

    // 11.2 Enlace de los objetos Java con la vista (XML)
    private void initObjects() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    /**
     * Desde Android 15 las apps se dibujan "de borde a borde".
     * Este método agrega un margen interno para que el contenido no quede
     * debajo de la barra de estado ni de la barra de navegación del teléfono.
     */
    private void ajustarBordesDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // 11.3 Recibe el id del ítem tocado en el menú y devuelve el Fragment que corresponde
    private Fragment obtenerFragment(int itemId) {

        if (itemId == R.id.navigation_home) {
            return new HomeFragment();
        }

        if (itemId == R.id.navigation_favorites) {
            return new FavoritesFragment();
        }

        if (itemId == R.id.navigation_info) {
            return new InfoFragment();
        }

        return null;
    }

    // 11.4 Método de carga del Fragment dentro del contenedor
    private void cargarFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Si había un detalle abierto, se cierra antes de cambiar de sección
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // 11.5 Configuración del menú inferior: decide qué Fragment cargar
    private void configurarBottomNavigation() {

        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = obtenerFragment(item.getItemId());

            if (fragment == null) {
                return false;
            }

            cargarFragment(fragment);

            return true;
        });

        // Tocar otra vez la sección actual (por ejemplo "Inicio" mientras se ve
        // un detalle) regresa a la lista de esa sección.
        bottomNavigation.setOnItemReselectedListener(item ->
                getSupportFragmentManager()
                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE));
    }

    /**
     * ACTIVIDAD PROPUESTA: abre el detalle de un Pokémon.
     * addToBackStack() lo guarda en la "pila de atrás": el botón Atrás del teléfono
     * (o la flecha de la barra superior) regresa a la pantalla anterior.
     */
    public void abrirDetalle(String nombrePokemon) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, DetailFragment.newInstance(nombrePokemon))
                .addToBackStack(null)
                .commit();
    }
}
