package com.example.micontrol.adaptadores;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.R;
import com.example.micontrol.entidades.Compras;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaComprasAdapter extends RecyclerView.Adapter<ListaComprasAdapter.ComprasViewHolder> {

    ArrayList<Compras> listaCompras;
    ArrayList<Compras> listaOriginal;


    public interface OnCitaClickListener {
        void onCitaClick(Compras compras);

    }

    private OnCitaClickListener clickListener;

    public void setOnCitaClickListener(OnCitaClickListener listener) {
        this.clickListener = listener;
    }



    public ListaComprasAdapter(ArrayList<Compras> listaCompras) {
        this.listaCompras = listaCompras;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCompras);
    }



    @NonNull
    @Override
    public ComprasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_compras, parent, false);
        return new ComprasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComprasViewHolder holder, int position) {
        holder.viewFolio.setText(holder.itemView.getContext().getString(R.string.folio) +" "+ listaCompras.get(position).getId());
        holder.ViewFecha.setText(holder.itemView.getContext().getString(R.string.FECHA) +" "+ listaCompras.get(position).getFecha());
        holder.ViewHora.setText(holder.itemView.getContext().getString(R.string.HORA) +" "+ listaCompras.get(position).getHora());
        holder.ViewCliente.setText(holder.itemView.getContext().getString(R.string.Proveedor) +" "+ listaCompras.get(position).getCliente());

        // Obtener las cadenas de los recursos
        String language = holder.itemView.getContext().getString(R.string.LENGUAJE);
        String country = holder.itemView.getContext().getString(R.string.PAIS);

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(language, country));

        String totalFormateado = format.format(listaCompras.get(position).getTotal());

        holder.ViewTotal.setText(holder.itemView.getContext().getString(R.string.TOTAL) +" "+totalFormateado );

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                Compras compras = listaCompras.get(position);
                clickListener.onCitaClick(compras);
            }
        });


    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaCompras.clear();
            listaCompras.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Compras> coleccion = listaOriginal.stream()
                        .filter(i -> i.getCliente().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaCompras.clear();
                listaCompras.addAll(coleccion);
            } else {
                listaCompras.clear();
                for (Compras c : listaOriginal) {
                    if (c.getCliente().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaCompras.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCompras(ArrayList<Compras> compras) {
        // Invierte el orden de las citas para mostrar las más recientes primero
        Collections.reverse(compras);

        listaCompras = compras;
        listaOriginal.clear();
        listaOriginal.addAll(compras);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }

    public class ComprasViewHolder extends RecyclerView.ViewHolder {

        TextView viewFolio, ViewFecha, ViewHora, ViewCliente, ViewTotal;

        public ComprasViewHolder(@NonNull View itemView) {
            super(itemView);

            viewFolio = itemView.findViewById(R.id.ViewFolio);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewCliente = itemView.findViewById(R.id.ViewCliente);
            ViewTotal = itemView.findViewById(R.id.ViewTotal);
        }
    }
}