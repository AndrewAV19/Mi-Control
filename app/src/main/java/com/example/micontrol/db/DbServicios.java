package com.example.micontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.micontrol.R;
import com.example.micontrol.entidades.Logo;
import com.example.micontrol.entidades.Servicios;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DbServicios extends DbHelper {

    Context context;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInBytes;

    public DbServicios(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public void storeData(Servicios servicios) {
        SQLiteDatabase db = this.getWritableDatabase();

        byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap resizedImage = Bitmap.createScaledBitmap(servicios.getImagenservicio(), 1000, 1000, false);
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInBytes = byteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put("imagen", imageInBytes);
        contentValues.put("servicio", servicios.getServicio());
        contentValues.put("precio", servicios.getPrecio());

        long checkIfQueryRun = db.insert(TABLE_SERVICIOS, null, contentValues);
        if (checkIfQueryRun > 0) {
            Toast.makeText(context.getApplicationContext(), R.string.ServicioCreadoCorrectamente, Toast.LENGTH_LONG).show();
            db.close();
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.ErrorAlAgregarServicio, Toast.LENGTH_LONG).show();
        }
    }

    //Aqui esta lo agregado
    public ArrayList<String> getServiceNames() {
        ArrayList<String> serviceNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT servicio FROM " + TABLE_SERVICIOS + " ORDER BY servicio COLLATE NOCASE ASC", null);

        if (cursor.moveToFirst()) {
            do {
                serviceNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return serviceNames;
    }

    //otro que se necesita
    public String getServiceNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT servicio FROM " + TABLE_SERVICIOS + " WHERE id = " + id, null);
        String serviceName = "";

        if (cursor.moveToFirst()) {
            serviceName = cursor.getString(0);
        }

        cursor.close();

        return serviceName;
    }

    public int getServiceIdByName(String serviceName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_SERVICIOS + " WHERE servicio = ?", new String[]{serviceName});
        int serviceId = -1;

        if (cursor.moveToFirst()) {
            serviceId = cursor.getInt(0);
        }

        cursor.close();

        return serviceId;
    }


    public ArrayList<Servicios> mostrarHistorialServicios() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Servicios> listaServicios = new ArrayList<>();
        Servicios servicios = null;
        Cursor cursorServicios = null;

        cursorServicios = db.rawQuery("SELECT * FROM " + TABLE_SERVICIOS + " ORDER BY servicio COLLATE NOCASE ASC", null);

        if (cursorServicios.moveToFirst()) {
            do {
                servicios = new Servicios(null, null, 0);
                servicios.setId(cursorServicios.getInt(0));
                servicios.setImagenservicio(BitmapFactory.decodeByteArray(cursorServicios.getBlob(1), 0, cursorServicios.getBlob(1).length));
                servicios.setServicio(cursorServicios.getString(2));
                servicios.setPrecio(cursorServicios.getDouble(3));
                listaServicios.add(servicios);
            } while (cursorServicios.moveToNext());
        }
        cursorServicios.close();

        return listaServicios;
    }



    //Ahora para ver el servicio
    public Servicios verServicios(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Servicios servicios = null;
        Cursor cursorServicios = null;

        cursorServicios = db.rawQuery("SELECT * FROM " + TABLE_SERVICIOS + " WHERE id = "+ id, null);

        if (cursorServicios.moveToFirst()){
            servicios = new Servicios(null, null, 0);
            servicios.setId(cursorServicios.getInt(0));
            servicios.setImagenservicio(BitmapFactory.decodeByteArray(cursorServicios.getBlob(1), 0, cursorServicios.getBlob(1).length));
            servicios.setServicio(cursorServicios.getString(2));
            servicios.setPrecio(cursorServicios.getDouble(3));
        }
        cursorServicios.close();

        return servicios;
    }

    public boolean editarServicio(int id, Bitmap imagen, String servicio, double precio) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Redimensionar la imagen antes de almacenarla en la base de datos
            Bitmap resizedImage = Bitmap.createScaledBitmap(imagen, 1000, 1000, false);

            // Convertir la imagen redimensionada a un array de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            db.execSQL("UPDATE " + TABLE_SERVICIOS + " SET imagen = ?, servicio = ?, precio = ? WHERE id = ?",
                    new Object[]{byteArray, servicio, precio, id});
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }


    //Eliminar
    public boolean eliminarServicio(int id){

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_SERVICIOS + " WHERE id = '" + id + "'");
            correcto = true;
        }catch (Exception ex){
            ex.toString();
            correcto = false;
        }finally {
            db.close();
        }

        return correcto;
    }




}