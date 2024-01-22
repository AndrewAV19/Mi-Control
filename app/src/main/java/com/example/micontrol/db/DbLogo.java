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

public class DbLogo extends DbHelper {

    Context context;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInBytes;


    public DbLogo(@Nullable Context context) {
        super(context);
        this.context = context;

        createDefaultLogo();
    }




    public boolean editarLogo(Logo logo) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Convertir la imagen del logo a un array de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            logo.getImagenservicio().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesImagen = byteArrayOutputStream.toByteArray();

            ContentValues contentValues = new ContentValues();
            contentValues.put("nombre", logo.getNombre());
            contentValues.put("imagen", bytesImagen);

            int filasActualizadas = db.update(TABLE_LOGO, contentValues, "id = ?", new String[]{String.valueOf(logo.getId())});
            return filasActualizadas > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }


    public Bitmap getPredeterminedImageFromDrawable() {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
    }

    private void createDefaultLogo() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOGO + " WHERE id = 1", null);
        if (!cursor.moveToFirst()) {
            // El logo predeterminado no existe, agregarlo a la tabla
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", 1);
            contentValues.put("nombre", context.getString(R.string.app_name));
            Bitmap logoDefault = getPredeterminedImageFromDrawable(); // Obtener el logo predeterminado desde los recursos
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            logoDefault.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            contentValues.put("imagen", byteArrayOutputStream.toByteArray());

            db.insert(TABLE_LOGO, null, contentValues);
        }
        cursor.close();
    }

    public Logo getLogo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOGO, null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
            byte[] bytesImagen = cursor.getBlob(cursor.getColumnIndex("imagen"));
            Bitmap imagen = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.length);

            Logo logo = new Logo();
            logo.setId(id);
            logo.setNombre(nombre);
            logo.setImagenservicio(imagen);
            return logo;
        }
        cursor.close();
        return null; // Devolver null si no se encuentra el logo con el id dado en la base de datos
    }

}