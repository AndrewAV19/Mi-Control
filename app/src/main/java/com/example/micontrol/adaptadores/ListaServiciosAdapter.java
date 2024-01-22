package com.example.micontrol.adaptadores;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.micontrol.R;
import com.example.micontrol.entidades.Servicios;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaServiciosAdapter extends RecyclerView.Adapter<ListaServiciosAdapter.ServiciosViewHolder> {

    ArrayList<Servicios> listaServicios;
    ArrayList<Servicios> listaOriginal;

    public ListaServiciosAdapter(ArrayList<Servicios> listaServicios) {
        this.listaServicios = listaServicios;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaServicios);
    }

    @NonNull
    @Override
    public ServiciosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_servicios, parent, false);
        return new ServiciosViewHolder(view);
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaServicios.clear();
            listaServicios.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Servicios> coleccion = listaOriginal.stream()
                        .filter(i -> i.getServicio().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaServicios.clear();
                listaServicios.addAll(coleccion);
            } else {
                listaServicios.clear();
                for (Servicios s : listaOriginal) {
                    if (s.getServicio().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaServicios.add(s);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCitas(ArrayList<Servicios> servicios) {
        // Invierte el orden de las citas para mostrar las más recientes primero
        listaServicios = servicios;
        listaOriginal.clear();
        listaOriginal.addAll(servicios);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ServiciosViewHolder holder, int position) {
        Servicios servicio = listaServicios.get(position);
        holder.ViewServicio.setText(holder.itemView.getContext().getString(R.string.Ser) +" "+  servicio.getServicio());
        // Formatear el precio como una cantidad de dinero en pesos
        // Obtener las cadenas de los recursos
        String language = holder.itemView.getContext().getString(R.string.LENGUAJE);
        String country = holder.itemView.getContext().getString(R.string.PAIS);

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(language, country));
        String precioFormateado = format.format(servicio.getPrecio());

        holder.ViewPrecio.setText(holder.itemView.getContext().getString(R.string.PR) +" "+  precioFormateado);


        // Utilizar Glide para cargar la imagen y aplicar una transformación para hacerla cuadrada
        Glide.with(holder.itemView)
                .load(servicio.getImagenservicio())
                .apply(RequestOptions.overrideOf(holder.imageViewServicio.getWidth(), holder.imageViewServicio.getHeight()))
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageViewServicio);
    }


    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    public class ServiciosViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewServicio;
        TextView ViewServicio, ViewPrecio;

        public ServiciosViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewServicio = itemView.findViewById(R.id.imageViewServicio);
            ViewServicio = itemView.findViewById(R.id.ViewServicio);
            ViewPrecio = itemView.findViewById(R.id.ViewPrecio);
        }
    }
}