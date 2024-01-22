package com.example.micontrol.entidades;

public class Compras2 {

    private int id;
    private long producto_id;
    private int folio_id;
    private int cantidad;

    private double total;

    private Productos producto;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(long producto_id) {
        this.producto_id = producto_id;
    }

    public int getFolio_id() {
        return folio_id;
    }
    public void setFolio_id(int folio_id) {
        this.folio_id = folio_id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }
}