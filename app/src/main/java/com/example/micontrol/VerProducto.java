package com.example.micontrol;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbProductos;
import com.example.micontrol.entidades.Productos;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class VerProducto extends AppCompatActivity {

    private Dialog agregarCantidadDialog;
    DbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText codigo,producto,precio,precioCompra,cantidad,descuento;
    ImageView Imagenproducto;
    ImageButton btnCrear,btnEliminar,ingresarMas;
    boolean correcto = false;
    Productos productos;
    long id = 0;
    private List<ColorModel> colorList;

    private static final int PICK_IMAGE_REQUEST = 99;
    private Uri imagePath;
    private Bitmap imageToStore;
    double ganancia = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_producto);

        descuento = (EditText) findViewById(R.id.descuento);
        codigo = (EditText) findViewById(R.id.codigo);
        codigo.setEnabled(false);
        Imagenproducto = (ImageView) findViewById(R.id.imagenProducto);
        producto = (EditText) findViewById(R.id.producto);
        cantidad = (EditText) findViewById(R.id.cantidad);
        precio = (EditText) findViewById(R.id.precio);
        precioCompra = (EditText) findViewById(R.id.precioCompra);
        btnCrear = findViewById(R.id.añadir);
        btnEliminar = findViewById(R.id.eliminar);
        ingresarMas = findViewById(R.id.ingresarMas);
        dbHelper = new DbHelper(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                id = Long.parseLong(null);
            } else {
                id = extras.getLong("ID");
            }
        } else {
            id = (long) savedInstanceState.getSerializable("ID");
        }


        DbProductos dbProductos = new DbProductos(VerProducto.this);
        productos = dbProductos.verProductos(id);

        if (productos != null){
            codigo.setText(Long.toString(productos.getId()));
            Imagenproducto.setImageBitmap(productos.getImagenproducto());
            precio.setText(Double.toString(productos.getPrecio()));
            precioCompra.setText(Double.toString(productos.getPrecio_compra()));
            descuento.setText(Double.toString(productos.getDescuento()));
            producto.setText(productos.getProducto());
            cantidad.setText(Integer.toString(productos.getCantidad()));
        }

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.ver_p), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        //applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));



        ingresarMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Lo de la ventana emergente
                agregarCantidadDialog = new Dialog(VerProducto.this);
                agregarCantidadDialog.setContentView(R.layout.dialog_agregar_cantidad);
                agregarCantidadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                agregarCantidadDialog.setCancelable(true);

                Button btnAceptar = agregarCantidadDialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = agregarCantidadDialog.findViewById(R.id.btnCancelar);
                EditText etCantidad = agregarCantidadDialog.findViewById(R.id.etCantidad);

                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cantidadStr = etCantidad.getText().toString();
                        if (!cantidadStr.isEmpty()) {
                            int cantidadAdicional = Integer.parseInt(cantidadStr);
                            int cantidadExistente = Integer.parseInt(cantidad.getText().toString());
                            int nuevaCantidad = cantidadExistente + cantidadAdicional;
                            cantidad.setText(String.valueOf(nuevaCantidad));
                            agregarCantidadDialog.dismiss();
                        } else {
                            Toast.makeText(VerProducto.this, R.string.debesIngresarCantidad, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarCantidadDialog.dismiss();
                    }
                });

                ingresarMas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        agregarCantidadDialog.show();
                    }
                });

            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!codigo.getText().toString().isEmpty() &&
                        !producto.getText().toString().isEmpty() &&
                        !precio.getText().toString().isEmpty() &&
                        !precioCompra.getText().toString().isEmpty() &&
                        !cantidad.getText().toString().isEmpty()) {

                    Bitmap bitmapToStore = imageToStore != null ? imageToStore : ((BitmapDrawable) Imagenproducto.getDrawable()).getBitmap();
                    double precioDouble = Double.parseDouble(precio.getText().toString());
                    double precioCompraDouble = Double.parseDouble(precioCompra.getText().toString());

                    // Verificar si el campo de descuento está vacío y establecerlo en 0 si es así.
                    double descuentoDouble = 0.0; // Valor predeterminado de 0
                    String descuentoStr = descuento.getText().toString().trim();
                    if (!descuentoStr.isEmpty()) {
                        descuentoDouble = Double.parseDouble(descuentoStr);
                    }

                    int cantidadInt = Integer.parseInt(cantidad.getText().toString());

                    // Calcular la ganancia
                    double ganancia = (precioDouble - (precioDouble * descuentoDouble / 100)) - precioCompraDouble;

                    correcto = dbProductos.editarProducto(Long.parseLong(codigo.getText().toString()), bitmapToStore, producto.getText().toString(), precioDouble, precioCompraDouble, ganancia, descuentoDouble, cantidadInt);

                    if (correcto) {
                        Toast.makeText(VerProducto.this, R.string.ProductoModificado, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), EditarProductoActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else {
                        Toast.makeText(VerProducto.this, R.string.ErrorModificarProducto, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerProducto.this, R.string.llenaLosDatosFaltantes, Toast.LENGTH_LONG).show();
                }
            }
        });

        Imagenproducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        //Para eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerProducto.this);

                // Creamos un objeto ImageView y establecemos la imagen en él
                ImageView imageView = new ImageView(VerProducto.this);
                imageView.setImageResource(R.drawable.logomodified1);
                // Establecemos el tamaño de la imagen
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Agregamos el objeto ImageView al diseño del diálogo
                RelativeLayout layout = new RelativeLayout(VerProducto.this);
                layout.addView(imageView);

                // Agregamos el mensaje al diseño del diálogo
                TextView textView = new TextView(VerProducto.this);
                textView.setText(R.string.EliminarProducto);
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
                                if (dbProductos.eliminarProducto(id)){
                                    startActivity(new Intent(getApplicationContext(), EditarProductoActivity.class));
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
    }

    private void chooseImage(){
        try{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imagePath = data.getData();

                // Obtener información del tamaño de la imagen sin cargarla completamente
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, options);

                // Verificar el tamaño de la imagen
                if (options.outWidth > 1000 || options.outHeight > 1000) {
                    // Reducir el tamaño de la imagen antes de cargarla en el ImageView
                    options.inSampleSize = calculateInSampleSize(options, 1000, 1000);
                    options.inJustDecodeBounds = false;
                    Bitmap resizedImage = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath), null, options);
                    Imagenproducto.setImageBitmap(resizedImage);
                    imageToStore = resizedImage;
                } else {
                    // La imagen tiene un tamaño válido, cargarla directamente en el ImageView
                    imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    Imagenproducto.setImageBitmap(imageToStore);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Para que se pueda ver la imagen
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    private Bitmap getPredeterminedImage() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.logo);
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