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
import com.example.micontrol.entidades.Talleres;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DbTalleres extends DbHelper{

    Context context;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInBytes;

    public DbTalleres(@Nullable Context context){
        super(context);
        this.context = context;
    }

    public void storeData(Talleres talleres) {
        SQLiteDatabase db = this.getWritableDatabase();

        byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap resizedImage = Bitmap.createScaledBitmap(talleres.getImagentaller(), 1000, 1000, false);
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInBytes = byteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put("imagen", imageInBytes);
        contentValues.put("titulo", talleres.getTitulo());
        contentValues.put("informacion", talleres.getInformacion());
        contentValues.put("precio", talleres.getPrecio());

        long checkIfQueryRun = db.insert(TABLE_TALLERES, null, contentValues);
        if (checkIfQueryRun > 0) {
            Toast.makeText(context.getApplicationContext(), R.string.TallerCreadoCorrectamente, Toast.LENGTH_LONG).show();
            db.close();
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.ErrorAlCrearTaller, Toast.LENGTH_LONG).show();
        }
    }

    //Aqui esta lo agregado
    public ArrayList<String> getServiceNames() {
        ArrayList<String> serviceNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT titulo FROM " + TABLE_TALLERES + " ORDER BY titulo COLLATE NOCASE ASC", null);

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

        Cursor cursor = db.rawQuery("SELECT informacion FROM " + TABLE_TALLERES + " WHERE id = " + id, null);
        String serviceName = "";

        if (cursor.moveToFirst()) {
            serviceName = cursor.getString(0);
        }

        cursor.close();

        return serviceName;
    }

    public int getServiceIdByName(String serviceName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_TALLERES + " WHERE titulo = ?", new String[]{serviceName});
        int serviceId = -1;

        if (cursor.moveToFirst()) {
            serviceId = cursor.getInt(0);
        }

        cursor.close();

        return serviceId;
    }


    public ArrayList<Talleres> mostrarHistorialTalleres() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Talleres> listaTalleres = new ArrayList<>();
        Talleres talleres = null;
        Cursor cursorTalleres = null;

        cursorTalleres = db.rawQuery("SELECT * FROM " + TABLE_TALLERES + " ORDER BY titulo COLLATE NOCASE ASC", null);

        if (cursorTalleres.moveToFirst()) {
            do {
                talleres = new Talleres(null,null, null, 0);
                talleres.setId(cursorTalleres.getInt(0));
                talleres.setImagentaller(BitmapFactory.decodeByteArray(cursorTalleres.getBlob(1), 0, cursorTalleres.getBlob(1).length));
                talleres.setTitulo(cursorTalleres.getString(2));
                talleres.setInformacion(cursorTalleres.getString(3));
                talleres.setPrecio(cursorTalleres.getDouble(4));
                listaTalleres.add(talleres);
            } while (cursorTalleres.moveToNext());
        }
        cursorTalleres.close();

        return listaTalleres;
    }



    //Ahora para ver el Taller
    public Talleres verTalleres(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Talleres talleres = null;
        Cursor cursorTalleres = null;

        cursorTalleres = db.rawQuery("SELECT * FROM " + TABLE_TALLERES + " WHERE id = "+ id, null);

        if (cursorTalleres.moveToFirst()){
            talleres = new Talleres(null,null, null, 0);
            talleres.setId(cursorTalleres.getInt(0));
            talleres.setImagentaller(BitmapFactory.decodeByteArray(cursorTalleres.getBlob(1), 0, cursorTalleres.getBlob(1).length));
            talleres.setTitulo(cursorTalleres.getString(2));
            talleres.setInformacion(cursorTalleres.getString(3));
            talleres.setPrecio(cursorTalleres.getDouble(4));
        }
        cursorTalleres.close();

        return talleres;
    }

    public boolean editarTaller(int id, Bitmap imagen, String titulo, String informacion, double precio) {
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

            db.execSQL("UPDATE " + TABLE_TALLERES + " SET imagen = ?, titulo = ?, informacion = ?, precio = ? WHERE id = ?",
                    new Object[]{byteArray, titulo, informacion, precio, id});
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
    public boolean eliminarTaller(int id){

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_TALLERES + " WHERE id = '" + id + "'");
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
