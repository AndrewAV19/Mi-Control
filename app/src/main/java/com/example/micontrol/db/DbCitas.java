package com.example.micontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.micontrol.entidades.Citas;

import java.util.ArrayList;

public class DbCitas extends DbHelper{

    Context context;

    public DbCitas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public ArrayList<Citas> obtenerCitasPorFecha(String fecha) {
        ArrayList<Citas> citasPorFecha = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_citas.*, t_servicios.servicio FROM t_citas " +
                "INNER JOIN t_servicios ON t_citas.id_servicio = t_servicios.id " +
                "WHERE fecha = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                Citas cita = new Citas();
                cita.setId(cursor.getInt(0));
                cita.setNombre(cursor.getString(1));
                cita.setTelefono(cursor.getString(2));
                cita.setFecha(cursor.getString(3));
                cita.setHora(cursor.getString(4));
                cita.setId_servicio(cursor.getInt(5));
                cita.setServicio(cursor.getString(cursor.getColumnIndex("servicio"))); // Obtener el nombre del servicio
                citasPorFecha.add(cita);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return citasPorFecha;
    }

    public long insertarCitas(String nombre, String telefono, String fecha, String hora, int id_servicio, String informacion_adicional) {
        long id = 0;

        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("telefono", telefono);
            values.put("fecha", fecha); // Guardar la fecha como un valor de tiempo (long)
            values.put("hora", hora);
            values.put("id_servicio", id_servicio);
            values.put("informacion_adicional", informacion_adicional);

            id = db.insert(TABLE_CITAS, null, values);
        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }


    public ArrayList<Citas> mostrarHistorialCitas() {
        ArrayList<Citas> listaCitas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_citas.id, t_citas.nombre, t_citas.telefono, t_citas.fecha, t_citas.hora, t_servicios.servicio, t_citas.informacion_adicional " +
                "FROM t_citas " +
                "JOIN t_servicios ON t_citas.id_servicio = t_servicios.id " +
                "ORDER BY fecha DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Citas cita = new Citas();
                cita.setId(cursor.getInt(0));
                cita.setNombre(cursor.getString(1));
                cita.setTelefono(cursor.getString(2));
                cita.setFecha(cursor.getString(3));
                cita.setHora(cursor.getString(4));
                cita.setServicio(cursor.getString(5));
                cita.setInformacion_adicional(cursor.getString(6));

                listaCitas.add(cita);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listaCitas;
    }



    //Ahora para ver la cita
    public Citas verCitas(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Citas citas = null;
        Cursor cursorCitas = null;

        cursorCitas = db.rawQuery("SELECT * FROM " + TABLE_CITAS + " WHERE id = "+ id, null);

        if (cursorCitas.moveToFirst()){
            citas = new Citas();
            citas.setId(cursorCitas.getInt(0));
            citas.setNombre(cursorCitas.getString(1));
            citas.setTelefono(cursorCitas.getString(2));
            citas.setFecha(cursorCitas.getString(3));
            citas.setHora(cursorCitas.getString(4));
            citas.setId_servicio(cursorCitas.getInt(5));
            citas.setInformacion_adicional(cursorCitas.getString(6));

            // Add this:
            int serviceId = citas.getId_servicio();

        }
        cursorCitas.close();

        return citas;
    }

    //Editar cita
    public boolean editarCitas(int id, String nombre, String telefono, String fecha, String hora, int id_servicio, String informacion_adicional) {
        boolean correcto = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_CITAS + " SET nombre = '" + nombre + "', telefono = '" + telefono + "', fecha = '" + fecha + "', hora = '" + hora + "', id_servicio = '" + id_servicio + "', informacion_adicional = '" + informacion_adicional + "' WHERE id = '" + id + "'");
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
    public boolean eliminarCitas(int id){

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_CITAS + " WHERE id = '" + id + "'");
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
