package com.example.micontrol;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.adaptadores.ListaCitasAdapter;
import com.example.micontrol.db.DbCitas;
import com.example.micontrol.entidades.Citas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HistorialCitasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ListaCitasAdapter.OnCitaClickListener {

    RecyclerView listaCitas;
    SearchView txtBuscar;
    ArrayList<Citas> listaArrayCitas;
    ListaCitasAdapter adapter;
    TextView txtNoCitas;
    private List<ColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_citas);
        listaCitas = findViewById(R.id.listaCitas);
        txtBuscar = findViewById(R.id.txtBuscar);
        txtNoCitas = findViewById(R.id.txtNoCitas);
        listaCitas.setLayoutManager(new LinearLayoutManager(this));

        DbCitas dbCitas = new DbCitas(HistorialCitasActivity.this);

        listaArrayCitas = new ArrayList<>();

        adapter = new ListaCitasAdapter(dbCitas.mostrarHistorialCitas());
        listaCitas.setAdapter(adapter);

        // Verificar si no hay citas y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoCitas.setVisibility(View.VISIBLE);
        } else {
            txtNoCitas.setVisibility(View.GONE);
        }

        // Configurar el listener de búsqueda
        txtBuscar.setOnQueryTextListener(this);

        adapter.setOnCitaClickListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.historial_c), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));


        bottomNavigationView.setSelectedItemId(R.id.historial_citas);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_cita:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "citas");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_cita:
                    startActivity(new Intent(getApplicationContext(), CrearCitaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
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
                    return true;
            }
            return false;
        });
    }

    public void onCitaClick(Citas cita) {
        String mensaje = getString(R.string.Hola) + " " + cita.getNombre() + " " + getString(R.string.Tengounacitaprogramadael) + " " + cita.getFecha() + " " + getString(R.string.alas) + " " + cita.getHora() +
                " " + getString(R.string.paraelservicio) + " " + cita.getServicio()+getString(R.string.nosvemos);

        ConfirmationDialog.show(this, getString(R.string.enviarCitaPorWhats), getString(R.string.deseasenviarcita), getString(R.string.enviar), getString(R.string.cancelar), new ConfirmationDialog.ConfirmationDialogListener() {
            @Override
            public void onPositiveButtonClicked() {
                // Abre la aplicación de WhatsApp con un mensaje predefinido
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?text=" + mensaje));
                startActivity(intent);
            }

            @Override
            public void onNegativeButtonClicked() {
                // No hacer nada si se selecciona "Cancelar"
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtrado(s);

        // Verificar si no hay citas y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoCitas.setVisibility(View.VISIBLE);
        } else {
            txtNoCitas.setVisibility(View.GONE);
        }

        return false;
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