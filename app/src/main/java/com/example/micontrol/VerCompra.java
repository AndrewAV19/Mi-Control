package com.example.micontrol;

import static com.example.micontrol.db.DbHelper.TABLE_COMPRAS2;
import static com.example.micontrol.db.DbHelper.TABLE_INVENTARIO;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.micontrol.db.DbCompras;
import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbProductos;
import com.example.micontrol.entidades.Compras;
import com.example.micontrol.entidades.Compras2;
import com.example.micontrol.entidades.Productos;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VerCompra extends AppCompatActivity {

    EditText nombre,recibido;
    Spinner producto;
    TextView total;
    TextView total2,cambio2;
    ImageButton btnCrear, btnEliminar, camara;
    boolean correcto = false;
    Compras compras;
    int id = 0;
    TableLayout tableLayout;
    ImageView pdf;
    private int cantidadSeleccionada = 0;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private double totalCompra = 0.0;
    private double totalCompraActual = 0.0;
    private boolean productoExistente = false;
    private int cantidadActual = 0;
    private int cantidadNueva = 0;
    private List<ColorModel> colorList;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isAccepted -> {
                if (isAccepted) Toast.makeText(this, R.string.PermisosConcedidos, Toast.LENGTH_LONG).show();
                else Toast.makeText(this, R.string.PermisosCancelados, Toast.LENGTH_LONG).show();
            }
    );


    ArrayAdapter<String> adapter; // Declarar el adaptador como variable miembro

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_compra);

        cambio2 = findViewById(R.id.cambio2);
        recibido = findViewById(R.id.recibido);
        pdf = (ImageView) findViewById(R.id.pdf);
        camara = findViewById(R.id.camara);
        nombre = findViewById(R.id.cliente);
        producto = findViewById(R.id.producto);
        total = findViewById(R.id.total);
        total2 = findViewById(R.id.total2);
        tableLayout = findViewById(R.id.tableLayout);
        btnCrear = findViewById(R.id.añadir);
        btnEliminar = findViewById(R.id.eliminar);

        // Crear los títulos de la tabla
        TableRow tableRowHeader = new TableRow(VerCompra.this);
        TextView nombreProductoHeader = new TextView(VerCompra.this);
        nombreProductoHeader.setText(R.string.Product);
        nombreProductoHeader.setTextSize(22);
        nombreProductoHeader.setTextColor(Color.WHITE);

        TextView cantidadProductoHeader = new TextView(VerCompra.this);
        cantidadProductoHeader.setText(R.string.Cantid);
        cantidadProductoHeader.setTextSize(22);
        cantidadProductoHeader.setTextColor(Color.WHITE);

        TextView precioProductoHeader = new TextView(VerCompra.this);
        precioProductoHeader.setText(R.string.Prec);
        precioProductoHeader.setTextSize(22);
        precioProductoHeader.setTextColor(Color.WHITE);

        TextView totalProductoHeader = new TextView(VerCompra.this);
        totalProductoHeader.setText(R.string.Tota);
        totalProductoHeader.setTextSize(22);
        totalProductoHeader.setTextColor(Color.WHITE);

        tableRowHeader.addView(nombreProductoHeader);
        tableRowHeader.addView(cantidadProductoHeader);
        tableRowHeader.addView(precioProductoHeader);
        tableRowHeader.addView(totalProductoHeader);
        tableLayout.addView(tableRowHeader);


        DbProductos dbProductos = new DbProductos(this);

        List<String> productos = dbProductos.getServiceNames();

        if (productos.isEmpty()) {
            Toast.makeText(this, R.string.AunNoHasAgregadoProductos, Toast.LENGTH_LONG).show();
        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item_jeje, productos);

            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            producto.setAdapter(adapter);
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = Integer.parseInt(null);
            } else {
                id = extras.getInt("ID");
            }
        } else {
            id = (int) savedInstanceState.getSerializable("ID");
        }

        DbCompras dbCompras = new DbCompras(VerCompra.this);
        compras = dbCompras.verCompra(id);

        if (compras != null) {
            nombre.setText(compras.getCliente());
            recibido.setText(String.valueOf(compras.getRecibido()));
            cambio2.setText(String.valueOf(compras.getCambio()));
            total2.setText(String.valueOf(compras.getTotal()));

            // Obtener los datos de TABLE_COMPRAS2 por medio del id de la compra
            ArrayList<Compras2> compras2 = dbCompras.obtenerCompras2PorIdCompra(compras.getId());

            totalCompra = compras.getTotal();

            // Mostrar los datos en el TableLayout
            for (Compras2 compra2 : compras2) {
                TableRow row = new TableRow(VerCompra.this);

                TextView productoTextView = new TextView(VerCompra.this);
                productoTextView.setText(compra2.getProducto().getProducto());

                TextView cantidadTextView = new TextView(VerCompra.this);
                cantidadTextView.setText(String.valueOf(compra2.getCantidad()));

                TextView precioTextView = new TextView(VerCompra.this);
                precioTextView.setText(String.valueOf(compra2.getProducto().getPrecio_compra()));

                // Calcular el total
                double total = compra2.getCantidad() * compra2.getProducto().getPrecio_compra();
                TextView totalTextView = new TextView(VerCompra.this);
                totalTextView.setText(String.valueOf(total));

                // Agregar las vistas a la fila
                row.addView(productoTextView);
                row.addView(cantidadTextView);
                row.addView(precioTextView);
                row.addView(totalTextView);

                // Agregar la fila al TableLayout
                tableLayout.addView(row);
            }
        }

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.ver_com), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        //applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));


        recibido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No es necesario implementar este método
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No es necesario implementar este método
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Verificar si se ha ingresado un valor válido en el campo "recibido"
                if (editable.length() > 0) {
                    // Obtener el valor recibido
                    double recibidoAmount = Double.parseDouble(editable.toString());

                    // Calcular el cambio
                    double cambio = recibidoAmount - totalCompra;

                    // Actualizar el campo "cambio2" con el valor calculado
                    cambio2.setText(String.format(Locale.getDefault(), "$%.2f", cambio));
                } else {
                    // El campo "recibido" está vacío, restablecer el campo "cambio2"
                    cambio2.setText("$0.00");
                }
            }
        });


        //Lo del pdf del boton
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos(view);
            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nombre.getText().toString().isEmpty() && !total2.getText().toString().isEmpty() ) {
                    double total = Double.parseDouble(total2.getText().toString().replace("$", ""));
                    // Dinero recibido
                    String recibidoText = recibido.getText().toString().trim();
                    double rec = 0; // Valor predeterminado
                    if (!recibidoText.isEmpty()) {
                        rec = Double.parseDouble(recibidoText.replace("$", ""));
                    }

                    double cam = rec - total; // Calcula el cambio correctamente

                    if (rec >= total) { // Verificar si el dinero recibido es suficiente
                        correcto = dbCompras.editarCompra(id, nombre.getText().toString(), rec, cam, total);

                        if (correcto) {
                            int folio = id;
                            SQLiteDatabase db = new DbHelper(VerCompra.this).getWritableDatabase();

                            ArrayList<Compras2> compras2Anteriores = dbCompras.obtenerCompras2PorIdCompra(compras.getId());
                            HashMap<String, Integer> cantidadesAnteriores = new HashMap<>();
                            for (Compras2 compra2 : compras2Anteriores) {
                                cantidadesAnteriores.put(compra2.getProducto().getProducto(), compra2.getCantidad());
                            }

                            dbCompras.eliminarCompra2(folio);

                            // Iterar sobre las filas del TableLayout
                            for (int i = 1; i < tableLayout.getChildCount(); i++) {
                                View child = tableLayout.getChildAt(i);
                                if (child instanceof TableRow) {
                                    TableRow row = (TableRow) child;
                                    TextView nombreProducto = (TextView) row.getChildAt(0);
                                    TextView cantidadProducto = (TextView) row.getChildAt(1);

                                    // Obtener los datos del producto
                                    String producto = nombreProducto.getText().toString();
                                    int cantidadNueva = Integer.parseInt(cantidadProducto.getText().toString());
                                    int cantidadActual = cantidadesAnteriores.getOrDefault(producto, 0);

                                    int diferencia = cantidadNueva - cantidadActual;

                                    if (diferencia > 0) {
                                        // Agregar al inventario
                                        agregarALaCompra(producto, Math.abs(diferencia));
                                    } else if (diferencia < 0) {
                                        // Quitar del inventario
                                        quitarDeLaCompra(producto, Math.abs(diferencia));
                                    }

                                    if (cantidadNueva == 0) {
                                        tableLayout.removeView(row);
                                    } else {
                                        // Guardar los datos en la tabla TABLE_COMPRAS2
                                        ContentValues values2 = new ContentValues();
                                        values2.put("folio_id", folio);
                                        values2.put("producto_id", obtenerIdProducto(producto)); // Obtener el ID del producto
                                        values2.put("cantidad", cantidadNueva);
                                        long id2 = db.insert(TABLE_COMPRAS2, null, values2);
                                    }
                                }
                            }

                            Toast.makeText(VerCompra.this, R.string.CompraModificada, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), EditarCompraActivity.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        } else {
                            Toast.makeText(VerCompra.this, R.string.ErrorModificarCompra, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(VerCompra.this, R.string.dineroNoSuficiente, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerCompra.this, R.string.llenaLosDatosFaltantes, Toast.LENGTH_LONG).show();
                }
            }
        });


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerCompra.this);

                // Creamos un objeto ImageView y establecemos la imagen en él
                ImageView imageView = new ImageView(VerCompra.this);
                imageView.setImageResource(R.drawable.logomodified1);
                // Establecemos el tamaño de la imagen
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Agregamos el objeto ImageView al diseño del diálogo
                RelativeLayout layout = new RelativeLayout(VerCompra.this);
                layout.addView(imageView);

                // Agregamos el mensaje al diseño del diálogo
                TextView textView = new TextView(VerCompra.this);
                textView.setText(R.string.EliminarCompra);
                textView.setPadding(20, 25, 20, 20);
                textView.setTextSize(20);
                textView.setId(View.generateViewId());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.BELOW, imageView.getId());
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layout.addView(textView, params);

                builder.setView(layout);
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (dbCompras.eliminarCompra(id)) {

                                    ArrayList<Compras2> compras2Anteriores = dbCompras.obtenerCompras2PorIdCompra(compras.getId());
                                    HashMap<String, Integer> cantidadesAnteriores = new HashMap<>();
                                    for (Compras2 compra2 : compras2Anteriores) {
                                        cantidadesAnteriores.put(compra2.getProducto().getProducto(), compra2.getCantidad());
                                    }

                                    for (int i1 = 1; i1 < tableLayout.getChildCount(); i1++) {
                                        View child = tableLayout.getChildAt(i1);
                                        if (child instanceof TableRow) {
                                            TableRow row = (TableRow) child;
                                            TextView nombreProducto = (TextView) row.getChildAt(0);
                                            TextView cantidadProducto = (TextView) row.getChildAt(1);

                                            // Obtener los datos del producto
                                            String producto = nombreProducto.getText().toString();
                                            int cantidadNueva = Integer.parseInt(cantidadProducto.getText().toString());
                                            int cantidadActual = cantidadesAnteriores.getOrDefault(producto, 0);

                                            int diferencia = cantidadActual;

                                            quitarDeLaCompra(producto, Math.abs(diferencia));

                                        }
                                    }

                                    int folio = id;
                                    dbCompras.eliminarCompra2(folio);

                                    startActivity(new Intent(getApplicationContext(), EditarCompraActivity.class));
                                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                    finish();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }

                        });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        positiveButton.setTextColor(getResources().getColor(R.color.white));
                        negativeButton.setTextColor(getResources().getColor(R.color.white));
                    }
                });
                dialog.getWindow().setBackgroundDrawableResource(R.color.colorDos);
                dialog.show();
            }
        });


        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar permisos de cámara
                if (ContextCompat.checkSelfPermission(VerCompra.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VerCompra.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // Si los permisos están concedidos, abrir la cámara para escanear el código de barras
                    iniciarEscaneoCodigoBarras();
                }
            }
        });

        producto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String productoSeleccionado = adapterView.getItemAtPosition(position).toString();
                mostrarDialogoCantidad(productoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No se seleccionó ningún producto
            }
        });
    }

    private void mostrarDialogoCantidad(String producto) {
        boolean productoExistente = false;
        int filaExistente = -1;

        // Verificar si el producto ya existe en el TableLayout
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View cellView = row.getChildAt(j);
                    if (cellView instanceof TextView) {
                        TextView textView = (TextView) cellView;
                        if (textView.getText().toString().equals(producto)) {
                            productoExistente = true;
                            filaExistente = i;
                            break;
                        }
                    }
                }
            }
        }

        if (productoExistente) {
            // Llamar al método mostrarDialogoActualizarCantidad
            mostrarDialogoActualizarCantidad(producto, filaExistente, cantidadSeleccionada);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.CantidadDe) + " " + producto);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);
            builder.setPositiveButton(R.string.Agregar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String cantidadString = input.getText().toString();
                    if (!cantidadString.isEmpty()) {
                        cantidadSeleccionada = Integer.parseInt(cantidadString);
                        agregarProductoATableLayout(producto, cantidadSeleccionada);
                    } else {
                        Toast.makeText(VerCompra.this, R.string.debesIngresarCantidad, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    private void agregarProductoATableLayout(String producto, int cantidad) {

        // Verificar si el producto ya existe en el TableLayout
        boolean productoExistente = false;
        int filaExistente = -1;

        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            View child = tableLayout.getChildAt(i);
            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View cellView = row.getChildAt(j);
                    if (cellView instanceof TextView) {
                        TextView textView = (TextView) cellView;
                        if (textView.getText().toString().equals(producto)) {
                            productoExistente = true;
                            filaExistente = i;
                            break;
                        }
                    }
                }
            }
        }

        if (productoExistente) {
            // Producto existente, mostrar diálogo para actualizar la cantidad
            mostrarDialogoActualizarCantidad(producto, filaExistente, cantidad);

        } else {
            TableRow tableRow = new TableRow(VerCompra.this);
            TextView nombreProducto = new TextView(VerCompra.this);
            nombreProducto.setText(producto);
            nombreProducto.setTextSize(14);
            nombreProducto.setTextColor(Color.WHITE);

            TextView cantidadProducto = new TextView(VerCompra.this);
            cantidadProducto.setText(String.valueOf(cantidad));
            cantidadProducto.setTextSize(14);
            cantidadProducto.setTextColor(Color.WHITE);

            TextView precioProducto = new TextView(VerCompra.this);
            DbProductos dbProductos = new DbProductos(this);
            double precio = dbProductos.getPrecioCompraProducto(producto);
            precioProducto.setText(String.valueOf(precio));
            precioProducto.setTextSize(14);
            precioProducto.setTextColor(Color.WHITE);

            double totalProducto = cantidad * precio;
            totalCompra += totalProducto;
            total2.setText(String.format(Locale.getDefault(), "$%.2f", totalCompra));

            TextView total = new TextView(VerCompra.this);
            total.setText(String.format(Locale.getDefault(), "$%.2f", totalProducto));
            total.setTextSize(14);
            total.setTextColor(Color.WHITE);

            tableRow.addView(nombreProducto);
            tableRow.addView(cantidadProducto);
            tableRow.addView(precioProducto);
            tableRow.addView(total);
            tableLayout.addView(tableRow);
        }

    }


    private long obtenerIdProducto(String nombreProducto) {
        // Crear una instancia de la base de datos
        SQLiteDatabase db = new DbHelper(VerCompra.this).getReadableDatabase();

        // Consultar el ID del producto por su nombre
        String[] projection = {"id"};
        String selection = "producto=?";
        String[] selectionArgs = {nombreProducto};
        Cursor cursor = db.query(TABLE_INVENTARIO, projection, selection, selectionArgs, null, null, null);

        long idProducto = -1;
        if (cursor.moveToFirst()) {
            idProducto = cursor.getLong(cursor.getColumnIndex("id"));
        }

        // Cerrar el cursor y la base de datos
        cursor.close();
        db.close();

        return idProducto;
    }

    private void mostrarDialogoActualizarCantidad(String producto, int filaExistente, int cantidad) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.CambiarCantidadDe)+" " + producto + " "+(getString(R.string.a)));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton(R.string.Cambiar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cantidadString = input.getText().toString();
                if (!cantidadString.isEmpty()) {
                    int nuevaCantidad = Integer.parseInt(cantidadString);
                    actualizarCantidadEnTabla(producto, filaExistente, nuevaCantidad);
                } else {
                    Toast.makeText(VerCompra.this, R.string.debesIngresarCantidad, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void actualizarCantidadEnTabla(String producto, int filaExistente, int nuevaCantidad) {
        TableRow row = (TableRow) tableLayout.getChildAt(filaExistente);
        TextView cantidadProducto = (TextView) row.getChildAt(1);
        TextView total = (TextView) row.getChildAt(3);
        int cantidadActual = Integer.parseInt(cantidadProducto.getText().toString());

        // Obtener el precio del producto desde la base de datos
        DbProductos dbProductos = new DbProductos(this);
        double precio = dbProductos.getPrecioCompraProducto(producto);

        // Calcular el total del producto actual y anterior
        double totalProductoActual = nuevaCantidad * precio;
        double totalProductoAnterior = cantidadActual * precio;

        // Actualizar el valor de totalCompra
        totalCompra -= totalProductoAnterior;
        totalCompra += totalProductoActual;
        total2.setText(String.format(Locale.getDefault(), "$%.2f", totalCompra));




            cantidadProducto.setText(String.valueOf(nuevaCantidad));
            total.setText(String.format(Locale.getDefault(), "$%.2f", totalProductoActual));


    }

    // Para el código de barras
    private void iniciarEscaneoCodigoBarras() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false); // Permitir rotación de la pantalla durante el escaneo
        integrator.setBeepEnabled(false); // Desactivar el sonido al escanear
        integrator.setCaptureActivity(CustomCaptureActivity.class); // Clase personalizada para capturar el resultado del escaneo
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, abrir la cámara para escanear el código de barras
                iniciarEscaneoCodigoBarras();
            } else {
                Toast.makeText(this, R.string.permisoCamaraDenegado, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                String codigoBarras = result.getContents();
                buscarProductoPorCodigo(codigoBarras);
            }
        }
    }

    private void buscarProductoPorCodigo(String codigo) {
        // Convertir el código de barras a tipo long
        long codigoLong;
        try {
            codigoLong = Long.parseLong(codigo);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.codigoNoValido, Toast.LENGTH_SHORT).show();
            return;
        }

        // Realizar la búsqueda del producto por el código escaneado
        boolean encontrado = false;
        String nombreProducto = "";
        DbProductos dbProductos = new DbProductos(this);
        List<Productos> productos = dbProductos.obtenerProductos();
        for (Productos producto : productos) {
            if (producto.getId() == codigoLong) {
                encontrado = true;
                nombreProducto = producto.getProducto();
                break;
            }
        }

        if (encontrado) {
            mostrarDialogoCantidad(nombreProducto);
        } else {
            Toast.makeText(this, R.string.codigoNoCoincide, Toast.LENGTH_SHORT).show();
        }
    }

    DbProductos dbProductos = new DbProductos(this);

    void agregarALaCompra(String producto, int cantidad) {
        // Obtener la instancia de la base de datos
        SQLiteDatabase db = new DbHelper(VerCompra.this).getWritableDatabase();

        // Obtener la cantidad actual del producto en la compra
        int cantidadActual = dbProductos.getCantidadProducto(producto);

        // Calcular la nueva cantidad después de agregar
        int nuevaCantidad = cantidadActual + cantidad;

        // Actualizar la cantidad del producto en la tabla de compra
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);
        String whereClause = "producto = ?";
        String[] whereArgs = {producto};
        db.update(TABLE_INVENTARIO, values, whereClause, whereArgs);

        // Cerrar la base de datos
        db.close();
    }

    void quitarDeLaCompra(String producto, int cantidad) {
        // Obtener la instancia de la base de datos
        SQLiteDatabase db = new DbHelper(VerCompra.this).getWritableDatabase();

        // Obtener la cantidad actual del producto en la compra
        int cantidadActual = dbProductos.getCantidadProducto(producto);

        // Calcular la nueva cantidad después de quitar
        int nuevaCantidad = cantidadActual - cantidad;

        // Actualizar la cantidad del producto en la tabla de compra
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);
        String whereClause = "producto = ?";
        String[] whereArgs = {producto};
        db.update(TABLE_INVENTARIO, values, whereClause, whereArgs);

        // Cerrar la base de datos
        db.close();
    }


    //Lo del pdf
    private void verificarPermisos(View view) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.PermisosConcedidos, Toast.LENGTH_LONG).show();
            crearPDF();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )) {
            Snackbar.make(view, R.string.EstePermisoEsNecesario, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            });
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void crearPDF() {
        try {
            String carpeta = "/compras_mi_control_pdf";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
                Toast.makeText(this, R.string.CarpetaCreada, Toast.LENGTH_LONG).show();
            }
            File archivo = new File(dir, compras.getId() + ".pdf");
            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter writer = PdfWriter.getInstance(documento, fos);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onStartPage(PdfWriter writer, Document document) {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    canvas.saveState();
                    canvas.setColorFill(BaseColor.WHITE); // Color de fondo blanco
                    canvas.rectangle(document.left(), document.bottom(), document.right(), document.top());
                    canvas.fill();
                    canvas.restoreState();
                }
            });

            documento.open();

            // Título "Ticket de Compra"
            Paragraph titulo = new Paragraph(getString(R.string.TICKET_COMPRA), new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            // Folio
            Paragraph folio = new Paragraph(getString(R.string.FOLIOPDF)+" " + compras.getId(), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
            folio.setAlignment(Element.ALIGN_LEFT);
            documento.add(folio);

            Paragraph fecha = new Paragraph(getString(R.string.FECHA)+" " + compras.getFecha(), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
            fecha.setAlignment(Element.ALIGN_LEFT);
            documento.add(fecha);

            Paragraph hora = new Paragraph(getString(R.string.HORA)+" " + compras.getHora(), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
            hora.setAlignment(Element.ALIGN_LEFT);
            documento.add(hora);

            // Crear tabla de productos
            PdfPTable tablaProductos = new PdfPTable(3); // 3 columnas para Producto, Cantidad y Precio
            tablaProductos.setWidthPercentage(100); // Ancho de tabla 100%
            tablaProductos.setSpacingBefore(20f); // Espacio antes de la tabla

            // Encabezados de la tabla
            PdfPCell celdaProducto = new PdfPCell(new Phrase(getString(R.string.Product)));
            celdaProducto.setBackgroundColor(BaseColor.LIGHT_GRAY);
            celdaProducto.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaProductos.addCell(celdaProducto);

            PdfPCell celdaCantidad = new PdfPCell(new Phrase(getString(R.string.CANT)));
            celdaCantidad.setBackgroundColor(BaseColor.LIGHT_GRAY);
            celdaCantidad.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaProductos.addCell(celdaCantidad);

            PdfPCell celdaPrecio = new PdfPCell(new Phrase(getString(R.string.Prec)));
            celdaPrecio.setBackgroundColor(BaseColor.LIGHT_GRAY);
            celdaPrecio.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaProductos.addCell(celdaPrecio);

            // Agregar productos a la tabla
            List<Compras2> listaProductos = obtenerProductosDeCompra(compras.getId()); // Obtener los productos de la compra
            for (Compras2 producto : listaProductos) {
                tablaProductos.addCell(producto.getProducto().getProducto()); // Nombre del producto
                tablaProductos.addCell(String.valueOf(producto.getCantidad())); // Cantidad

                double precioProducto = producto.getProducto().getPrecio(); // Obtener el precio del producto
                tablaProductos.addCell(String.valueOf(precioProducto)); // Precio total del producto
            }

            // Agregar tabla de productos al documento
            documento.add(tablaProductos);


            // Agregar saltos de línea
            for (int i = 0; i < 2; i++) {
                documento.add(new Paragraph("\n"));
            }

            // Total
            Paragraph total = new Paragraph(getString(R.string.TOTAL)+" " + compras.getTotal(), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingAfter(20f); // Espacio después del párrafo
            documento.add(total);

            // Agregar saltos de línea
            for (int i = 0; i < 10; i++) {
                documento.add(new Paragraph("\n"));
            }

            // Mensaje de agradecimiento
            Paragraph agradecimiento = new Paragraph(getString(R.string.GRACIASPORTUCOMPRA), new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK));
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            documento.add(agradecimiento);

            documento.close();

            Toast.makeText(this, R.string.ArchivoPdfCreadoCorrectamente, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(VerCompra.this);
            builder.setMessage(R.string.DeseasEnviarPDFPorWhats)
                    .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            enviarPorWhatsApp();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // No hacer nada
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void enviarPorWhatsApp() {
        String carpeta = "/compras_mi_control_pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;
        String archivoPath = path + "/"+compras.getId()+".pdf";

        File archivo = new File(archivoPath);

        Uri archivoUri = FileProvider.getUriForFile(this, "com.example.micontrol.fileprovider", archivo);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, archivoUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Concede permisos de lectura al receptor

        intent.setPackage("com.whatsapp");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.WhatsappNoIntalado, Toast.LENGTH_SHORT).show();
        }

    }

    private List<Compras2> obtenerProductosDeCompra(int compraId) {
        List<Compras2> listaProductos = new ArrayList<>();

        // Obtener una instancia de DbHelper
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Definir la consulta SQL para obtener los productos de la compra
        String query = "SELECT * FROM " + DbHelper.TABLE_COMPRAS2 +
                " WHERE folio_id = ?";
        String[] selectionArgs = { String.valueOf(compraId) };

        // Ejecutar la consulta y obtener el cursor
        Cursor cursor = db.rawQuery(query, selectionArgs);

        // Recorrer el cursor y construir la lista de productos
        while (cursor.moveToNext()) {
            Compras2 producto = new Compras2();

            // Obtener los datos del cursor y asignarlos al objeto Compras2
            producto.setId(cursor.getInt(cursor.getColumnIndex("id2")));
            producto.setFolio_id(cursor.getInt(cursor.getColumnIndex("folio_id")));
            producto.setProducto_id(cursor.getLong(cursor.getColumnIndex("producto_id")));
            producto.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));

            // Consultar los datos del producto en la tabla de inventario
            long productoId = producto.getProducto_id();
            Cursor productoCursor = db.query(
                    DbHelper.TABLE_INVENTARIO,
                    null,
                    "id = ?",
                    new String[] { String.valueOf(productoId) },
                    null,
                    null,
                    null
            );

            if (productoCursor.moveToFirst()) {
                // Construir el objeto Productos y asignarlo al objeto Compras2
                Productos productoDetalle = new Productos();
                productoDetalle.setId(productoCursor.getLong(productoCursor.getColumnIndex("id")));
                productoDetalle.setProducto(productoCursor.getString(productoCursor.getColumnIndex("producto")));
                productoDetalle.setPrecio(productoCursor.getDouble(productoCursor.getColumnIndex("precio")));
                producto.setProducto(productoDetalle);
            }

            productoCursor.close();
            listaProductos.add(producto);
        }

        // Cerrar el cursor y la base de datos
        cursor.close();
        db.close();

        return listaProductos;
    }

    private int getStartColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getStartColorRes();
            }
        }
        return R.color.colorUno;
    }

    private int getCenterColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getCenterColorRes();
            }
        }
        return R.color.colorDos;
    }

    private int getEndColor(String selectedColor) {
        for (ColorModel color : colorList) {
            if (color.getColorName().equals(selectedColor)) {
                return color.getEndColorRes();
            }
        }
        return R.color.colorTres;
    }


    // Método para aplicar el color de fondo
    private void applyBackground(View view, int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, startColor),
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, endColor)
                }
        );
        view.setBackground(gradientDrawable);
        setStatusBarColor(ContextCompat.getColor(this, startColor));
    }


    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }


    // Método para aplicar el color del BottomNavigationView
    private void applyBottomNavigationColor(BottomNavigationView bottomNavigationView, String selectedColor) {
        int startColor = getStartColor(selectedColor);
        int centerColor = getCenterColor(selectedColor);
        int endColor = getEndColor(selectedColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, startColor),
                        ContextCompat.getColor(this, centerColor),
                        ContextCompat.getColor(this, endColor)
                }
        );

        // Establecer el gradiente como el fondo del área de contenido principal del BottomNavigationView
        View bottomNavigationContent = bottomNavigationView.getChildAt(0);
        bottomNavigationContent.setBackground(gradientDrawable);
    }


    // Método para crear la lista de objetos ColorModel
    private List<ColorModel> createColorList() {
        List<ColorModel> colorList = new ArrayList<>();
        colorList.add(new ColorModel(getString(R.string.cafe), R.color.cafeUno, R.color.cafeDos, R.color.cafeTres));
        colorList.add(new ColorModel(getString(R.string.azul), R.color.azulUno, R.color.azulDos, R.color.azulTres));
        colorList.add(new ColorModel(getString(R.string.gris), R.color.grisUno, R.color.grisDos, R.color.grisTres));
        colorList.add(new ColorModel(getString(R.string.verde), R.color.verdeUno, R.color.verdeDos, R.color.verdeTres));
        colorList.add(new ColorModel(getString(R.string.morado), R.color.moradoUno, R.color.moradoDos, R.color.moradoTres));
        colorList.add(new ColorModel(getString(R.string.rosa), R.color.rosaUno, R.color.rosaDos, R.color.rosaTres));
        return colorList;
    }




}
