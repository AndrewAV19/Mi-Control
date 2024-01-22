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
import com.example.micontrol.VerCita;
import com.example.micontrol.entidades.Citas;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaCitasAdapter1 extends RecyclerView.Adapter<ListaCitasAdapter1.CitasViewHolder1> {

    ArrayList<Citas> listaCitas;
    ArrayList<Citas> listaOriginal;

    public ListaCitasAdapter1(ArrayList<Citas> listaCitas){
        this.listaCitas = listaCitas;
        //Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaCitas);
    }

    @NonNull
    @Override
    public CitasViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_citas,null,false);
        return new CitasViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasViewHolder1 holder, int position) {
        holder.viewNombre.setText(holder.itemView.getContext().getString(R.string.NOMBRE) +" "+ listaCitas.get(position).getNombre());
        holder.ViewTelefono.setText(holder.itemView.getContext().getString(R.string.TELEFONO) +" "+ listaCitas.get(position).getTelefono());
        holder.ViewFecha.setText(holder.itemView.getContext().getString(R.string.FECHA) +" "+ listaCitas.get(position).getFecha());
        holder.ViewHora.setText(holder.itemView.getContext().getString(R.string.HORA) +" "+ listaCitas.get(position).getHora());
        holder.ViewServicio.setText(holder.itemView.getContext().getString(R.string.SERVICIO) +" "+ listaCitas.get(position).getServicio());
        //holder.ViewServicio.setText(holder.itemView.getContext().getString(R.string.infoAdicionalConDosPuntos) +" "+ listaCitas.get(position).getInformacion_adicional());

    }

    public void filtrado(String txtBuscar){
        int longitud = txtBuscar.length();
        if (longitud == 0){
            listaCitas.clear();
            listaCitas.addAll(listaOriginal);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Citas> coleccion = listaCitas.stream()
                        .filter(i -> i.getNombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaCitas.clear();
                listaCitas.addAll(coleccion);
            }else {
                for (Citas c : listaOriginal){
                    if (c.getNombre().toLowerCase().contains(txtBuscar.toLowerCase())){
                        listaCitas.add(c);
                    }
                }
            }
            }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class CitasViewHolder1 extends RecyclerView.ViewHolder{

        TextView viewNombre, ViewTelefono, ViewFecha, ViewHora, ViewServicio,ViewInformacion_adicional;
        public CitasViewHolder1(@NonNull View itemView) {
            super(itemView);

            //ViewInformacion_adicional = itemView.findViewById(R.id.ViewInformacion_adicional);
            viewNombre = itemView.findViewById(R.id.viewNombre);
            ViewTelefono = itemView.findViewById(R.id.ViewTelefono);
            ViewFecha = itemView.findViewById(R.id.ViewFecha);
            ViewHora = itemView.findViewById(R.id.ViewHora);
            ViewServicio = itemView.findViewById(R.id.ViewServicio);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context,VerCita.class);
                    intent.putExtra("ID",listaCitas.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

        }
    }
}
