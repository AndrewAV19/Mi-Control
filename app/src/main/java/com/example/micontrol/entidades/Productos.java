package com.example.micontrol.entidades;

import android.graphics.Bitmap;

public class Productos {

    private Bitmap Imagenproducto;
    private String producto;
    private double precio;
    private double precio_compra;
    private double ganancia;
    private double descuento;
    private int cantidad;
    private long id;


    public void actualizarPrecioConDescuento() {
        if (descuento > 0) {
            double descuentoDecimal = descuento / 100.0; // Convertir el descuento a decimal (por ejemplo, 10% -> 0.10)
            double precioConDescuento = precio * (1 - descuentoDecimal);
            precio = precioConDescuento;

            // Actualizar la ganancia con el nuevo precio
            ganancia = precio - precio_compra;
        }
    }

    public Productos(long id,Bitmap imagenproducto, String producto, double precio, double precio_compra, double ganancia, double descuento, int cantidad) {
        this.id = id;
        this.Imagenproducto = imagenproducto;
        this.producto = producto;
        this.precio = precio;
        this.precio_compra = precio_compra;
        this.ganancia = ganancia;
        this.descuento = descuento;
        this.cantidad = cantidad;
    }

    public Productos() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getImagenproducto() {
        return Imagenproducto;
    }

    public void setImagenproducto(Bitmap imagenproducto) {
        Imagenproducto = imagenproducto;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_compra() {return precio_compra;}

    public void setPrecio_compra(double precio_compra) {
        this.precio_compra = precio_compra;
    }


    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }

}