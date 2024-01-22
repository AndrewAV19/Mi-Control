package com.example.micontrol;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        // Obtén el color seleccionado desde SharedPreferences
        String selectedColor = AppPreferences.getSelectedColor(this);
        int startColor = getStartColor(selectedColor);
        int centerColor = getCenterColor(selectedColor);
        int endColor = getEndColor(selectedColor);
        applyBackground(startColor, centerColor, endColor);
    }

    protected void applyBackground(int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, startColor),
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, endColor)
                }
        );
        View view = findViewById(android.R.id.content);
        if (view != null) {
            view.setBackground(gradientDrawable);
        }
    }

    private int getStartColor(String selectedColor) {
        switch (selectedColor) {
            case "cafe":
                return R.color.cafeUno;
            case "azul":
                return R.color.azulUno;
            case "gris":
                return R.color.grisUno;
            case "verde":
                return R.color.verdeUno;
            case "morado":
                return R.color.moradoUno;
            case "rosa":
                return R.color.rosaUno;
            default:
                return R.color.colorUno;
        }
    }

    private int getCenterColor(String selectedColor) {
        switch (selectedColor) {
            case "cafe":
                return R.color.cafeDos;
            case "azul":
                return R.color.azulDos;
            case "gris":
                return R.color.grisDos;
            case "verde":
                return R.color.verdeDos;
            case "morado":
                return R.color.moradoDos;
            case "rosa":
                return R.color.rosaDos;
            default:
                return R.color.colorDos;
        }
    }

    private int getEndColor(String selectedColor) {
        switch (selectedColor) {
            case "cafe":
                return R.color.cafeTres;
            case "azul":
                return R.color.azulTres;
            case "gris":
                return R.color.grisTres;
            case "verde":
                return R.color.verdeTres;
            case "morado":
                return R.color.moradoTres;
            case "rosa":
                return R.color.rosaTres;
            default:
                return R.color.colorTres;
        }
    }
}
