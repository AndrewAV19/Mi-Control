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
import com.example.micontrol.VerProducto;
import com.example.micontrol.entidades.Productos;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaProductosAdapter1 extends RecyclerView.Adapter<ListaProductosAdapter1.ProductosViewHolder> {

    ArrayList<Productos> listaProductos;
    ArrayList<Productos> listaOriginal;

    public ListaProductosAdapter1(ArrayList<Productos> listaProductos) {
        this.listaProductos = listaProductos;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaProductos);
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_productos, parent, false);
        return new ProductosViewHolder(view);
    }

    public void filtrado(String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {
            listaProductos.clear();
            listaProductos.addAll(listaOriginal);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                List<Productos> coleccion = listaOriginal.stream()
                        .filter(i -> i.getProducto().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                listaProductos.clear();
                listaProductos.addAll(coleccion);
            } else {
                listaProductos.clear();
                for (Productos s : listaOriginal) {
                    if (s.getProducto().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaProductos.add(s);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setVentas(ArrayList<Productos> productos) {
        // Invierte el orden de las ventas para mostrar las más recientes primero
        listaProductos = productos;
        listaOriginal.clear();
        listaOriginal.addAll(productos);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        Productos productos = listaProductos.get(position);
        holder.ViewFolio.setText(holder.itemView.getContext().getString(R.string.CODIGO) +" "+ productos.getId());
        holder.ViewProducto.setText(holder.itemView.getContext().getString(R.string.PRODUCTO) +" "+ productos.getProducto());

        // Obtener las cadenas de los recursos
        String language = holder.itemView.getContext().getString(R.string.LENGUAJE);
        String country = holder.itemView.getContext().getString(R.string.PAIS);

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale(language, country));

        String precioFormateado = format.format(productos.getPrecio());
        String precioCompraFormateado = format.format(productos.getPrecio_compra());
        String ganancia = format.format(productos.getGanancia());

        holder.ViewPrecio.setText(holder.itemView.getContext().getString(R.string.Pre) +" "+ precioFormateado);
        holder.ViewPrecioCompra.setText(holder.itemView.getContext().getString(R.string.PreCOM) +" "+ precioCompraFormateado);
        holder.ViewGanancia.setText(holder.itemView.getContext().getString(R.string.GANANCIA) +" "+ ganancia);
        holder.ViewDescuento.setText(holder.itemView.getContext().getString(R.string.Desc) +" "+ productos.getDescuento()+"%");
        holder.ViewCantidad.setText(holder.itemView.getContext().getString(R.string.CANTIDADALMACEN) +" "+ productos.getCantidad());

        // Utilizar Glide para cargar la imagen y aplicar una transformación para hacerla cuadrada
        Glide.with(holder.itemView)
                .load(productos.getImagenproducto())
                .apply(RequestOptions.overrideOf(holder.imageViewProducto.getWidth(), holder.imageViewProducto.getHeight()))
                .apply(RequestOptions.centerCropTransform())
                .into(holder.imageViewProducto);
    }


    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProducto;
        TextView ViewFolio,ViewProducto, ViewPrecio,ViewPrecioCompra,ViewGanancia, ViewCantidad, ViewDescuento;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            ViewDescuento = itemView.findViewById(R.id.ViewDescuento);
            ViewFolio = itemView.findViewById(R.id.ViewFolio);
            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
            ViewProducto = itemView.findViewById(R.id.ViewProducto);
            ViewPrecio = itemView.findViewById(R.id.ViewPrecio);
            ViewPrecioCompra = itemView.findViewById(R.id.ViewPrecioCompra);
            ViewGanancia = itemView.findViewById(R.id.ViewGanancia);
            ViewCantidad = itemView.findViewById(R.id.ViewCantidad);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, VerProducto.class);
                    intent.putExtra("ID",listaProductos.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}