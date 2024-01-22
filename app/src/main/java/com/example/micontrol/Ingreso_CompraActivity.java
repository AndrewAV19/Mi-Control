package com.example.micontrol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.micontrol.adaptadores.ListaCitasAdapter2;
import com.example.micontrol.db.DbHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Ingreso_CompraActivity extends AppCompatActivity {

    private double[] ingresosPorMes;
    private int currentYear;
    private Calendar calendar;
    private TextView textViewDate;
    private TextView textViewNoCitas;
    private ListaCitasAdapter2 adapter;


    ImageButton buttonPrev, buttonNext;
    private TextView precioEnero, precioFebrero, precioMarzo, precioAbril, precioMayo, precioJunio, precioJulio,
            precioAgosto, precioSeptiembre, precioOctubre, precioNoviembre, precioDiciembre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_compra);

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
        bottomNavigationView.setSelectedItemId(R.id.ingresos_compras);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_compra:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "compras");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_compra:
                    startActivity(new Intent(getApplicationContext(), CrearCompraActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.editar_compra:
                    startActivity(new Intent(getApplicationContext(), EditarCompraActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingresos_compras:
                    return true;
                case R.id.historial_compras:
                    startActivity(new Intent(getApplicationContext(), HistorialComprasActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
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
        Cursor c = db.rawQuery(
                "SELECT SUM(" + DbHelper.TABLE_COMPRAS + ".total) FROM " +
                        DbHelper.TABLE_COMPRAS + " " +
                        " WHERE strftime('%m', " +
                        DbHelper.TABLE_COMPRAS + ".fecha) = ? AND strftime('%Y', " +
                        DbHelper.TABLE_COMPRAS + ".fecha) = ?",
                new String[]{String.format("%02d", mes), String.valueOf(year)});

        if (c.moveToFirst()) {
            suma = c.getDouble(0);
            ingresosPorMes[mes - 1] = suma; // Actualizar el valor del array ingresosPorMes
        }
        c.close();
        db.close();
        return suma;
    }

}
