package com.example.micontrol.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NOMBRE = "micontrol.db";
    public static final String TABLE_CITAS = "t_citas";
    public static final String TABLE_SERVICIOS = "t_servicios";
    public static final String TABLE_TALLERES = "t_talleres";
    public static final String TABLE_INVENTARIO = "t_inventario";
    public static final String TABLE_VENTAS = "t_ventas";
    public static final String TABLE_COMPRAS = "t_compras";
    public static final String TABLE_MERMA = "t_merma";
    public static final String TABLE_VENTAS2 = "t_ventas2";
    public static final String TABLE_COMPRAS2 = "t_compras2";
    public static final String TABLE_MERMA2 = "t_merma2";
    public static final String TABLE_LOGO = "t_logo";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Tabla de servicios
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_SERVICIOS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "imagen BLOB , " +
                "servicio TEXT NOT NULL, " +
                "precio DOUBLE NOT NULL)");

        //Tabla de Citas
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_CITAS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "telefono TEXT NOT NULL," +
                "fecha Date NOT NULL," +
                "hora TEXT NOT NULL," +
                "id_servicio INTEGER, " +
                "informacion_adicional TEXT," +
                "FOREIGN KEY (id_servicio) REFERENCES "+ TABLE_SERVICIOS + "(id)" +
                ")");

        //Tabla de Talleres
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_TALLERES + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "imagen BLOB , " +
                "titulo TEXT NOT NULL, " +
                "informacion TEXT NOT NULL, " +
                "precio DOUBLE NOT NULL)");

        //Tabla de Inventario
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_INVENTARIO + "(" +
                "id LONG PRIMARY KEY , " +
                "imagen BLOB , " +
                "producto TEXT NOT NULL, " +
                "precio DOUBLE NOT NULL, " +
                "precio_compra DOUBLE NOT NULL, " +
                "ganancia DOUBLE NOT NULL, " +
                "descuento DOUBLE , " +
                "cantidad INTEGER NOT NULL)");

        //Tabla de Ventas2
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_VENTAS2 + "(" +
                "id2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "folio_id INTEGER NOT NULL, " +
                "producto_id LONG NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "FOREIGN KEY (folio_id) REFERENCES " + TABLE_VENTAS + "(id)," +
                "FOREIGN KEY (producto_id) REFERENCES " + TABLE_INVENTARIO + "(id));");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_VENTAS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha DATE NOT NULL," +
                "hora TEXT NOT NULL," +
                "cliente TEXT, " +
                "recibido double, " +
                "cambio double, " +
                "ganancias double, " +
                "total double NOT NULL)");

        //Tabla de Compras2
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_COMPRAS2 + "(" +
                "id2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "folio_id INTEGER NOT NULL, " +
                "producto_id LONG NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "FOREIGN KEY (folio_id) REFERENCES " + TABLE_COMPRAS + "(id)," +
                "FOREIGN KEY (producto_id) REFERENCES " + TABLE_INVENTARIO + "(id));");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_COMPRAS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha DATE NOT NULL," +
                "hora TEXT NOT NULL," +
                "cliente TEXT, " +
                "recibido double, " +
                "cambio double, " +
                "total double NOT NULL)");

        //Tabla de Merma2
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_MERMA2 + "(" +
                "id2 INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "folio_id INTEGER NOT NULL, " +
                "producto_id LONG NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "FOREIGN KEY (folio_id) REFERENCES " + TABLE_MERMA + "(id)," +
                "FOREIGN KEY (producto_id) REFERENCES " + TABLE_INVENTARIO + "(id));");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_MERMA + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha DATE NOT NULL," +
                "hora TEXT NOT NULL," +
                "motivo TEXT, " +
                "total double NOT NULL)");

        //Tabla de logo
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_LOGO + "(" +
                "id INTEGER , " +
                "nombre TEXT ," +
                "imagen BLOB )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CITAS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICIOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TALLERES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTARIO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VENTAS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_VENTAS2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGO);
        onCreate(sqLiteDatabase);
    }

    public boolean backupDatabase(Context context) {
        try {
            File dbFile = context.getDatabasePath(DATABASE_NOMBRE);

            if (dbFile.exists()) {
                String backupFileName = "backup_" + System.currentTimeMillis() + ".db";
                File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiAppBackups");

                if (!backupDir.exists()) {
                    backupDir.mkdirs();
                }

                File backupFile = new File(backupDir, backupFileName);

                FileInputStream fis = new FileInputStream(dbFile);
                FileOutputStream fos = new FileOutputStream(backupFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                fos.close();
                fis.close();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean restoreDatabase(Context context, String backupFileName) {
        try {
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiAppBackups");
            File backupFile = new File(backupDir, backupFileName);

            if (backupFile.exists()) {
                File dbFile = context.getDatabasePath(DATABASE_NOMBRE);

                FileInputStream fis = new FileInputStream(backupFile);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                fos.close();
                fis.close();

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



}
