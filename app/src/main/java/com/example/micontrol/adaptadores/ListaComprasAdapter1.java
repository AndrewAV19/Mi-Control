package com.example.micontrol.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.R;
import com.example.micontrol.VerCompra;
import com.example.micontrol.entidades.Compras;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaComprasAdapter1 extends RecyclerView.Adapter<ListaComprasAdapter1.ComprasViewHolder1> {

    ArrayList<Compras> listaCompras;
    ArrayList<Compras> listaOriginal;

    public ListaComprasAdapter1(ArrayList<Compras> listaCompras) {
        this.listaCompras = listaCompras;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCompras);
    }

    @NonNull
    @Override
    public ComprasViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_compras, null, false);
        return new ComprasViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComprasViewHolder1 holder, int position) {
        holder.ViewFolio.setText(holder.itemView.getContext().getString(R.string.folio) + " " + listaCompras.get(position).getId());
        holder.ViewFecha.setText(holder.itemView.getContext().getString(R.string.FECHA) + " " + listaCompras.get(position).getFecha());
        holder.ViewHora.setText(holder.itemView.getContext().getString(R.string.HORA) + " " + listaCompras.get(position).getHora());
        holder.ViewProveedor.setText(holder.itemView.getContext().getString(R.string.Proveedor) + " " + listaCompras.get(position).getCliente());

        // Obtener las cadenas de los recursos
        String language = holder.itemView.getContext().getString(R.string.LENGUAJE);
        String country = holder.itemView.getContext().getString(R.string.PAIS);

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(language, country));

        String totalFormateado = format.format(listaCompras.get(position).getTotal());

        holder.ViewTotal.setText(holder.itemView.getContext().getString(R.string.TOTAL) + " " + totalFormateado);
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaCompras.clear();
            listaCompras.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Compras> coleccion = listaCompras.stream()
                        .filter(i -> i.getCliente().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaCompras.clear();
                listaCompras.addAll(coleccion);
            } else {
                for (Compras c : listaOriginal) {
                    if (c.getCliente().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaCompras.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }

    public class ComprasViewHolder1 extends RecyclerView.ViewHolder {

        TextView ViewFolio, ViewFecha, ViewHora, ViewProveedor, ViewTotal;

        public ComprasViewHolder1(@NonNull View itemView) {
            super(itemView);

            ViewFolio = itemView.findViewById(R.id.ViewFolio);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewProveedor = itemView.findViewById(R.id.ViewCliente);
            ViewTotal = itemView.findViewById(R.id.ViewTotal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VerCompra.class);
                    intent.putExtra("ID", listaCompras.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}
