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
import com.example.micontrol.entidades.Productos;
import com.example.micontrol.entidades.Ventas2;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ListaProductosAdapter extends RecyclerView.Adapter<ListaProductosAdapter.ProductosViewHolder> {

    ArrayList<Productos> listaProductos;
    ArrayList<Productos> listaOriginal;
    private HashMap<Long, Integer> productoCantidadMap = new HashMap<>();

    public ListaProductosAdapter(ArrayList<Productos> listaProductos) {
        this.listaProductos = listaProductos;
        // Para buscar
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaProductos);
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_productos2, parent, false);
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

    public void actualizarCantidadProductosVendidos(List<Ventas2> listaVentas) {
        productoCantidadMap.clear();

        // Recorremos las ventas y actualizamos la cantidad de productos vendidos por producto
        for (Ventas2 venta : listaVentas) {
            long productoId = venta.getProducto_id();
            int cantidadVendida = venta.getCantidad();

            // Si ya existe una cantidad asociada al producto, la actualizamos sumando la cantidad vendida
            if (productoCantidadMap.containsKey(productoId)) {
                int cantidadActual = productoCantidadMap.get(productoId);
                productoCantidadMap.put(productoId, cantidadActual + cantidadVendida);
            } else {
                // Si no existe, simplemente agregamos el producto con la cantidad vendida
                productoCantidadMap.put(productoId, cantidadVendida);
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

        holder.ViewProducto.setText(holder.itemView.getContext().getString(R.string.PRODUCTO) + " " + productos.getProducto());

        // Obtener el ID del producto actual
        long productoId = productos.getId();

        // Verificar si hay información de cantidad vendida para este producto
        if (productoCantidadMap.containsKey(productoId)) {
            int cantidadVendida = productoCantidadMap.get(productoId);
            holder.ViewNumeroVentas.setText("Cantidad vendida: " + cantidadVendida);
        } else {
            // Si no hay información, mostramos que no se han vendido unidades
            holder.ViewNumeroVentas.setText("Cantidad vendida: 0");
        }

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
        TextView ViewFolio,ViewProducto, ViewPrecio,ViewPrecioCompra,ViewGanancia, ViewCantidad,ViewDescuento,ViewNumeroVentas ;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);



            imageViewProducto = itemView.findViewById(R.id.imageViewProducto);
            ViewProducto = itemView.findViewById(R.id.ViewProducto);
            ViewNumeroVentas = itemView.findViewById(R.id.ViewNumeroVentas);

        }
    }
}