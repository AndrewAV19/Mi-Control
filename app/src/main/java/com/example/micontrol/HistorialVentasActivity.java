package com.example.micontrol;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.adaptadores.ListaVentasAdapter;
import com.example.micontrol.db.DbVentas;
import com.example.micontrol.entidades.Ventas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, ListaVentasAdapter.OnCitaClickListener {

    RecyclerView listaVentas;
    SearchView txtBuscar;
    ArrayList<Ventas> listaArrayVentas;
    ListaVentasAdapter adapter;
    TextView txtNoVentas;
    private List<ColorModel> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ventas);
        listaVentas = findViewById(R.id.listaVentas);
        txtBuscar = findViewById(R.id.txtBuscar);
        txtNoVentas = findViewById(R.id.txtNoVentas);
        listaVentas.setLayoutManager(new LinearLayoutManager(this));

        DbVentas dbVentas = new DbVentas(HistorialVentasActivity.this);

        listaArrayVentas = new ArrayList<>();

        adapter = new ListaVentasAdapter(dbVentas.mostrarHistorialVentas());
        listaVentas.setAdapter(adapter);

        // Verificar si no hay ventas y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoVentas.setVisibility(View.VISIBLE);
        } else {
            txtNoVentas.setVisibility(View.GONE);
        }

        // Configurar el listener de búsqueda
        txtBuscar.setOnQueryTextListener(this);

        adapter.setOnCitaClickListener(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.historial_v), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));

        bottomNavigationView.setSelectedItemId(R.id.historial_ventas);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_venta:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "ventas");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_venta:
                    startActivity(new Intent(getApplicationContext(), CrearVentaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.editar_venta:
                    startActivity(new Intent(getApplicationContext(), EditarVentaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingresos_ventas:
                    startActivity(new Intent(getApplicationContext(), IngresosVentasActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.historial_ventas:
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtrado(s);

        // Verificar si no hay ventas y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoVentas.setVisibility(View.VISIBLE);
        } else {
            txtNoVentas.setVisibility(View.GONE);
        }

        return false;
    }

    @Override
    public void onCitaClick(Ventas ventas) {
        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ACCION)
                .setMessage(R.string.QUEACCIONDESEASREALIZAR)
                .setPositiveButton(R.string.ENVIARPORWHATS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enviarPorWhatsApp(ventas.getId());
                    }
                })
                .setNegativeButton(R.string.VISUALIZARPDF, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        visualizarPDF(ventas.getId());
                    }
                })
                .show();
    }


    public void enviarPorWhatsApp(int id) {
        String carpeta = "/ventas_mi_control_pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;
        String archivoPath = path + "/" + id + ".pdf";

        File archivo = new File(archivoPath);

        if (archivo.exists()) {
            Uri archivoUri = FileProvider.getUriForFile(this, "com.example.micontrol.fileprovider", archivo);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, archivoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Concede permisos de lectura al receptor

            intent.setPackage("com.whatsapp");

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string.WhatsappNoIntalado, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.AUN_NO_HAS_CREADO_EL_PDF, Toast.LENGTH_SHORT).show();
        }
    }


    public void visualizarPDF(int id) {
        String carpeta = "/ventas_mi_control_pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;
        String archivoPath = path + "/" + id + ".pdf";

        File archivo = new File(archivoPath);

        if (archivo.exists()) {
            // Crear el objeto Uri para el archivo PDF
            Uri archivoUri = FileProvider.getUriForFile(this, "com.example.micontrol.fileprovider", archivo);


            // Crear el intent para abrir y visualizar el PDF con una aplicación externa
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(archivoUri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Agregar el flag para conceder permisos de lectura al receptor
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Manejar la excepción si no hay aplicaciones instaladas que puedan abrir el PDF
                Toast.makeText(this, R.string.NOSEENCONTROAPP, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.AUN_NO_HAS_CREADO_EL_PDF, Toast.LENGTH_SHORT).show();
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