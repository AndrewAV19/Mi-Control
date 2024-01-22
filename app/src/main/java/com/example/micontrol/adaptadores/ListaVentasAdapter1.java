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
import com.example.micontrol.VerVenta;
import com.example.micontrol.entidades.Ventas;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaVentasAdapter1 extends RecyclerView.Adapter<ListaVentasAdapter1.VentasViewHolder1> {

    ArrayList<Ventas> listaVentas;
    ArrayList<Ventas> listaOriginal;

    public ListaVentasAdapter1(ArrayList<Ventas> listaVentas){
        this.listaVentas = listaVentas;
        //Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaVentas);
    }

    @NonNull
    @Override
    public VentasViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_ventas,null,false);
        return new VentasViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VentasViewHolder1 holder, int position) {
        holder.ViewFolio.setText(holder.itemView.getContext().getString(R.string.folio) +" "+ listaVentas.get(position).getId());
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
    }

    public void filtrado(String txtBuscar){
        int longitud = txtBuscar.length();
        if (longitud == 0){
            listaVentas.clear();
            listaVentas.addAll(listaOriginal);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Ventas> coleccion = listaVentas.stream()
                        .filter(i -> i.getCliente().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaVentas.clear();
                listaVentas.addAll(coleccion);
            }else {
                for (Ventas c : listaOriginal){
                    if (c.getCliente().toLowerCase().contains(txtBuscar.toLowerCase())){
                        listaVentas.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaVentas.size();
    }

    public class VentasViewHolder1 extends RecyclerView.ViewHolder{

        TextView ViewFolio, ViewFecha, ViewHora, ViewCliente, ViewTotal;
        public VentasViewHolder1(@NonNull View itemView) {
            super(itemView);

            ViewFolio = itemView.findViewById(R.id.ViewFolio);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewCliente = itemView.findViewById(R.id.ViewCliente);
            ViewTotal = itemView.findViewById(R.id.ViewTotal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context,VerVenta.class);
                    intent.putExtra("ID",listaVentas.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}