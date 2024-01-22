package com.example.micontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.micontrol.db.DbCitas;
import com.example.micontrol.db.DbServicios;
import com.example.micontrol.entidades.Citas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VerCita extends AppCompatActivity {

    EditText name,phone,calendario_txt,hora_txt,informacion_adicional;
    Spinner servicio;
    ImageButton btnCrear,btnEliminar;
    boolean correcto = false;
    Citas citas;
    int id = 0;
    private int selectedServicePosition = 0;
    private List<ColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_cita);

        informacion_adicional = findViewById(R.id.informacion_adicional);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        calendario_txt = findViewById(R.id.calendario_txt);
        hora_txt = findViewById(R.id.hora_txt);
        servicio = findViewById(R.id.servicio);
        btnCrear = findViewById(R.id.añadir);
        btnEliminar = findViewById(R.id.eliminar);

        DbServicios dbServicios = new DbServicios(this);
        List<String> serviceNames = dbServicios.getServiceNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_jeje, serviceNames);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        servicio.setAdapter(adapter);

        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                id = Integer.parseInt(null);
            }else{
                id = extras.getInt("ID");
            }
        }else{
            id = (int) savedInstanceState.getSerializable("ID");
        }

        DbCitas dbCitas = new DbCitas(VerCita.this);
        citas = dbCitas.verCitas(id);

        if (citas != null){
            name.setText(citas.getNombre());
            phone.setText(citas.getTelefono());
            calendario_txt.setText(citas.getFecha());
            hora_txt.setText(citas.getHora());
            informacion_adicional.setText(citas.getInformacion_adicional());
            //servicio.setText(citas.getId_servicio());

            String serviceName = dbServicios.getServiceNameById(citas.getId_servicio());

            // Actualizar la variable global selectedServicePosition
            selectedServicePosition = adapter.getPosition(serviceName);

            // Establecer la selección en el Spinner
            servicio.setSelection(selectedServicePosition);
        }

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.ver_c), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        //applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));



        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().equals("") && !phone.getText().toString().equals("") && !calendario_txt.getText().toString().equals("") && !hora_txt.getText().toString().equals("")){
                    String selectedServiceName = servicio.getSelectedItem().toString();
                    int id_servicio = dbServicios.getServiceIdByName(selectedServiceName);
                    correcto = dbCitas.editarCitas(id,name.getText().toString(),phone.getText().toString(),calendario_txt.getText().toString(),hora_txt.getText().toString(),id_servicio,informacion_adicional.getText().toString());

                    if(correcto){
                        Toast.makeText(VerCita.this, R.string.CitaModificada,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), EditarCitaActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    }else {
                        Toast.makeText(VerCita.this, R.string.ErrorModificarCita,Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(VerCita.this, R.string.llenaLosDatosFaltantes,Toast.LENGTH_LONG).show();
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerCita.this);

                // Creamos un objeto ImageView y establecemos la imagen en él
                ImageView imageView = new ImageView(VerCita.this);
                imageView.setImageResource(R.drawable.logomodified1);
                // Establecemos el tamaño de la imagen
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Agregamos el objeto ImageView al diseño del diálogo
                RelativeLayout layout = new RelativeLayout(VerCita.this);
                layout.addView(imageView);

                // Agregamos el mensaje al diseño del diálogo
                TextView textView = new TextView(VerCita.this);
                textView.setText(R.string.DeseasEliminarCita);
                textView.setPadding(20, 25, 20, 20);
                textView.setTextSize(20);
                textView.setId(View.generateViewId());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, imageView.getId());
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layout.addView(textView, params);

                builder.setView(layout);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dbCitas.eliminarCitas(id)){
                                    startActivity(new Intent(getApplicationContext(), EditarCitaActivity.class));
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }

                        });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        positiveButton.setTextColor(getResources().getColor(R.color.white));
                        negativeButton.setTextColor(getResources().getColor(R.color.white));
                    }
                });
                dialog.getWindow().setBackgroundDrawableResource(R.color.colorDos);
                dialog.show();
            }
        });
    }
    public void abrirCalendario(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(VerCita.this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayofMonth) {
                        String fecha = String.format("%04d-%02d-%02d", year, month + 1, dayofMonth);
                        calendario_txt.setText(fecha);
                    }
                }, anio, mes, dia);
        dpd.show();
    }

    public void abrirHora(View view){
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        TimePickerDialog tmd = new TimePickerDialog(VerCita.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaString = String.format("%02d:%02d", hourOfDay, minute);
                hora_txt.setText(horaString);
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