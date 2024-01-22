package com.example.micontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.micontrol.db.DbCitas;
import com.example.micontrol.db.DbServicios;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CrearCitaActivity extends AppCompatActivity {

    EditText tv;
    EditText tv1;
    EditText name,phone,calendario_txt,hora_txt,informacion_adicional;
    Spinner servicio;
    ImageButton btnCrear;
    private List<ColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cita);
        tv = findViewById(R.id.calendario_txt);
        tv1 = findViewById(R.id.hora_txt);

        informacion_adicional = findViewById(R.id.informacion_adicional);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        calendario_txt = findViewById(R.id.calendario_txt);
        hora_txt = findViewById(R.id.hora_txt);
        servicio = findViewById(R.id.servicio);
        btnCrear = findViewById(R.id.añadir);

        DbServicios dbServicios = new DbServicios(this);

        List<String> serviceNames = dbServicios.getServiceNames();

        if (serviceNames.isEmpty()) {
            Toast.makeText(this, R.string.AunNoHasAgregadoServicios, Toast.LENGTH_LONG).show();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item_jeje, serviceNames);

            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            servicio.setAdapter(adapter);


        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.crear_c), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));


        bottomNavigationView.setSelectedItemId(R.id.crear_cita);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_cita:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "citas");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_cita:
                    return true;
                case R.id.editar_cita:
                    startActivity(new Intent(getApplicationContext(), EditarCitaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.eliminar_cita:
                    startActivity(new Intent(getApplicationContext(), EstatusCitaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.historial_citas:
                    startActivity(new Intent(getApplicationContext(), HistorialCitasActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
            }
            return false;
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = name.getText().toString().trim();
                String telefono = phone.getText().toString().trim();
                String fecha = calendario_txt.getText().toString().trim();
                String hora = hora_txt.getText().toString().trim();
                String info = informacion_adicional.getText().toString().trim();


                if (nombre.isEmpty() || telefono.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(CrearCitaActivity.this, R.string.llenaLosDatosFaltantes, Toast.LENGTH_LONG).show();
                } else {
                    DbCitas dbCitas = new DbCitas(CrearCitaActivity.this);

                    // Obtener el nombre del servicio seleccionado
                    String servicioSeleccionado = servicio.getSelectedItem().toString();

                    // Obtener el ID del servicio desde la base de datos
                    int id_servicio = dbServicios.getServiceIdByName(servicioSeleccionado);



                    long id = dbCitas.insertarCitas(nombre, telefono, fecha, hora, id_servicio,info);
                    if (id > 0) {
                        Toast.makeText(CrearCitaActivity.this, R.string.CitaGuardada, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), CrearCitaActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else {
                        Toast.makeText(CrearCitaActivity.this, R.string.ErrorCita, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void abrirCalendario(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(CrearCitaActivity.this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        String fecha = String.format("%04d-%02d-%02d", year, month + 1, dayofMonth);
                        tv.setText(fecha);
                    }
                }, anio, mes, dia);
        dpd.show();
    }


    public void abrirHora(View view){
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        TimePickerDialog tmd = new TimePickerDialog(CrearCitaActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaString = String.format("%02d:%02d", hourOfDay, minute);
                tv1.setText(horaString);
            }
        },hora,min,false);
        tmd.show();
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
