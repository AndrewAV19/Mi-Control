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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListaCitasAdapter extends RecyclerView.Adapter<ListaCitasAdapter.CitasViewHolder> {

    ArrayList<Citas> listaCitas;
    ArrayList<Citas> listaOriginal;

    public interface OnCitaClickListener {
        void onCitaClick(Citas cita);
    }

    private OnCitaClickListener clickListener;

    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.clickListener = listener;
    }

    public ListaCitasAdapter(ArrayList<Citas> listaCitas) {
        this.listaCitas = listaCitas;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCitas);
    }

    @NonNull
    @Override
    public CitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_citas, parent, false);
        return new CitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder holder, int position) {
        holder.viewNombre.setText(holder.itemView.getContext().getString(R.string.NOMBRE) +" "+ listaCitas.get(position).getNombre());
        holder.ViewTelefono.setText(holder.itemView.getContext().getString(R.string.TELEFONO) +" "+ listaCitas.get(position).getTelefono());
        holder.ViewFecha.setText(holder.itemView.getContext().getString(R.string.FECHA) +" "+ listaCitas.get(position).getFecha());
        holder.ViewHora.setText(holder.itemView.getContext().getString(R.string.HORA) +" "+ listaCitas.get(position).getHora());
        holder.ViewServicio.setText(holder.itemView.getContext().getString(R.string.SERVICIO) +" "+ listaCitas.get(position).getServicio());
        //holder.ViewServicio.setText(holder.itemView.getContext().getString(R.string.infoAdicionalConDosPuntos) +" "+ listaCitas.get(position).getInformacion_adicional());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                Citas cita = listaCitas.get(position);
                clickListener.onCitaClick(cita);
            }
        });
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaCitas.clear();
            listaCitas.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Citas> coleccion = listaOriginal.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaCitas.clear();
                listaCitas.addAll(coleccion);
            } else {
                listaCitas.clear();
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
        // Invierte el orden de las citas para mostrar las más recientes primero
        Collections.reverse(citas);

        listaCitas = citas;
        listaOriginal.clear();
        listaOriginal.addAll(citas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class CitasViewHolder extends RecyclerView.ViewHolder {

        TextView viewNombre, ViewTelefono, ViewFecha, ViewHora, ViewServicio,ViewInformacion_adicional;

        public CitasViewHolder(@NonNull View itemView) {
            super(itemView);

            //ViewInformacion_adicional = itemView.findViewById(R.id.ViewInformacion_adicional);
            viewNombre = itemView.findViewById(R.id.viewNombre);
            ViewTelefono = itemView.findViewById(R.id.ViewTelefono);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewServicio = itemView.findViewById(R.id.ViewServicio);
        }
    }
}