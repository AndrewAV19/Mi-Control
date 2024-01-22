package com.example.micontrol.entidades;

import android.graphics.Bitmap;

public class Talleres {

    private Bitmap Imagentaller;
    private String titulo;
    private String informacion;
    private double precio;

    public Talleres(Bitmap imagentaller, String titulo, String informacion, double precio) {
        this.Imagentaller = imagentaller;
        this.titulo = titulo;
        this.informacion = informacion;
        this.precio = precio;
    }

    public Talleres() {}

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImagentaller() {
        return Imagentaller;
    }

    public void setImagentaller(Bitmap imagentaller) {
        Imagentaller = imagentaller;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

}
