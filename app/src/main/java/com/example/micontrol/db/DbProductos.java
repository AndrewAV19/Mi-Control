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
import com.example.micontrol.entidades.Productos;
import com.example.micontrol.entidades.Ventas2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbProductos extends DbHelper {

    Context context;
    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInBytes;

    public DbProductos(@Nullable Context context) {
        super(context);
        this.context = context;
    }



    //Codigo de barras
    public List<Productos> obtenerProductos() {
        List<Productos> productos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INVENTARIO, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex("id"));
                byte[] imagenBytes = cursor.getBlob(cursor.getColumnIndex("imagen"));
                Bitmap imagenProducto = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                String producto = cursor.getString(cursor.getColumnIndex("producto"));
                double precio = cursor.getDouble(cursor.getColumnIndex("precio"));
                double precioCompra = cursor.getDouble(cursor.getColumnIndex("precio_compra"));
                double ganancia = cursor.getDouble(cursor.getColumnIndex("ganancia"));
                double descuento = cursor.getDouble(cursor.getColumnIndex("descuento"));
                int cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"));
                Productos p = new Productos(id, imagenProducto, producto, precio, precioCompra, ganancia,descuento,cantidad);
                productos.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }

    //Aqui acaba codigo de barras

    public void disminuirCantidadProducto(String producto, int cantidad) {
        SQLiteDatabase db = getWritableDatabase();

        // Obtener la cantidad actual del producto
        int cantidadActual = getCantidadProducto(producto);

        // Calcular la nueva cantidad restando la cantidad seleccionada
        int nuevaCantidad = cantidadActual - cantidad;

        if (nuevaCantidad >= 0) {
            ContentValues values = new ContentValues();
            values.put("cantidad", nuevaCantidad);

            String whereClause = "producto=?";
            String[] whereArgs = {producto};

            db.update(TABLE_INVENTARIO, values, whereClause, whereArgs);
        }

        db.close();
    }

    public void aumentarCantidadProducto(String producto, int cantidad) {
        // Crear una instancia de la base de datos en modo escritura
        SQLiteDatabase db = getWritableDatabase();

        // Obtener la cantidad actual del producto
        int cantidadActual = getCantidadProducto(producto);

        // Calcular la nueva cantidad
        int nuevaCantidad = cantidadActual + cantidad;

        // Crear los valores a actualizar
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);

        // Actualizar la cantidad del producto en la base de datos
        String selection = "producto=?";
        String[] selectionArgs = {producto};
        int rowsAffected = db.update(TABLE_INVENTARIO, values, selection, selectionArgs);

        // Cerrar la base de datos
        db.close();

        if (rowsAffected > 0) {
            // La cantidad del producto se ha actualizado correctamente
            Toast.makeText(context, "Cantidad del producto actualizada", Toast.LENGTH_SHORT).show();
        } else {
            // Ocurrió un error al actualizar la cantidad del producto
            Toast.makeText(context, "Error al actualizar la cantidad del producto", Toast.LENGTH_SHORT).show();
        }
    }



    public int getCantidadProducto(String nombreProducto) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT cantidad FROM " + TABLE_INVENTARIO + " WHERE producto = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreProducto});
        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }

    public double getPrecioProducto(String nombreProducto) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT precio, descuento FROM " + TABLE_INVENTARIO + " WHERE producto = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreProducto});
        double precio = 0.0;
        double descuento = 0.0;

        if (cursor.moveToFirst()) {
            precio = cursor.getDouble(0);
            descuento = cursor.getDouble(1);
        }
        cursor.close();

        Productos producto = new Productos();
        producto.setPrecio(precio);
        producto.setDescuento(descuento);
        producto.actualizarPrecioConDescuento();

        return producto.getPrecio();
    }


    public double getPrecioCompraProducto(String nombreProducto) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT precio_compra FROM " + TABLE_INVENTARIO + " WHERE producto = ?";
        Cursor cursor = db.rawQuery(query, new String[]{nombreProducto});
        double precio_compra = 0.0;
        if (cursor.moveToFirst()) {
            precio_compra = cursor.getDouble(0);
        }
        cursor.close();
        return precio_compra;
    }



    public void storeData(Productos productos) {
        SQLiteDatabase db = this.getWritableDatabase();

        byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap resizedImage = Bitmap.createScaledBitmap(productos.getImagenproducto(), 1000, 1000, false);
        resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInBytes = byteArrayOutputStream.toByteArray();


        ContentValues contentValues = new ContentValues();
        contentValues.put("id", productos.getId());
        contentValues.put("imagen", imageInBytes);
        contentValues.put("producto", productos.getProducto());
        contentValues.put("precio", productos.getPrecio());
        contentValues.put("precio_compra", productos.getPrecio_compra());
        contentValues.put("ganancia", productos.getGanancia());
        contentValues.put("descuento", productos.getDescuento());
        contentValues.put("cantidad", productos.getCantidad());

        long checkIfQueryRun = db.insert(TABLE_INVENTARIO, null, contentValues);
        if (checkIfQueryRun > 0) {
            Toast.makeText(context.getApplicationContext(), R.string.ProductoCreadoCorrectamente, Toast.LENGTH_LONG).show();
            db.close();
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.ErrorAlCrearProducto, Toast.LENGTH_LONG).show();
        }
    }

    //Aqui esta lo agregado
    public ArrayList<String> getServiceNames() {
        ArrayList<String> serviceNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT producto FROM " + TABLE_INVENTARIO + " ORDER BY producto COLLATE NOCASE ASC", null);

        if (cursor.moveToFirst()) {
            do {
                serviceNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return serviceNames;
    }



    public ArrayList<Productos> mostrarHistorialProductos() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Productos> listaProductos = new ArrayList<>();
        Productos productos = null;
        Cursor cursorProductos = null;

        cursorProductos = db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO + " ORDER BY producto COLLATE NOCASE ASC", null);

        if (cursorProductos.moveToFirst()) {
            do {
                productos = new Productos(0,null, null, 0,0,0,0, 0);
                productos.setId(cursorProductos.getLong(0));
                productos.setImagenproducto(BitmapFactory.decodeByteArray(cursorProductos.getBlob(1), 0, cursorProductos.getBlob(1).length));
                productos.setProducto(cursorProductos.getString(2));
                productos.setPrecio(cursorProductos.getDouble(3));
                productos.setPrecio_compra(cursorProductos.getDouble(4));
                productos.setGanancia(cursorProductos.getDouble(5));
                productos.setDescuento(cursorProductos.getDouble(6));
                productos.setCantidad(cursorProductos.getInt(7));
                listaProductos.add(productos);
            } while (cursorProductos.moveToNext());
        }
        cursorProductos.close();

        return listaProductos;
    }



    //Ahora para ver los productos
    public Productos verProductos(long id){
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Productos productos = null;
        Cursor cursorProductos = null;

        cursorProductos = db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO + " WHERE id = "+ id, null);

        if (cursorProductos.moveToFirst()){
            productos = new Productos(0,null, null, 0,0,0,0, 0);
            productos.setId(cursorProductos.getLong(0));
            productos.setImagenproducto(BitmapFactory.decodeByteArray(cursorProductos.getBlob(1), 0, cursorProductos.getBlob(1).length));
            productos.setProducto(cursorProductos.getString(2));
            productos.setPrecio(cursorProductos.getDouble(3));
            productos.setPrecio_compra(cursorProductos.getDouble(4));
            productos.setGanancia(cursorProductos.getDouble(5));
            productos.setDescuento(cursorProductos.getDouble(6));
            productos.setCantidad(cursorProductos.getInt(7));
        }
        cursorProductos.close();

        return productos;
    }

    //Para que sea de manera asc de cantidad (para lo del fragment)
    public ArrayList<Productos> mostrarHistorialProductos1() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Productos> listaProductos = new ArrayList<>();
        Productos productos = null;
        Cursor cursorProductos = null;

        cursorProductos = db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO + " ORDER BY cantidad ASC", null);

        if (cursorProductos.moveToFirst()) {
            do {
                productos = new Productos(0,null, null, 0,0,0,0, 0);
                productos.setId(cursorProductos.getLong(0));
                productos.setImagenproducto(BitmapFactory.decodeByteArray(cursorProductos.getBlob(1), 0, cursorProductos.getBlob(1).length));
                productos.setProducto(cursorProductos.getString(2));
                productos.setPrecio(cursorProductos.getDouble(3));
                productos.setPrecio_compra(cursorProductos.getDouble(4));
                productos.setGanancia(cursorProductos.getDouble(5));
                productos.setDescuento(cursorProductos.getDouble(6));
                productos.setCantidad(cursorProductos.getInt(7));
                listaProductos.add(productos);
            } while (cursorProductos.moveToNext());
        }
        cursorProductos.close();

        return listaProductos;
    }

    public ArrayList<Productos> mostrarHistorialProductosMasVendidos() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Productos> listaProductos = new ArrayList<>();
        Productos productos = null;
        Cursor cursorProductos = null;

        cursorProductos = db.rawQuery("SELECT * FROM " + TABLE_INVENTARIO +
                " ORDER BY (SELECT SUM(cantidad) FROM " + TABLE_VENTAS2 +
                " WHERE " + TABLE_VENTAS2 + ".producto_id = " + TABLE_INVENTARIO + ".id) DESC", null);

        if (cursorProductos.moveToFirst()) {
            do {
                productos = new Productos(0, null, null, 0, 0, 0, 0, 0);
                productos.setId(cursorProductos.getLong(0));
                productos.setImagenproducto(BitmapFactory.decodeByteArray(cursorProductos.getBlob(1), 0, cursorProductos.getBlob(1).length));
                productos.setProducto(cursorProductos.getString(2));
                productos.setPrecio(cursorProductos.getDouble(3));
                productos.setPrecio_compra(cursorProductos.getDouble(4));
                productos.setGanancia(cursorProductos.getDouble(5));
                productos.setDescuento(cursorProductos.getDouble(6));
                productos.setCantidad(cursorProductos.getInt(7));
                listaProductos.add(productos);
            } while (cursorProductos.moveToNext());
        }
        cursorProductos.close();

        return listaProductos;
    }

    public ArrayList<Ventas2> obtenerListaDeVentas2RelacionadaConProductos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Ventas2> listaVentas = new ArrayList<>();

        Cursor cursorVentas = db.rawQuery("SELECT * FROM " + TABLE_VENTAS2, null);

        if (cursorVentas.moveToFirst()) {
            do {
                Ventas2 venta = new Ventas2();
                venta.setId(cursorVentas.getInt(cursorVentas.getColumnIndex("id2")));
                venta.setProducto_id(cursorVentas.getLong(cursorVentas.getColumnIndex("producto_id")));
                venta.setFolio_id(cursorVentas.getInt(cursorVentas.getColumnIndex("folio_id")));
                venta.setCantidad(cursorVentas.getInt(cursorVentas.getColumnIndex("cantidad")));

                // Aquí puedes agregar más información relacionada con la venta si lo necesitas

                listaVentas.add(venta);
            } while (cursorVentas.moveToNext());
        }
        cursorVentas.close();

        return listaVentas;
    }



    public boolean editarProducto(long id, Bitmap imagen, String producto, double precio, double precio_compra, double ganancia, double descuento, int cantidad) {
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

            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("imagen", byteArray);
            values.put("producto", producto);
            values.put("precio", precio);
            values.put("precio_compra", precio_compra);
            values.put("ganancia", ganancia);
            values.put("descuento", descuento);
            values.put("cantidad", cantidad);

            db.update(TABLE_INVENTARIO, values, "id = ?", new String[]{String.valueOf(id)});
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }



    public boolean eliminarProducto(long id){

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_INVENTARIO + " WHERE id = '" + id + "'");
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