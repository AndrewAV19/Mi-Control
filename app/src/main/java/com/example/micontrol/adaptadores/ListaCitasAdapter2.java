package com.example.micontrol.adaptadores;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.R;
import com.example.micontrol.entidades.Citas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaCitasAdapter2 extends RecyclerView.Adapter<ListaCitasAdapter2.CitasViewHolder2> {

    private ArrayList<Citas> listaCitas;
    private ArrayList<Citas> listaOriginal;
    private ArrayList<IngresoPorMes> ingresosPorMes;

    public ListaCitasAdapter2(ArrayList<Citas> listaCitas) {
        this.listaCitas = listaCitas;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCitas);
    }

    public void setIngresosPorMes(ArrayList<IngresoPorMes> ingresosPorMes) {
        this.ingresosPorMes = ingresosPorMes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CitasViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_citas2, parent, false);
        return new CitasViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder2 holder, int position) {
        IngresoPorMes ingresoPorMes = ingresosPorMes.get(position);
        holder.viewMes.setText("Mes: " + ingresoPorMes.getMes());
        holder.viewIngresoTotal.setText("Ingreso Total: " + ingresoPorMes.getIngresoTotal());
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaCitas.clear();
            listaCitas.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Citas> coleccion = listaCitas.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaCitas.clear();
                listaCitas.addAll(coleccion);
            } else {
                for (Citas c : listaOriginal) {
                    if (c.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaCitas.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCitas(ArrayList<Citas> citas) {
        listaCitas = citas;
        listaOriginal.clear();
        listaOriginal.addAll(citas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class CitasViewHolder2 extends RecyclerView.ViewHolder {

        TextView viewMes, viewIngresoTotal;

        public CitasViewHolder2(@NonNull View itemView) {
            super(itemView);
            viewMes = itemView.findViewById(R.id.viewMes);
            viewIngresoTotal = itemView.findViewById(R.id.viewIngresoTotal);
        }
    }

    public static class IngresoPorMes {
        private String mes;
        private double ingresoTotal;

        public IngresoPorMes(String mes, double ingresoTotal) {
            this.mes = mes;
            this.ingresoTotal = ingresoTotal;
        }

        public String getMes() {
            return mes;
        }

        public double getIngresoTotal() {
            return ingresoTotal;
        }
    }
}