package com.example.micontrol.entidades;

import android.graphics.Bitmap;

public class Logo {

    private Bitmap Imagenservicio;
    private String nombre;

    public Logo(Bitmap imagenservicio) {
        this.Imagenservicio = imagenservicio;

    }

    public Logo() {}

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Bitmap getImagenservicio() {
        return Imagenservicio;
    }

    public void setImagenservicio(Bitmap imagenservicio) {
        Imagenservicio = imagenservicio;
    }



}