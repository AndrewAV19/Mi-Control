package com.example.micontrol;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.adaptadores.ListaProductosAdapter2;
import com.example.micontrol.db.DbProductos;
import com.example.micontrol.entidades.Productos;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class InventarioFragment extends Fragment implements SearchView.OnQueryTextListener {

    RecyclerView listaProductos;
    ArrayList<Productos> listaArrayProductos;
    SearchView txtBuscar;
    ListaProductosAdapter2 adapter;
    TextView txtNoProductos;
    private List<ColorModel> colorList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        listaProductos = view.findViewById(R.id.listaProductos);
        txtBuscar = view.findViewById(R.id.txtBuscar);
        txtNoProductos = view.findViewById(R.id.txtNoProductos);
        listaProductos.setLayoutManager(new LinearLayoutManager(requireContext()));

        DbProductos dbProductos = new DbProductos(requireContext());

        listaArrayProductos = dbProductos.mostrarHistorialProductos1();

        adapter = new ListaProductosAdapter2(listaArrayProductos);
        listaProductos.setAdapter(adapter);

        // Verificar si no hay productos y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoProductos.setVisibility(View.VISIBLE);
        } else {
            txtNoProductos.setVisibility(View.GONE);
        }

        // Configurar el listener de búsqueda
        txtBuscar.setOnQueryTextListener(this);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(view.findViewById(R.id.inve), getStartColor(AppPreferences.getSelectedColor(requireContext())), getCenterColor(AppPreferences.getSelectedColor(requireContext())), getEndColor(AppPreferences.getSelectedColor(requireContext())));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(requireContext()));


        bottomNavigationView.setSelectedItemId(R.id.home_producto);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_producto:
                    return true;
                case R.id.crear_producto:
                    startActivity(new Intent(getActivity(), CrearProductoActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.editar_producto:
                    startActivity(new Intent(getActivity(), EditarProductoActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.historial_productos:
                    startActivity(new Intent(getActivity(), HistorialProductosActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
            }
            return false;
        });

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filtrado(s);

        // Verificar si no hay productos y mostrar el mensaje correspondiente
        if (adapter.getItemCount() == 0) {
            txtNoProductos.setVisibility(View.VISIBLE);
        } else {
            txtNoProductos.setVisibility(View.GONE);
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