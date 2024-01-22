package com.example.micontrol.entidades;

import java.util.List;

public class Ventas {

    private int id;
    private String fecha;
    private String hora;
    private String cliente;
    private double recibido;
    private double cambio;
    private double ganancias;
    private double total;
    private String producto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setRecibido(double recibido) {
        this.recibido = recibido;
    }

    public double getRecibido() {
        return recibido;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public double getCambio() {
        return cambio;
    }

    public void setGanancias(double ganancias) {
        this.ganancias = ganancias;
    }

    public double getGanancias() {
        return ganancias;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

}