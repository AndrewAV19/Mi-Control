package com.example.micontrol.entidades;

import android.graphics.Bitmap;

public class Servicios {

    private Bitmap Imagenservicio;
    private String servicio;
    private double precio;

    public Servicios(Bitmap imagenservicio, String servicio, double precio) {
        this.Imagenservicio = imagenservicio;
        this.servicio = servicio;
        this.precio = precio;
    }

    public Servicios() {}

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImagenservicio() {
        return Imagenservicio;
    }

    public void setImagenservicio(Bitmap imagenservicio) {
        Imagenservicio = imagenservicio;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }


}