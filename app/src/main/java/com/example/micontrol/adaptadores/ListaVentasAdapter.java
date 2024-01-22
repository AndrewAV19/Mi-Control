package com.example.micontrol.adaptadores;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.R;
import com.example.micontrol.entidades.Ventas;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaVentasAdapter extends RecyclerView.Adapter<ListaVentasAdapter.VentasViewHolder> {

    ArrayList<Ventas> listaVentas;
    ArrayList<Ventas> listaOriginal;


    public interface OnCitaClickListener {
        void onCitaClick(Ventas ventas);

    }

    private OnCitaClickListener clickListener;

    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.clickListener = listener;
    }



    public ListaVentasAdapter(ArrayList<Ventas> listaVentas) {
        this.listaVentas = listaVentas;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaVentas);
    }



    @NonNull
    @Override
    public VentasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_ventas, parent, false);
        return new VentasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentasViewHolder holder, int position) {
        holder.viewFolio.setText(holder.itemView.getContext().getString(R.string.folio) +" "+ listaVentas.get(position).getId());
        holder.ViewFecha.setText(holder.itemView.getContext().getString(R.string.FECHA) +" "+ listaVentas.get(position).getFecha());
        holder.ViewHora.setText(holder.itemView.getContext().getString(R.string.HORA) +" "+ listaVentas.get(position).getHora());
        holder.ViewCliente.setText(holder.itemView.getContext().getString(R.string.CLIENTE) +" "+ listaVentas.get(position).getCliente());

        // Obtener las cadenas de los recursos
        String language = holder.itemView.getContext().getString(R.string.LENGUAJE);
        String country = holder.itemView.getContext().getString(R.string.PAIS);

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(language, country));

        String totalFormateado = format.format(listaVentas.get(position).getTotal());

        holder.ViewTotal.setText(holder.itemView.getContext().getString(R.string.TOTAL) +" "+totalFormateado );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                Ventas ventas = listaVentas.get(position);
                clickListener.onCitaClick(ventas);
            }
        });


    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaVentas.clear();
            listaVentas.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Ventas> coleccion = listaOriginal.stream()
                        .filter(i -> i.getCliente().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaVentas.clear();
                listaVentas.addAll(coleccion);
            } else {
                listaVentas.clear();
                for (Ventas c : listaOriginal) {
                    if (c.getCliente().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaVentas.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setVentas(ArrayList<Ventas> ventas) {
        // Invierte el orden de las citas para mostrar las más recientes primero
        Collections.reverse(ventas);

        listaVentas = ventas;
        listaOriginal.clear();
        listaOriginal.addAll(ventas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaVentas.size();
    }

    public class VentasViewHolder extends RecyclerView.ViewHolder {

        TextView viewFolio, ViewFecha, ViewHora, ViewCliente, ViewTotal;

        public VentasViewHolder(@NonNull View itemView) {
            super(itemView);

            viewFolio = itemView.findViewById(R.id.ViewFolio);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewCliente = itemView.findViewById(R.id.ViewCliente);
            ViewTotal = itemView.findViewById(R.id.ViewTotal);
        }
    }
}