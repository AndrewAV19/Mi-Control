package com.example.micontrol.adaptadores;

import android.content.Context;
import android.content.Intent;
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
import com.example.micontrol.VerTaller;
import com.example.micontrol.entidades.Talleres;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaTalleresAdapter1 extends RecyclerView.Adapter<ListaTalleresAdapter1.TalleresViewHolder> {

    ArrayList<Talleres> listaTalleres;
    ArrayList<Talleres> listaOriginal;

    public ListaTalleresAdapter1(ArrayList<Talleres> listaTalleres) {
        this.listaTalleres = listaTalleres;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaTalleres);
    }

    @NonNull
    @Override
    public TalleresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_talleres, parent, false);
        return new TalleresViewHolder(view);
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaTalleres.clear();
            listaTalleres.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Talleres> coleccion = listaOriginal.stream()
                        .filter(i -> i.getTitulo().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaTalleres.clear();
                listaTalleres.addAll(coleccion);
            } else {
                listaTalleres.clear();
                for (Talleres t : listaOriginal) {
                    if (t.getTitulo().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaTalleres.add(t);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setCitas(ArrayList<Talleres> talleres) {
        // Invierte el orden de las citas para mostrar las más recientes primero
        listaTalleres = talleres;
        listaOriginal.clear();
        listaOriginal.addAll(talleres);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TalleresViewHolder holder, int position) {
        Talleres taller = listaTalleres.get(position);
        holder.ViewTitulo.setText(taller.getTitulo());
        //holder.ViewInformacion.setText("Informacion: " + taller.getInformacion());
        //holder.ViewPrecio.setText("Precio: $" + taller.getPrecio());

        // Utilizar Glide para cargar la imagen y aplicar una transformación para hacerla cuadrada
        Glide.with(holder.itemView)
                .load(taller.getImagentaller())
                .apply(RequestOptions.overrideOf(holder.imageViewTaller.getWidth(), holder.imageViewTaller.getHeight()))
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageViewTaller);
    }


    @Override
    public int getItemCount() {
        return listaTalleres.size();
    }

    public class TalleresViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewTaller;
        TextView ViewTitulo, ViewInformacion, ViewPrecio;

        public TalleresViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewTaller = itemView.findViewById(R.id.imageViewTaller);
            ViewTitulo = itemView.findViewById(R.id.ViewTitulo);
            //ViewInformacion = itemView.findViewById(R.id.ViewInformacion);
            //ViewPrecio = itemView.findViewById(R.id.ViewPrecio);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VerTaller.class);
                    intent.putExtra("ID",listaTalleres.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}