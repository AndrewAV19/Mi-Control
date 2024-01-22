package com.example.micontrol;

public class ColorModel {
    private String colorName;
    private int startColorRes;
    private int centerColorRes;
    private int endColorRes;

    public ColorModel(String colorName, int startColorRes, int centerColorRes, int endColorRes) {
        this.colorName = colorName;
        this.startColorRes = startColorRes;
        this.centerColorRes = centerColorRes;
        this.endColorRes = endColorRes;
    }

    public String getColorName() {
        return colorName;
    }

    public int getStartColorRes() {
        return startColorRes;
    }

    public int getCenterColorRes() {
        return centerColorRes;
    }

    public int getEndColorRes() {
        return endColorRes;
    }
}
