package com.example.micontrol;

import android.graphics.Bitmap;

public class LogoChangeEvent {
    private String nombreLogo;
    private Bitmap imagenLogo;

    public LogoChangeEvent(String nombreLogo, Bitmap imagenLogo) {
        this.nombreLogo = nombreLogo;
        this.imagenLogo = imagenLogo;
    }

    public String getNombreLogo() {
        return nombreLogo;
    }

    public Bitmap getImagenLogo() {
        return imagenLogo;
    }
}
