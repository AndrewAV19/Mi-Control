package com.example.micontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.micontrol.entidades.Compras;
import com.example.micontrol.entidades.Compras2;
import com.example.micontrol.entidades.Productos;

import java.util.ArrayList;

public class DbCompras extends DbHelper {

    Context context;

    public DbCompras(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public boolean eliminarCompra2(int folio_id) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_COMPRAS2 + " WHERE folio_id = " + folio_id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }






    public ArrayList<Compras2> obtenerCompras2PorIdCompra(int idCompra) {
        ArrayList<Compras2> compras2List = new ArrayList<>();
        String query = "SELECT " + TABLE_COMPRAS2 + ".id2, " + TABLE_COMPRAS2 + ".folio_id, " +
                TABLE_COMPRAS2 + ".producto_id, " + TABLE_COMPRAS2 + ".cantidad, " +
                TABLE_INVENTARIO + ".precio_compra, " +
                TABLE_INVENTARIO + ".producto " +
                "FROM " + TABLE_COMPRAS2 +
                " INNER JOIN " + TABLE_INVENTARIO +
                " ON " + TABLE_COMPRAS2 + ".producto_id = " + TABLE_INVENTARIO + ".id " +
                "WHERE " + TABLE_COMPRAS2 + ".folio_id = " + idCompra;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Compras2 compra2 = new Compras2();
                compra2.setId(cursor.getInt(0));
                compra2.setFolio_id(cursor.getInt(1));
                compra2.setProducto_id(cursor.getLong(2));
                compra2.setCantidad(cursor.getInt(3));

                Productos producto = new Productos();
                producto.setPrecio_compra(cursor.getDouble(4));
                producto.setProducto(cursor.getString(5));

                compra2.setProducto(producto);
                compras2List.add(compra2);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return compras2List;
    }


    public ArrayList<Compras> obtenerComprasPorFecha(String fecha) {
        ArrayList<Compras> comprasPorFecha = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_compras.* FROM t_compras " +
                "WHERE fecha = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                Compras compra = new Compras();
                compra.setId(cursor.getInt(0));
                compra.setFecha(cursor.getString(1));
                compra.setHora(cursor.getString(2));
                compra.setCliente(cursor.getString(3));
                compra.setRecibido(cursor.getDouble(4));
                compra.setCambio(cursor.getDouble(5));
                compra.setTotal(cursor.getDouble(6));
                comprasPorFecha.add(compra);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return comprasPorFecha;
    }


    public ArrayList<Compras> mostrarHistorialCompras() {
        ArrayList<Compras> listaCompras = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_compras.id, t_compras.fecha, t_compras.hora, t_compras.cliente, t_compras.recibido, t_compras.cambio, t_compras.total " +
                "FROM t_compras " +
                "ORDER BY t_compras.id DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Compras compra = new Compras();
                compra.setId(cursor.getInt(0));
                compra.setFecha(cursor.getString(1));
                compra.setHora(cursor.getString(2));
                compra.setCliente(cursor.getString(3));
                compra.setRecibido(cursor.getDouble(4));
                compra.setCambio(cursor.getDouble(5));
                compra.setTotal(cursor.getDouble(6));

                listaCompras.add(compra);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listaCompras;
    }

    public Compras verCompra(int id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Compras compra = null;
        Cursor cursorCompra = null;

        cursorCompra = db.rawQuery("SELECT * FROM " + TABLE_COMPRAS + " WHERE id = " + id, null);

        if (cursorCompra.moveToFirst()) {
            compra = new Compras();
            compra.setId(cursorCompra.getInt(0));
            compra.setFecha(cursorCompra.getString(1));
            compra.setHora(cursorCompra.getString(2));
            compra.setCliente(cursorCompra.getString(3));
            compra.setRecibido(cursorCompra.getDouble(4));
            compra.setCambio(cursorCompra.getDouble(5));
            compra.setTotal(cursorCompra.getDouble(6));
        }
        cursorCompra.close();

        return compra;
    }

    public boolean editarCompra(int id, String cliente, double recibido, double cambio, double total) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_COMPRAS + " SET cliente = '" + cliente + "', recibido = " + recibido + ", cambio = " + cambio + ", total = " + total + " WHERE id = " + id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }


    public boolean eliminarCompra(int id) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_COMPRAS + " WHERE id = " + id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }
}
