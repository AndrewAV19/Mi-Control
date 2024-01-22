package com.example.micontrol;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbTalleres;
import com.example.micontrol.entidades.Talleres;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerTaller extends AppCompatActivity {

    DbHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText titulo,informacion,precio;
    ImageView Imagentaller;
    ImageButton btnCrear,btnEliminar;
    boolean correcto = false;
    Talleres talleres;
    ImageView pdf;
    int id = 0;

    private static final int PICK_IMAGE_REQUEST = 99;
    private Uri imagePath;
    private Bitmap imageToStore;
    private List<ColorModel> colorList;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isAcepteado -> {
                if (isAcepteado) Toast.makeText(this, R.string.PermisosConcedidos, Toast.LENGTH_LONG).show();
                else Toast.makeText(this, R.string.PermisosCancelados, Toast.LENGTH_LONG).show();
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_taller);

        pdf = (ImageView) findViewById(R.id.pdf);
        Imagentaller = (ImageView) findViewById(R.id.imagenTaller);
        titulo = (EditText) findViewById(R.id.titulo);
        informacion = (EditText) findViewById(R.id.informacion);
        precio = (EditText) findViewById(R.id.precio);
        btnCrear = findViewById(R.id.añadir);
        btnEliminar = findViewById(R.id.eliminar);
        dbHelper = new DbHelper(this);

        if (savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                id = Integer.parseInt(null);
            }else{
                id = extras.getInt("ID");
            }
        }else{
            id = (int) savedInstanceState.getSerializable("ID");
        }

        DbTalleres dbTalleres = new DbTalleres(VerTaller.this);
        talleres = dbTalleres.verTalleres(id);

        if (talleres != null){
            Imagentaller.setImageBitmap(talleres.getImagentaller());
            precio.setText(Double.toString(talleres.getPrecio()));
            titulo.setText(talleres.getTitulo());
            informacion.setText(talleres.getInformacion());
        }

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(findViewById(R.id.ver_t), getStartColor(AppPreferences.getSelectedColor(this)), getCenterColor(AppPreferences.getSelectedColor(this)), getEndColor(AppPreferences.getSelectedColor(this)));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        //applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(this));



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
                if (!titulo.getText().toString().isEmpty() && !informacion.getText().toString().isEmpty() && !precio.getText().toString().isEmpty()) {
                    Bitmap bitmapToStore = imageToStore != null ? imageToStore : ((BitmapDrawable) Imagentaller.getDrawable()).getBitmap();
                    double precioDouble = Double.parseDouble(precio.getText().toString());
                    correcto = dbTalleres.editarTaller(id, bitmapToStore, titulo.getText().toString(), informacion.getText().toString(), precioDouble);

                    if (correcto) {
                        Toast.makeText(VerTaller.this, R.string.TallerModificadoCorrectamente, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), EditarTallerActivity.class));
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else {
                        Toast.makeText(VerTaller.this, R.string.ErrorModificarTaller, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerTaller.this, R.string.llenaLosDatosFaltantes, Toast.LENGTH_LONG).show();
                }
            }
        });

        Imagentaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        //Para eliminar
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VerTaller.this);

                // Creamos un objeto ImageView y establecemos la imagen en él
                ImageView imageView = new ImageView(VerTaller.this);
                imageView.setImageResource(R.drawable.logomodified1);
                // Establecemos el tamaño de la imagen
                imageView.setLayoutParams(new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                // Agregamos el objeto ImageView al diseño del diálogo
                RelativeLayout layout = new RelativeLayout(VerTaller.this);
                layout.addView(imageView);

                // Agregamos el mensaje al diseño del diálogo
                TextView textView = new TextView(VerTaller.this);
                textView.setText(R.string.EliminarTaller);
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
                                if (dbTalleres.eliminarTaller(id)){
                                    startActivity(new Intent(getApplicationContext(), EditarTallerActivity.class));
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
                    Imagentaller.setImageBitmap(resizedImage);
                    imageToStore = resizedImage;
                } else {
                    // La imagen tiene un tamaño válido, cargarla directamente en el ImageView
                    imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                    Imagentaller.setImageBitmap(imageToStore);
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
            String carpeta = "/talleres_mi_control_pdf";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
                Toast.makeText(this, R.string.CarpetaCreada, Toast.LENGTH_LONG).show();
            }
            File archivo = new File(dir, talleres.getTitulo() + ".pdf");
            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter writer = PdfWriter.getInstance(documento, fos);

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onStartPage(PdfWriter writer, Document document) {
                    PdfContentByte canvas = writer.getDirectContentUnder();
                    canvas.saveState();
                    canvas.setColorFill(BaseColor.WHITE); // Establecer el color de relleno como blanco
                    canvas.rectangle(document.left(), document.bottom(), document.right(), document.top());
                    canvas.fill();
                    canvas.restoreState();
                }
            });

            documento.open();

            // Agregar la imagen al documento PDF
            if (imageToStore != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageToStore.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Image imagen = Image.getInstance(byteArray);
                imagen.scaleToFit(200, 200);

                // Alineación de la imagen a la izquierda
                imagen.setAlignment(Image.LEFT);

                documento.add(imagen);
            }

            // Crear una fuente con letras de tamaño 16 y alineadas a la izquierda
            Font contenidoFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

            // Agregar el título al documento PDF
            Font tituloFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD); // Fuente del título
            Paragraph titulo = new Paragraph(talleres.getTitulo(), tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(10); // Espacio después del título
            documento.add(titulo);

            // Agregar la información del taller
            Paragraph informacion = new Paragraph(talleres.getInformacion(), contenidoFont);
            informacion.setAlignment(Element.ALIGN_LEFT);
            documento.add(informacion);

            // Agregar un espacio después de la información
            documento.add(new Paragraph("\n"));

            // Agregar el precio al final
            String precioTexto = getString(R.string.PrecioEnPdf) + talleres.getPrecio();
            Paragraph precio = new Paragraph(precioTexto, contenidoFont);
            precio.setAlignment(Element.ALIGN_RIGHT); // Alineación a la derecha
            precio.setFont(FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD)); // Fuente en negrita
            documento.add(precio);

            // Cerrar el documento
            documento.close();

            Toast.makeText(this, R.string.ArchivoPdfCreadoCorrectamente, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(VerTaller.this);
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
        String carpeta = "/talleres_mi_control_pdf";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;
        String archivoPath = path + "/" + talleres.getTitulo() + ".pdf";

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