package com.example.micontrol;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class Pantalla_De_Carga extends AppCompatActivity {
    private List<ColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_de_carga);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.p_c), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        //applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));


        int Tiempo = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Pantalla_De_Carga.this, MainActivity.class));
                finish();
            }
        },Tiempo);
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
        setStatusBarColor(ContextCompat.getColor(this, startColor));
    }


    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }


    // Método para aplicar el color del BottomNavigationView
    private void applyBottomNavigationColor(BottomNavigationView bottomNavigationView, String selectedColor) {
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

        // Establecer el gradiente como el fondo del área de contenido principal del BottomNavigationView
        View bottomNavigationContent = bottomNavigationView.getChildAt(0);
        bottomNavigationContent.setBackground(gradientDrawable);
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

}