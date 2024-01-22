package com.example.micontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

public class CrearProductoActivity extends AppCompatActivity {

    DbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText codigo,producto,precio,precioCompra,cantidad,descuento;
    Button cambiar_imagen;
    ImageButton añadir,camara;
    Button cambiar;
    ImageView ImagenProducto;
    private static final int PICK_IMAGE_REQUEST = 99;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    double ganancia = 0;
    private List<ColorModel> colorList;


    private Uri imagePath;
    private Bitmap imageToStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_producto);

        descuento = (EditText) findViewById(R.id.descuento);
        camara = (ImageButton) findViewById(R.id.camara);
        codigo = (EditText) findViewById(R.id.codigo);
        ImagenProducto = (ImageView) findViewById(R.id.imagenProducto);
        producto = (EditText) findViewById(R.id.producto);
        cantidad = (EditText) findViewById(R.id.cantidad);
        precio = (EditText) findViewById(R.id.precio);
        precioCompra = (EditText) findViewById(R.id.precioCompra);
        añadir = (ImageButton) findViewById(R.id.añadir);
        dbHelper = new DbHelper(this);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.crear_p), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));


        bottomNavigationView.setSelectedItemId(R.id.crear_producto);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_producto:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "inventario");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_producto:
                    return true;
                case R.id.editar_producto:
                    startActivity(new Intent(getApplicationContext(), EditarProductoActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.historial_productos:
                    startActivity(new Intent(getApplicationContext(), HistorialProductosActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
            }
            return false;
        });

        camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkCameraPermission()) {
                    openCameraForBarcodeScanning();
                } else {
                    requestCameraPermission();
                }
            }
        });



        ImagenProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeImage();
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
                    ImagenProducto.setImageBitmap(resizedImage);
                    imageToStore = resizedImage;
                } else {
                    // La imagen tiene un tamaño válido, cargarla directamente en el ImageView
                    imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    ImagenProducto.setImageBitmap(imageToStore);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            String barcode = result.getContents();
            codigo.setText(barcode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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

    private void storeImage() {
        DbProductos dbProductos = new DbProductos(CrearProductoActivity.this);
        if (!codigo.getText().toString().isEmpty() &&
                !producto.getText().toString().isEmpty() &&
                !precio.getText().toString().isEmpty() &&
                !precioCompra.getText().toString().isEmpty() &&
                !cantidad.getText().toString().isEmpty() &&
                ImagenProducto.getDrawable() != null) {

            Bitmap bitmapToStore = imageToStore != null ? imageToStore : getPredeterminedImage();
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
            // Calcular la ganancia con descuento aplicado al precio de venta
            double ganancia = (precioDouble - (precioDouble * descuentoDouble / 100)) - precioCompraDouble;

            dbProductos.storeData(new Productos(Long.parseLong(codigo.getText().toString()), bitmapToStore, producto.getText().toString(), precioDouble, precioCompraDouble, ganancia, descuentoDouble, cantidadInt));

            startActivity(new Intent(getApplicationContext(), CrearProductoActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.llenaLosDatosFaltantes, Toast.LENGTH_LONG).show();
        }
    }




    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void openCameraForBarcodeScanning() {
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
                openCameraForBarcodeScanning();
            } else {
                Toast.makeText(this, R.string.permisoCamaraDenegado, Toast.LENGTH_SHORT).show();
            }
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