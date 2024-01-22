package com.example.micontrol;

import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.micontrol.db.DbLogo;
import com.example.micontrol.entidades.Logo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private List<ColorModel> colorList;
    private static final int REQUEST_CONFIGURACION = 1;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyToolbarColor(toolbar, AppPreferences.getSelectedColor(this));

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //Para el logo y Nombre
        View headerView = navigationView.getHeaderView(0);
        CircularImageView navImagen = headerView.findViewById(R.id.nav_imagen);
        TextView navNombre = headerView.findViewById(R.id.nav_nombre);

        // Recuperar el logo de la base de datos usando DbLogo
        DbLogo dbLogo = new DbLogo(this);
        Logo logo = dbLogo.getLogo(1); // ID del logo que deseas obtener, en este caso, 1

        // Establecer la imagen y el nombre en el header
        if (logo != null) {
            navImagen.setImageBitmap(logo.getImagenservicio());
            navNombre.setText(logo.getNombre());
        }
        //Aqui acaba lo de logo y Nombre

        EventBus.getDefault().register(this);
        //setupLogoAndName();



        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.drawer_layout), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyNavigationColor(navigationView, AppPreferences.getSelectedColor(this));



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.homee);

            // Cargar el fragmento según la información adicional enviada por EditarCitaActivity
            String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
            if (fragmentToLoad != null) {
                switch (fragmentToLoad) {
                    case "citas":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CitasFragment()).commit();
                        break;
                    case "servicios":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ServiciosFragment()).commit();
                        break;
                    case "talleres":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TalleresFragment()).commit();
                        break;
                    case "inventario":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InventarioFragment()).commit();
                        break;
                    case "ventas":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VentasFragment()).commit();
                        break;
                    case "compras":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ComprasFragment()).commit();
                        break;
                    case "ingresos":
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IngresosFragment()).commit();
                        break;
                }
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.homee:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;

            case R.id.servicios:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ServiciosFragment()).commit();
                break;

            case R.id.citas:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CitasFragment()).commit();
                break;

            case R.id.talleres:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TalleresFragment()).commit();
                break;

            case R.id.inventario:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new InventarioFragment()).commit();
                break;

            case R.id.ventas:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VentasFragment()).commit();
                break;

            case R.id.compras:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ComprasFragment()).commit();
                break;

            case R.id.ingresos:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new IngresosFragment()).commit();
                break;

            case R.id.configuracion:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ConfiguracionFragment()).commit();
                break;

            case R.id.about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AcercaDeFragment()).commit();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private int getStartColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getStartColorRes();
            }
        }
        return R.color.colorUno;
    }

    private int getCenterColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getCenterColorRes();
            }
        }
        return R.color.colorDos;
    }

    private int getEndColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getEndColorRes();
            }
        }
        return R.color.colorTres;
    }


    // Método para aplicar el color de fondo
    private void applyBackground(View view, int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, startColor),
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, endColor)
                }
        );
        view.setBackground(gradientDrawable);
    }


    // Método para aplicar el color del BottomNavigationView
    public void applyNavigationColor(NavigationView navigationView, String selectedColor) {
        int startColor = getStartColor(selectedColor);
        int centerColor = getCenterColor(selectedColor);
        int endColor = getEndColor(selectedColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, startColor),
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, endColor)
                }
        );

        // Establecer el gradiente como el fondo del área de contenido principal del NavigationView
        View navigationViewContent = navigationView.getChildAt(0);
        navigationViewContent.setBackground(gradientDrawable);
    }

    public void applyToolbarColor(Toolbar toolbar, String selectedColor) {
        int centerColor = getStartColor(selectedColor);

        // Create a GradientDrawable with only two colors, using the centerColor as both start and end color
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, centerColor)
                }
        );

        // Set the gradient as the background of the Toolbar
        toolbar.setBackground(gradientDrawable);
    }



    // Método para crear la lista de objetos ColorModel
    private List<ColorModel> createColorList() {
        List<ColorModel> colorList = new ArrayList<>();
        colorList.add(new ColorModel(getString(R.string.cafe), R.color.cafeUno, R.color.cafeDos, R.color.cafeTres));
        colorList.add(new ColorModel(getString(R.string.azul), R.color.azulUno, R.color.azulDos, R.color.azulTres));
        colorList.add(new ColorModel(getString(R.string.gris), R.color.grisUno, R.color.grisDos, R.color.grisTres));
        colorList.add(new ColorModel(getString(R.string.verde), R.color.verdeUno, R.color.verdeDos, R.color.verdeTres));
        colorList.add(new ColorModel(getString(R.string.morado), R.color.moradoUno, R.color.moradoDos, R.color.moradoTres));
        colorList.add(new ColorModel(getString(R.string.rosa), R.color.rosaUno, R.color.rosaDos, R.color.rosaTres));
        return colorList;
    }

    private void setupLogoAndName(String nombreLogo, Bitmap imagenLogo) {
        View headerView = navigationView.getHeaderView(0);
        CircularImageView navImagen = headerView.findViewById(R.id.nav_imagen);
        TextView navNombre = headerView.findViewById(R.id.nav_nombre);

        // Update the logo and name in the header
        navImagen.setImageBitmap(imagenLogo);
        navNombre.setText(nombreLogo);
    }


    // Método para manejar el evento de cambio de logo y nombre
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoChangeEvent(LogoChangeEvent event) {
        String nombreLogo = event.getNombreLogo();
        Bitmap imagenLogo = event.getImagenLogo();

        // Update the logo and name in the NavigationView
        setupLogoAndName(nombreLogo, imagenLogo);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Desuscribirse de los eventos de EventBus al destruir la actividad
        EventBus.getDefault().unregister(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onColorChangeEvent(ColorChangeEvent event) {
        String selectedColor = event.getSelectedColor();
        applyBackground(findViewById(R.id.drawer_layout), getStartColor(selectedColor), getCenterColor(selectedColor), getEndColor(selectedColor));
        applyNavigationColor(navigationView, selectedColor);
        applyToolbarColor(toolbar, selectedColor);
    }



    private void actualizarLogoNavigationView(Bitmap imagenLogo) {
        View headerView = navigationView.getHeaderView(0);
        CircularImageView navImagen = headerView.findViewById(R.id.nav_imagen);
        navImagen.setImageBitmap(imagenLogo);
    }

}