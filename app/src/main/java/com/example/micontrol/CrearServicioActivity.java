package com.example.micontrol;

import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbServicios;
import com.example.micontrol.entidades.Servicios;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CrearServicioActivity extends AppCompatActivity {

    DbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText servicio,precio;
    Button cambiar_imagen;
    ImageButton añadir;
    Button cambiar;
    ImageView Imagenservicio;
    int id = 0;

    private static final int PICK_IMAGE_REQUEST = 99;

    private Uri imagePath;
    private  Bitmap imageToStore;
    private List<ColorModel> colorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_servicio);

        Imagenservicio = (ImageView) findViewById(R.id.imagenLogo);
        servicio = (EditText) findViewById(R.id.servicio);
        precio = (EditText) findViewById(R.id.precio);
        añadir = (ImageButton) findViewById(R.id.añadir);
        dbHelper = new DbHelper(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.crear_s), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));



        bottomNavigationView.setSelectedItemId(R.id.crear_servicio);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_servicio:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("fragmentToLoad", "servicios");
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.crear_servicio:
                    return true;
                case R.id.editar_servicio:
                    startActivity(new Intent(getApplicationContext(), EditarServicioActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
                case R.id.historial_servicios:
                    startActivity(new Intent(getApplicationContext(), HistorialServiciosActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                    return true;
            }
            return false;
        });

        Imagenservicio.setOnClickListener(new View.OnClickListener() {
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
                    Imagenservicio.setImageBitmap(resizedImage);
                    imageToStore = resizedImage;
                } else {
                    // La imagen tiene un tamaño válido, cargarla directamente en el ImageView
                    imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    Imagenservicio.setImageBitmap(imageToStore);
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

    private void storeImage() {
        DbServicios dbServicios = new DbServicios(CrearServicioActivity.this);
        if (!servicio.getText().toString().isEmpty() && !precio.getText().toString().isEmpty() && Imagenservicio.getDrawable() != null) {
            Bitmap bitmapToStore = imageToStore != null ? imageToStore : getPredeterminedImage();
            double precioDouble = Double.parseDouble(precio.getText().toString());
            dbServicios.storeData(new Servicios(bitmapToStore, servicio.getText().toString(), precioDouble));

             startActivity(new Intent(getApplicationContext(), CrearServicioActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),R.string.llenaLosDatosFaltantes,Toast.LENGTH_LONG).show();
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