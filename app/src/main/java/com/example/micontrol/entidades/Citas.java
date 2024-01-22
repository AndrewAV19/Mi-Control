package com.example.micontrol.entidades;

public class Citas {

    private int id;
    private String nombre;
    private String telefono;
    private String fecha;
    private String hora;
    private int id_servicio; // Cambio el tipo de String a int
    private String servicio;
    private String informacion_adicional;

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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public int getId_servicio() { // Cambio el tipo de String a int
        return id_servicio;
    }

    public void setId_servicio(int id_servicio) { // Cambio el tipo de String a int
        this.id_servicio = id_servicio;
    }

    public String getServicio() {return servicio;}

    public void setServicio(String servicio) {this.servicio = servicio;}

    public String getInformacion_adicional() {return informacion_adicional;}

    public void setInformacion_adicional(String informacion_adicional) {this.informacion_adicional = informacion_adicional;}

}