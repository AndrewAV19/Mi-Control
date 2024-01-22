package com.example.micontrol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.micontrol.adaptadores.ListaCitasAdapter2;
import com.example.micontrol.db.DbHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Ingreso_GeneralActivity extends AppCompatActivity {

    private double[] ingresosPorMes;
    private int currentYear;
    private Calendar calendar;
    private TextView textViewDate;
    private TextView textViewNoCitas;
    private ListaCitasAdapter2 adapter;
    private List<ColorModel> colorList;

    ImageButton buttonPrev, buttonNext;
    private TextView precioEnero, precioFebrero, precioMarzo, precioAbril, precioMayo, precioJunio, precioJulio,
            precioAgosto, precioSeptiembre, precioOctubre, precioNoviembre, precioDiciembre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_general);

        calendar = Calendar.getInstance();
        textViewDate = findViewById(R.id.textViewDate);
        adapter = new ListaCitasAdapter2(new ArrayList<>());
        textViewNoCitas = findViewById(R.id.textViewNoCitas);
        precioEnero = findViewById(R.id.precio_enero);
        precioFebrero = findViewById(R.id.precio_febrero);
        precioMarzo = findViewById(R.id.precio_marzo);
        precioAbril = findViewById(R.id.precio_abril);
        precioMayo = findViewById(R.id.precio_mayo);
        precioJunio = findViewById(R.id.precio_junio);
        precioJulio = findViewById(R.id.precio_julio);
        precioAgosto = findViewById(R.id.precio_agosto);
        precioSeptiembre = findViewById(R.id.precio_septiembre);
        precioOctubre = findViewById(R.id.precio_octubre);
        precioNoviembre = findViewById(R.id.precio_noviembre);
        precioDiciembre = findViewById(R.id.precio_diciembre);
        buttonPrev = findViewById(R.id.buttonPrev);
        buttonNext = findViewById(R.id.buttonNext);
        ingresosPorMes = new double[12];

        currentYear = calendar.get(Calendar.YEAR);
        textViewDate.setText(String.valueOf(currentYear));

        updateMonthPrices(currentYear);

        buttonPrev.setOnClickListener(view -> {
            currentYear--;
            textViewDate.setText(String.valueOf(currentYear));
            updateMonthPrices(currentYear);
        });

        buttonNext.setOnClickListener(view -> {
            currentYear++;
            textViewDate.setText(String.valueOf(currentYear));
            updateMonthPrices(currentYear);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.ingreso_gen), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));

        bottomNavigationView.setSelectedItemId(R.id.ingreso_general);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_ingreso:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "ingresos");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.ingreso_citas:
                    startActivity(new Intent(getApplicationContext(), Ingreso_CitaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingreso_ventas:
                    startActivity(new Intent(getApplicationContext(), Ingreso_VentaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingreso_ganancias_ventas:
                    startActivity(new Intent(getApplicationContext(), Ingreso_Ganancias_VentaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingreso_general:
                    return true;

            }
            return false;
        });

    }

    private void updateMonthPrices(int year) {
        for (int i = 1; i <= 12; i++) {
            double ingresos = sumarPreciosPorMes(i, year);
            setPrecioMes(i, ingresos);
        }
    }

    private void setPrecioMes(int mes, double ingresos) {
        TextView textViewMes;
        switch (mes) {
            case 1:
                textViewMes = precioEnero;
                break;
            case 2:
                textViewMes = precioFebrero;
                break;
            case 3:
                textViewMes = precioMarzo;
                break;
            case 4:
                textViewMes = precioAbril;
                break;
            case 5:
                textViewMes = precioMayo;
                break;
            case 6:
                textViewMes = precioJunio;
                break;
            case 7:
                textViewMes = precioJulio;
                break;
            case 8:
                textViewMes = precioAgosto;
                break;
            case 9:
                textViewMes = precioSeptiembre;
                break;
            case 10:
                textViewMes = precioOctubre;
                break;
            case 11:
                textViewMes = precioNoviembre;
                break;
            case 12:
                textViewMes = precioDiciembre;
                break;
            default:
                return;
        }
        // Formatear el ingreso utilizando NumberFormat
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String ingresosFormatted = numberFormat.format(ingresos);

        textViewMes.setText(ingresosFormatted);
    }


    private double sumarPreciosPorMes(int mes, int year) {
        double suma = 0;
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta para sumar los precios de los servicios por mes y año
        Cursor c1 = db.rawQuery(
                "SELECT SUM(" + DbHelper.TABLE_SERVICIOS + ".precio) FROM " +
                        DbHelper.TABLE_SERVICIOS + " INNER JOIN " +
                        DbHelper.TABLE_CITAS + " ON " +
                        DbHelper.TABLE_SERVICIOS + ".id = " +
                        DbHelper.TABLE_CITAS + ".id_servicio WHERE strftime('%m', " +
                        DbHelper.TABLE_CITAS + ".fecha) = ? AND strftime('%Y', " +
                        DbHelper.TABLE_CITAS + ".fecha) = ?",
                new String[]{String.format("%02d", mes), String.valueOf(year)});

        if (c1.moveToFirst()) {
            suma += c1.getDouble(0);
        }

        c1.close();

        // Consulta para sumar los precios de las ventas por mes y año
        Cursor c2 = db.rawQuery(
                "SELECT SUM(" + DbHelper.TABLE_VENTAS + ".total) FROM " +
                        DbHelper.TABLE_VENTAS + " " +
                        " WHERE strftime('%m', " +
                        DbHelper.TABLE_VENTAS + ".fecha) = ? AND strftime('%Y', " +
                        DbHelper.TABLE_VENTAS + ".fecha) = ?",
                new String[]{String.format("%02d", mes), String.valueOf(year)});

        if (c2.moveToFirst()) {
            suma += c2.getDouble(0);
        }

        c2.close();
        db.close();

        ingresosPorMes[mes - 1] = suma; // Actualizar el valor del array ingresosPorMes

        return suma;
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
