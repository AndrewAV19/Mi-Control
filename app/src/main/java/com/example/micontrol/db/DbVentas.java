package com.example.micontrol.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.micontrol.entidades.Productos;
import com.example.micontrol.entidades.Ventas;
import com.example.micontrol.entidades.Ventas2;

import java.util.ArrayList;

public class DbVentas extends DbHelper {

    Context context;

    public DbVentas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public boolean eliminarVenta2(int folio_id) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_VENTAS2 + " WHERE folio_id = " + folio_id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }



    public ArrayList<Ventas2> obtenerVentas2PorIdVenta(int idVenta) {
        ArrayList<Ventas2> ventas2PorIdVenta = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t2.producto_id, t2.cantidad, t1.producto, t1.precio " +
                "FROM " + TABLE_VENTAS2 + " t2 " +
                "JOIN " + TABLE_INVENTARIO + " t1 ON t2.producto_id = t1.id " +
                "WHERE t2.folio_id = ?";
        String[] selectionArgs = {String.valueOf(idVenta)};
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                long productoId = cursor.getLong(cursor.getColumnIndex("producto_id"));
                int cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
                String nombreProducto = cursor.getString(cursor.getColumnIndex("producto"));
                double precioProducto = cursor.getDouble(cursor.getColumnIndex("precio"));

                Ventas2 venta2 = new Ventas2();
                venta2.setProducto_id(productoId);
                venta2.setCantidad(cantidad);

                Productos producto = new Productos();
                producto.setId(productoId);
                producto.setProducto(nombreProducto);
                producto.setPrecio(precioProducto);

                venta2.setProducto(producto);

                ventas2PorIdVenta.add(venta2);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return ventas2PorIdVenta;
    }



    public ArrayList<Ventas> obtenerVentasPorFecha(String fecha) {
        ArrayList<Ventas> ventasPorFecha = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_ventas.* FROM t_ventas " +
                "WHERE fecha = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fecha});

        if (cursor.moveToFirst()) {
            do {
                Ventas venta = new Ventas();
                venta.setId(cursor.getInt(0));
                venta.setFecha(cursor.getString(1));
                venta.setHora(cursor.getString(2));
                venta.setCliente(cursor.getString(3));
                venta.setRecibido(cursor.getDouble(4));
                venta.setCambio(cursor.getDouble(5));
                venta.setGanancias(cursor.getDouble(6));
                venta.setTotal(cursor.getDouble(7));
                ventasPorFecha.add(venta);
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();

        return ventasPorFecha;
    }



    public ArrayList<Ventas> mostrarHistorialVentas() {
        ArrayList<Ventas> listaVentas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT t_ventas.id, t_ventas.fecha, t_ventas.hora, t_ventas.cliente,t_ventas.recibido,t_ventas.cambio,t_ventas.ganancias, t_ventas.total " +
                "FROM t_ventas " +
                "ORDER BY t_ventas.id DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Ventas venta = new Ventas();
                venta.setId(cursor.getInt(0));
                venta.setFecha(cursor.getString(1));
                venta.setHora(cursor.getString(2));
                venta.setCliente(cursor.getString(3));
                venta.setRecibido(cursor.getDouble(4));
                venta.setCambio(cursor.getDouble(5));
                venta.setGanancias(cursor.getDouble(6));
                venta.setTotal(cursor.getDouble(7));

                listaVentas.add(venta);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listaVentas;
    }

    public Ventas verVenta(int id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Ventas venta = null;
        Cursor cursorVenta = null;

        cursorVenta = db.rawQuery("SELECT * FROM " + TABLE_VENTAS + " WHERE id = "+ id, null);

        if (cursorVenta.moveToFirst()){
            venta = new Ventas();
            venta.setId(cursorVenta.getInt(0));
            venta.setFecha(cursorVenta.getString(1));
            venta.setHora(cursorVenta.getString(2));
            venta.setCliente(cursorVenta.getString(3));
            venta.setRecibido(cursorVenta.getDouble(4));
            venta.setCambio(cursorVenta.getDouble(5));
            venta.setGanancias(cursorVenta.getDouble(6));
            venta.setTotal(cursorVenta.getDouble(7));


        }
        cursorVenta.close();

        return venta;
    }

    public boolean editarVenta(int id, String cliente, double recibido, double cambio, double ganancias, double total) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Modificar la consulta SQL para incluir el campo "ganancias"
            db.execSQL("UPDATE " + TABLE_VENTAS + " SET cliente = '" + cliente + "', recibido = " + recibido + ", cambio = " + cambio + ", ganancias = " + ganancias + ", total = " + total + " WHERE id = " + id);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }



    public boolean eliminarVenta(int id) {
        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_VENTAS + " WHERE id = " + id);
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
