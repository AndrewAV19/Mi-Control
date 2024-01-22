package com.example.micontrol;

import static android.app.Activity.RESULT_OK;

import static com.example.micontrol.db.DbHelper.TABLE_LOGO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.micontrol.db.DbHelper;
import com.example.micontrol.db.DbLogo;
import com.example.micontrol.db.DbServicios;
import com.example.micontrol.entidades.Logo;
import com.example.micontrol.entidades.Servicios;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private String selectedColor;
    ImageView ImagenLogo;
    private static final int PICK_IMAGE_REQUEST = 99;
    private Uri imagePath;
    private Bitmap imageToStore;
    ImageButton añadir;
    EditText name;
    DbHelper dbHelper;
    private DbLogo dbLogo;
    private Logo currentLogo;

    private List<ColorModel> colorList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);

        ImagenLogo = (ImageView) view.findViewById(R.id.imagenLogo);
        añadir = (ImageButton) view.findViewById(R.id.añadir);
        name = (EditText) view.findViewById(R.id.name);
        dbHelper = new DbHelper(requireContext());

        // Crear la lista de objetos ColorModel con la información de los colores
        colorList = createColorList();

        // Obtener el color seleccionado de SharedPreferences y aplicarlo al fondo
        selectedColor = AppPreferences.getSelectedColor(requireContext());
        applyBackground(view, getStartColor(selectedColor), getCenterColor(selectedColor), getEndColor(selectedColor));

        dbLogo = new DbLogo(requireContext());

        // Recuperar el logo existente con id = 1 desde la base de datos
        currentLogo = dbLogo.getLogo(1);
        if (currentLogo != null) {
            name.setText(currentLogo.getNombre());
            ImagenLogo.setImageBitmap(currentLogo.getImagenservicio());
        }

        // Mostrar el diálogo de selección cuando se toque el área del título del color
        TextView colorTitle = view.findViewById(R.id.colorTitle);
        colorTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorSelectionDialog();
            }
        });

        TextView backup = view.findViewById(R.id.backup);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackupConfirmationDialog();
            }
        });

        TextView ingresabackup = view.findViewById(R.id.ingresarbackup);
        ingresabackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackupSelectionDialog();
            }
        });

        ImagenLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });


        return view;
    }

    private void showBackupSelectionDialog() {
        File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiAppBackups");
        if (!backupDir.exists() || !backupDir.isDirectory()) {
            Toast.makeText(requireContext(), R.string.carpetaBackupsNoEncontrada, Toast.LENGTH_SHORT).show();
            return;
        }

        File[] backupFiles = backupDir.listFiles();
        if (backupFiles == null || backupFiles.length == 0) {
            Toast.makeText(requireContext(), R.string.Noseencontróningúnarchivodebackup, Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un diálogo de selección de archivo de backup
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.SeleccionarBackup);
        List<String> backupFileNames = new ArrayList<>();
        for (File file : backupFiles) {
            backupFileNames.add(file.getName());
        }
        builder.setItems(backupFileNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedBackupFileName = backupFileNames.get(i);
                performRestore(selectedBackupFileName);
            }
        });
        builder.show();
    }

    private void performRestore(String backupFileName) {
        boolean isRestoreSuccessful = dbHelper.restoreDatabase(requireContext(), backupFileName);
        if (isRestoreSuccessful) {
            Toast.makeText(requireContext(), R.string.Restauraciónexitosa, Toast.LENGTH_SHORT).show();
            // Puedes realizar alguna acción adicional después de la restauración, si es necesario.
        } else {
            Toast.makeText(requireContext(), R.string.Errorenlarestauración, Toast.LENGTH_SHORT).show();
        }
    }

    private void showBackupConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.ConfirmacióndeBackup);
        builder.setMessage(R.string.Deseasrealizarunbackupdelabasededatos);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performBackup();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(requireContext(), R.string.Backupcancelado, Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    // Método para realizar el backup de la base de datos

    private void performBackup() {
        boolean isBackupSuccessful = dbHelper.backupDatabase(requireContext());
        if (isBackupSuccessful) {
            Toast.makeText(requireContext(), R.string.Backupexitoso, Toast.LENGTH_SHORT).show();

            // Envío del archivo a través de WhatsApp
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MiAppBackups");
            if (backupDir.exists() && backupDir.isDirectory()) {
                File[] files = backupDir.listFiles();
                if (files != null && files.length > 0) {
                    File backupFile = files[0]; // Tomar el primer archivo (asumiendo que solo hay uno)
                    Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", backupFile);

                    // Crear el intent para enviar el archivo
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("application/octet-stream");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    sendIntent.setPackage("com.whatsapp");

                    // Pedir al usuario que seleccione a quién enviar el archivo
                    String title = getResources().getString(R.string.Enviarbackupatravésde);
                    startActivity(Intent.createChooser(sendIntent, title));
                } else {
                    Toast.makeText(requireContext(), R.string.Noseencontróelarchivodebackup, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), R.string.Carpetadebackupsnoencontrada, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), R.string.Errorenelbackup, Toast.LENGTH_SHORT).show();
        }
    }



    private void saveChanges() {
        // Guardar los cambios realizados por el usuario en el objeto currentLogo
        String nombreLogo = name.getText().toString();
        Bitmap imagenLogo = ((BitmapDrawable) ImagenLogo.getDrawable()).getBitmap();
        currentLogo.setNombre(nombreLogo);
        currentLogo.setImagenservicio(imagenLogo);

        // Guardar el logo actualizado en la base de datos
        boolean logoActualizado = dbLogo.editarLogo(currentLogo);

        if (logoActualizado) {
            EventBus.getDefault().post(new LogoChangeEvent(nombreLogo, imagenLogo));
            Toast.makeText(requireContext(), R.string.cambiosGuardados, Toast.LENGTH_SHORT).show();
        }
    }


    private void chooseImage() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imagePath = data.getData();

                // Obtener información del tamaño de la imagen sin cargarla completamente
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                requireContext().getContentResolver().openInputStream(imagePath).close(); // Agregamos este close() para evitar una posible fuga de recursos
                BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imagePath), null, options);

                // Verificar el tamaño de la imagen
                if (options.outWidth > 1000 || options.outHeight > 1000) {
                    // Reducir el tamaño de la imagen antes de cargarla en el ImageView
                    options.inSampleSize = calculateInSampleSize(options, 1000, 1000);
                    options.inJustDecodeBounds = false;
                    Bitmap resizedImage = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imagePath), null, options);
                    ImagenLogo.setImageBitmap(resizedImage);
                    imageToStore = resizedImage;

                } else {
                    // La imagen tiene un tamaño válido, cargarla directamente en el ImageView
                    imageToStore = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imagePath));
                    ImagenLogo.setImageBitmap(imageToStore);

                }
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedColor = parent.getItemAtPosition(position).toString();
        AppPreferences.saveSelectedColor(requireContext(), selectedColor);

        // Publicar el evento de cambio de color (Puedes eliminar esta línea si no es necesario)
        EventBus.getDefault().post(new ColorChangeEvent(selectedColor));

        // Aplicar el color seleccionado al fondo
        applyBackground(getView(), getStartColor(selectedColor), getCenterColor(selectedColor), getEndColor(selectedColor));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No hacer nada
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


    private void applyBackground(View view, int startColor, int centerColor, int endColor) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(requireContext(), startColor),
                        ContextCompat.getColor(requireContext(), centerColor),
                        ContextCompat.getColor(requireContext(), endColor)
                }
        );
        view.setBackground(gradientDrawable);
    }

    // Método para mostrar el diálogo de selección de color
    private void showColorSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.seleccionaColor);

        // Obtener la lista de nombres de colores a mostrar en el diálogo
        String[] colorNames = new String[colorList.size()];
        for (int i = 0; i < colorList.size(); i++) {
            colorNames[i] = colorList.get(i).getColorName();
        }

        builder.setItems(colorNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // El usuario ha seleccionado un color
                        selectedColor = colorList.get(which).getColorName();
                        AppPreferences.saveSelectedColor(requireContext(), selectedColor);
                        applyBackground(getView(), getStartColor(selectedColor), getCenterColor(selectedColor), getEndColor(selectedColor));
                        applyTheme(); // Actualiza el tema de la aplicación con el nuevo color
                    }
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }



    private void applyTheme() {
        int themeResId;
        int statusBarColorResId;
        int dialogThemeResId; // Nuevo variable para almacenar el DialogTheme correspondiente

        switch (selectedColor) {
            case "Café":
                themeResId = R.style.Theme_MiControl_Cafe;
                statusBarColorResId = R.color.cafeUno;
                dialogThemeResId = R.style.DialogTheme_Cafe; // Usa el nuevo estilo para el diálogo
                break;
            case "Azul":
                themeResId = R.style.Theme_MiControl_Azul;
                statusBarColorResId = R.color.azulUno;
                dialogThemeResId = R.style.DialogTheme_Azul; // Usa el nuevo estilo para el diálogo
                break;
            case "Gris":
                themeResId = R.style.Theme_MiControl_Gris;
                statusBarColorResId = R.color.grisUno;
                dialogThemeResId = R.style.DialogTheme_Gris; // Usa el nuevo estilo para el diálogo
                break;
            case "Verde":
                themeResId = R.style.Theme_MiControl_Verde;
                statusBarColorResId = R.color.verdeUno;
                dialogThemeResId = R.style.DialogTheme_Verde; // Usa el nuevo estilo para el diálogo
                break;
            case "Morado":
                themeResId = R.style.Theme_MiControl_Morado;
                statusBarColorResId = R.color.moradoUno;
                dialogThemeResId = R.style.DialogTheme_Morado; // Usa el nuevo estilo para el diálogo
                break;
            case "Rosa":
                themeResId = R.style.Theme_MiControl_Rosa;
                statusBarColorResId = R.color.rosaUno;
                dialogThemeResId = R.style.DialogTheme_Rosa; // Usa el nuevo estilo para el diálogo
                break;

            default:
                themeResId = R.style.Theme_MiControl;
                statusBarColorResId = R.color.colorUno;
                dialogThemeResId = R.style.DialogTheme; // Usa el estilo original para el diálogo
                break;
        }
        requireActivity().setTheme(themeResId);
        setStatusBarColor(statusBarColorResId);
        // Asigna el nuevo estilo para el DialogTheme
        setDialogTheme(dialogThemeResId);

        EventBus.getDefault().post(new ColorChangeEvent(selectedColor));
    }

    private void setStatusBarColor(@ColorRes int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(requireContext(), colorResId);
            Window window = requireActivity().getWindow();
            window.setStatusBarColor(color);
        }
    }



    private void setDialogTheme(int themeResId) {
        if (getActivity() != null) {
            // Crea un nuevo contexto con el nuevo tema para el diálogo
            ContextThemeWrapper newContext = new ContextThemeWrapper(getActivity(), themeResId);

            // Reinicializa los diálogos con el nuevo tema
            AlertDialog.Builder builder = new AlertDialog.Builder(newContext);

            // Configura el diálogo aquí, si es necesario
            // Por ejemplo, si tienes botones o contenido personalizado en el diálogo, configúralos aquí.

            // Mostrar el diálogo actualizado
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }




}
