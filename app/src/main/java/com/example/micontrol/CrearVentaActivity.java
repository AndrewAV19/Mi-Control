package com.example.micontrol;

import static com.example.micontrol.db.DbHelper.TABLE_INVENTARIO;
import static com.example.micontrol.db.DbHelper.TABLE_VENTAS;
import static com.example.micontrol.db.DbHelper.TABLE_VENTAS2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbProductos;
import com.example.micontrol.entidades.Productos;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CrearVentaActivity extends AppCompatActivity {

    EditText nombre,recibido;
    Spinner producto;
    TextView total;
    TextView total2,cambio2, ganancia2;
    ImageButton btnCrear, camara;
    TableLayout tableLayout;
    private int cantidadSeleccionada = 0;
    private double totalVenta = 0.0;
    private double cambioVenta = 0.0;
    private double totalGanancias = 0.0;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private List<ColorModel> colorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_venta);

        ganancia2 = findViewById(R.id.ganancia2);
        cambio2 = findViewById(R.id.cambio2);
        recibido = findViewById(R.id.recibido);
        camara = (ImageButton) findViewById(R.id.camara);
        nombre = findViewById(R.id.cliente);
        producto = findViewById(R.id.producto);
        total = findViewById(R.id.total);
        total2 = findViewById(R.id.total2);
        btnCrear = findViewById(R.id.añadir);
        tableLayout = findViewById(R.id.tableLayout);

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
                    double cambio = recibidoAmount - totalVenta;

                    // Actualizar el campo "cambio2" con el valor calculado
                    cambio2.setText(String.format(Locale.getDefault(), "$%.2f", cambio));
                } else {
                    // El campo "recibido" está vacío, restablecer el campo "cambio2"
                    cambio2.setText("$0.00");
                }
            }
        });



        Calendar c = Calendar.getInstance();
        String fecha = String.format("%02d/%d/%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), c.get(Calendar.DAY_OF_MONTH));
        String hora = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        total2.setText("$0.00");

        // Crear los títulos de la tabla
        TableRow tableRowHeader = new TableRow(CrearVentaActivity.this);
        TextView nombreProductoHeader = new TextView(CrearVentaActivity.this);
        nombreProductoHeader.setText(R.string.Product);
        nombreProductoHeader.setTextSize(22);
        nombreProductoHeader.setTextColor(Color.WHITE);

        TextView cantidadProductoHeader = new TextView(CrearVentaActivity.this);
        cantidadProductoHeader.setText(R.string.Cantid);
        cantidadProductoHeader.setTextSize(22);
        cantidadProductoHeader.setTextColor(Color.WHITE);

        TextView precioProductoHeader = new TextView(CrearVentaActivity.this);
        precioProductoHeader.setText(R.string.Prec);
        precioProductoHeader.setTextSize(22);
        precioProductoHeader.setTextColor(Color.WHITE);

        TextView totalProductoHeader = new TextView(CrearVentaActivity.this);
        totalProductoHeader.setText(R.string.Tota);
        totalProductoHeader.setTextSize(22);
        totalProductoHeader.setTextColor(Color.WHITE);

        tableRowHeader.addView(nombreProductoHeader);
        tableRowHeader.addView(cantidadProductoHeader);
        tableRowHeader.addView(precioProductoHeader);
        tableRowHeader.addView(totalProductoHeader);
        tableLayout.addView(tableRowHeader);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.crear_v), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));

        bottomNavigationView.setSelectedItemId(R.id.crear_venta);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_venta:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "ventas");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_venta:
                    return true;
                case R.id.editar_venta:
                    startActivity(new Intent(getApplicationContext(), EditarVentaActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.ingresos_ventas:
                    startActivity(new Intent(getApplicationContext(), IngresosVentasActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.historial_ventas:
                    startActivity(new Intent(getApplicationContext(), HistorialVentasActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
            }
            return false;
        });

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Verificar permisos de cámara
                if (ContextCompat.checkSelfPermission(CrearVentaActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CrearVentaActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
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



        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cliente = nombre.getText().toString().trim();

                // Obtener la fecha y hora actuales
                Calendar c = Calendar.getInstance();
                String fecha = String.format("%04d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                String hora = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

                // Total de la venta
                double total = Double.parseDouble(total2.getText().toString().substring(1));
                double ganancias = Double.parseDouble(ganancia2.getText().toString().substring(1)); // Eliminar el símbolo de $

                // Dinero recibido
                double recibidoAmount;
                if (recibido.getText().toString().isEmpty()) {
                    recibidoAmount = 0.0;
                } else {
                    recibidoAmount = Double.parseDouble(recibido.getText().toString());
                }

                // Calcular el cambio
                double cambio = recibidoAmount - total;

                if (recibidoAmount < total) {
                    // Mostrar mensaje de error
                    Toast.makeText(CrearVentaActivity.this, R.string.dineroNoSuficiente, Toast.LENGTH_LONG).show();
                } else {
                    // Crear una instancia de la base de datos
                    SQLiteDatabase db = new DbHelper(CrearVentaActivity.this).getWritableDatabase();

                    // Guardar los datos en la tabla TABLE_VENTAS
                    ContentValues values = new ContentValues();
                    values.put("fecha", fecha);
                    values.put("hora", hora);
                    values.put("cliente", cliente);
                    values.put("recibido", recibidoAmount);
                    values.put("cambio", cambio);
                    values.put("ganancias", ganancias);
                    values.put("total", total);
                    long id = db.insert(TABLE_VENTAS, null, values);

                    if (id > 0) {
                        // Obtener el folio como el ID de la venta
                        int folio = (int) id;

                        // Iterar sobre las filas de la tabla
                        for (int i = 1; i < tableLayout.getChildCount(); i++) {
                            View child = tableLayout.getChildAt(i);
                            if (child instanceof TableRow) {
                                TableRow row = (TableRow) child;
                                TextView nombreProducto = (TextView) row.getChildAt(0);
                                TextView cantidadProducto = (TextView) row.getChildAt(1);

                                // Obtener los datos del producto
                                String producto = nombreProducto.getText().toString();
                                int cantidad = Integer.parseInt(cantidadProducto.getText().toString());

                                // Verificar si la cantidad es mayor que 0
                                if (cantidad > 0) {
                                    ContentValues values2 = new ContentValues();
                                    values2.put("folio_id", folio);
                                    values2.put("producto_id", obtenerIdProducto(producto)); // Obtener el ID del producto
                                    values2.put("cantidad", cantidad);
                                    long id2 = db.insert(TABLE_VENTAS2, null, values2);

                                    // Actualizar la cantidad en el inventario
                                    DbProductos dbProductos = new DbProductos(CrearVentaActivity.this);
                                    dbProductos.disminuirCantidadProducto(producto, cantidad);

                                    if (id2 > 0) {
                                        // Venta agregada correctamente
                                        Toast.makeText(CrearVentaActivity.this, R.string.VentaCreadaCorrectamente, Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), CrearVentaActivity.class));
                                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                        finish();
                                    } else {
                                        // Error al agregar la venta
                                        Toast.makeText(CrearVentaActivity.this, R.string.ErrorCrearVenta, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // Eliminar el producto si la cantidad es 0
                                    // Aquí puedes agregar el código necesario para eliminar el producto de la venta
                                }
                            }
                        }
                    } else {
                        // Error al agregar la venta
                        Toast.makeText(CrearVentaActivity.this, R.string.ErrorCrearVenta, Toast.LENGTH_LONG).show();
                    }

                    // Cerrar la base de datos
                    db.close();
                }
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
                    Toast.makeText(CrearVentaActivity.this, R.string.debesIngresarCantidad, Toast.LENGTH_SHORT).show();
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
        // Verificar si la cantidad está disponible
        if (!verificarCantidadDisponible(producto, cantidad)) {
            Toast.makeText(this, R.string.NoSeTieneEsaCantidadProducto, Toast.LENGTH_SHORT).show();
            return;
        }

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
            mostrarDialogoActualizarCantidad(producto, filaExistente, cantidad);
        } else {
            TableRow tableRow = new TableRow(CrearVentaActivity.this);
            TextView nombreProducto = new TextView(CrearVentaActivity.this);
            nombreProducto.setText(producto);
            nombreProducto.setTextSize(14);
            nombreProducto.setTextColor(Color.WHITE);

            TextView cantidadProducto = new TextView(CrearVentaActivity.this);
            cantidadProducto.setText(String.valueOf(cantidad));
            cantidadProducto.setTextSize(14);
            cantidadProducto.setTextColor(Color.WHITE);

            TextView precioProducto = new TextView(CrearVentaActivity.this);
            DbProductos dbProductos = new DbProductos(this);
            double precio = dbProductos.getPrecioProducto(producto);
            double costoProducto = dbProductos.getPrecioCompraProducto(producto);
            precioProducto.setText(String.valueOf(precio));
            precioProducto.setTextSize(14);
            precioProducto.setTextColor(Color.WHITE);

            double totalProducto = cantidad * precio;
            totalVenta += totalProducto;
            total2.setText(String.format(Locale.getDefault(), "$%.2f", totalVenta));

            TextView total = new TextView(CrearVentaActivity.this);
            total.setText(String.format(Locale.getDefault(), "$%.2f", totalProducto));
            total.setTextSize(14);
            total.setTextColor(Color.WHITE);

            // Calcular la ganancia del producto agregado
            double gananciaProducto = (precio - costoProducto) * cantidad;
            totalGanancias += gananciaProducto;
            ganancia2.setText(String.format(Locale.getDefault(), "$%.2f", totalGanancias));

            tableRow.addView(nombreProducto);
            tableRow.addView(cantidadProducto);
            tableRow.addView(precioProducto);
            tableRow.addView(total);
            tableLayout.addView(tableRow);


        }

    }

    private boolean verificarCantidadDisponible(String producto, int cantidad) {
        DbProductos dbProductos = new DbProductos(this);
        int cantidadDisponible = dbProductos.getCantidadProducto(producto);
        return cantidadDisponible >= cantidad;
    }

    private long obtenerIdProducto(String nombreProducto) {
        // Crear una instancia de la base de datos
        SQLiteDatabase db = new DbHelper(CrearVentaActivity.this).getReadableDatabase();

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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
                    Toast.makeText(CrearVentaActivity.this, R.string.debesIngresarCantidad, Toast.LENGTH_SHORT).show();
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
        double precio = dbProductos.getPrecioProducto(producto);
        double costoProducto = dbProductos.getPrecioCompraProducto(producto);

        // Calcular el total del producto actual y anterior
        double totalProductoActual = nuevaCantidad * precio;
        double totalProductoAnterior = cantidadActual * precio;

        // Calcular la ganancia del producto actual y anterior
        double gananciaProductoActual = (precio - costoProducto) * nuevaCantidad;
        double gananciaProductoAnterior = (precio - costoProducto) * cantidadActual;


        // Actualizar el valor de totalVenta
        totalVenta -= totalProductoAnterior;
        totalVenta += totalProductoActual;
        total2.setText(String.format(Locale.getDefault(), "$%.2f", totalVenta));

        // Actualizar el valor de totalGanancias
        totalGanancias -= gananciaProductoAnterior;
        totalGanancias += gananciaProductoActual;
        ganancia2.setText(String.format(Locale.getDefault(), "$%.2f", totalGanancias));

        // Obtener el límite del inventario para el producto seleccionado
        int limiteInventario = dbProductos.getCantidadProducto(producto);

        if (nuevaCantidad <= limiteInventario) {
            cantidadProducto.setText(String.valueOf(nuevaCantidad));
            total.setText(String.format(Locale.getDefault(), "$%.2f", totalProductoActual));
        } else {
            Toast.makeText(CrearVentaActivity.this, R.string.NoSeTieneEsaCantidadProducto, Toast.LENGTH_SHORT).show();
            // Restaurar el total anterior
            totalVenta -= totalProductoActual;
            totalVenta += totalProductoAnterior;
            total2.setText(String.format(Locale.getDefault(), "$%.2f", totalVenta));
        }
    }






    //Para el codigo de barras
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