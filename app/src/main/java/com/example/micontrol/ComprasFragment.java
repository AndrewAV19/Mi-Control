package com.example.micontrol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.adaptadores.ListaComprasAdapter;
import com.example.micontrol.db.DbCompras;
import com.example.micontrol.db.DbHelper;
import com.example.micontrol.entidades.Compras;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ComprasFragment extends Fragment {

    private Calendar calendar;
    private TextView textViewDate, gastoDiario;
    private TextView textViewNoCompras;
    private RecyclerView recyclerView;
    private double gastoDiarioTotal = 0.0;
    private ListaComprasAdapter adapter;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<ColorModel> colorList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compras, container, false);

        gastoDiario = view.findViewById(R.id.ingresoDiario);
        calendar = Calendar.getInstance();
        textViewDate = view.findViewById(R.id.textViewDate);
        recyclerView = view.findViewById(R.id.recyclerViewCompras);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ListaComprasAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        textViewNoCompras = view.findViewById(R.id.textViewNoCompras);

        updateCompras();

        ImageButton buttonPrev = view.findViewById(R.id.buttonPrev);
        buttonPrev.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateCompras();
        });

        ImageButton buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateCompras();
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(view.findViewById(R.id.com), getStartColor(AppPreferences.getSelectedColor(requireContext())), getCenterColor(AppPreferences.getSelectedColor(requireContext())), getEndColor(AppPreferences.getSelectedColor(requireContext())));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(requireContext()));


        bottomNavigationView.setSelectedItemId(R.id.home_compra);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_compra:
                    return true;
                case R.id.crear_compra:
                    startActivity(new Intent(getActivity(), CrearCompraActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.editar_compra:
                    startActivity(new Intent(getActivity(), EditarCompraActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.ingresos_compras:
                    startActivity(new Intent(getActivity(), IngresosComprasActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.historial_compras:
                    startActivity(new Intent(getActivity(), HistorialComprasActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
            }
            return false;
        });

        return view;
    }

    private void updateCompras() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());
        textViewDate.setText(dateString);

        // Lógica para obtener las compras del día seleccionado y actualizar el adaptador
        DbCompras dbCompras = new DbCompras(getActivity());
        ArrayList<Compras> comprasDelDia = dbCompras.obtenerComprasPorFecha(dateString);
        adapter.setCompras(comprasDelDia);
        if (comprasDelDia.isEmpty()) {
            textViewNoCompras.setVisibility(View.VISIBLE);
        } else {
            textViewNoCompras.setVisibility(View.GONE);
        }

        sumarGastosDiarios();
    }

    private void sumarGastosDiarios() {
        double suma = 0;
        DbHelper dbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());

        Cursor c = db.rawQuery(
                "SELECT SUM(" + DbHelper.TABLE_COMPRAS + ".total) FROM " +
                        DbHelper.TABLE_COMPRAS + " " +
                        " WHERE " + DbHelper.TABLE_COMPRAS + ".fecha = ?",
                new String[]{dateString});

        if (c.moveToFirst()) {
            suma = c.getDouble(0);
        }
        c.close();
        db.close();

        gastoDiarioTotal = suma;
        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String precioFormateado = format.format(gastoDiarioTotal);
        gastoDiario.setText(precioFormateado);
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
                        ContextCompat.getColor(requireContext(), startColor),
                        ContextCompat.getColor(requireContext(), centerColor),
                        ContextCompat.getColor(requireContext(), endColor)

                }
        );
        view.setBackground(gradientDrawable);
        setStatusBarColor(ContextCompat.getColor(requireContext(), startColor));
    }


    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(color);
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
                        ContextCompat.getColor(requireContext(), startColor),
                        ContextCompat.getColor(requireContext(), centerColor),
                        ContextCompat.getColor(requireContext(), endColor)
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
