package com.example.micontrol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.micontrol.adaptadores.ListaCitasAdapter;
import com.example.micontrol.db.DbCitas;
import com.example.micontrol.db.DbHelper;
import com.example.micontrol.entidades.Citas;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CitasFragment extends Fragment {

    private Calendar calendar;
    private TextView textViewDate,ingresoDiario;
    private TextView textViewNoCitas;
    private RecyclerView recyclerView;
    private ListaCitasAdapter adapter;
    private double ingresoDiarioTotal = 0.0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<ColorModel> colorList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas, container, false);

        ingresoDiario = view.findViewById(R.id.ingresoDiario);
        calendar = Calendar.getInstance();
        textViewDate = view.findViewById(R.id.textViewDate);
        recyclerView = view.findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ListaCitasAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        textViewNoCitas = view.findViewById(R.id.textViewNoCitas);


        updateCitas();
        NotificationUtils.createNotificationChannel(getActivity());

        ImageButton buttonPrev = view.findViewById(R.id.buttonPrev);
        buttonPrev.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            updateCitas();
        });

        ImageButton buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            updateCitas();
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        // Aplicar el fondo con el color guardado cuando se crea la actividad
        colorList = createColorList();
        applyBackground(view.findViewById(R.id.citas), getStartColor(AppPreferences.getSelectedColor(requireContext())), getCenterColor(AppPreferences.getSelectedColor(requireContext())), getEndColor(AppPreferences.getSelectedColor(requireContext())));
        // Cambiar el color del BottomNavigationView cuando se crea la actividad
        applyBottomNavigationColor(bottomNavigationView, AppPreferences.getSelectedColor(requireContext()));


        bottomNavigationView.setSelectedItemId(R.id.home_cita);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_cita:
                    return true;
                case R.id.crear_cita:
                    startActivity(new Intent(getActivity(), CrearCitaActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.editar_cita:
                    startActivity(new Intent(getActivity(), EditarCitaActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.eliminar_cita:
                    startActivity(new Intent(getActivity(), EstatusCitaActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
                case R.id.historial_citas:
                    startActivity(new Intent(getActivity(), HistorialCitasActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    return true;
            }
            return false;
        });

        return view;
    }

    private void updateCitas() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());
        textViewDate.setText(dateString);

        // Lógica para obtener las citas del día seleccionado y actualizar el adaptador
        DbCitas dbCitas = new DbCitas(getActivity());
        ArrayList<Citas> citasDelDia = dbCitas.obtenerCitasPorFecha(dateString);
        adapter.setCitas(citasDelDia);
        if (citasDelDia.isEmpty()) {
            textViewNoCitas.setVisibility(View.VISIBLE);
        } else {
            textViewNoCitas.setVisibility(View.GONE);
        }

        // Programar la alarma para cada cita del día
        for (Citas cita : citasDelDia) {
            String horaCita = cita.getHora(); // Obtener la hora de la cita (en formato "HH:mm")

            // Obtener la hora y minutos de la cita
            String[] partesHora = horaCita.split(":");
            int hora = Integer.parseInt(partesHora[0]);
            int minutos = Integer.parseInt(partesHora[1]);

            // Crear un objeto Calendar para la hora de la cita
            Calendar citaCalendar = Calendar.getInstance();
            citaCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hora, minutos);

            // Obtener el tiempo actual
            Calendar now = Calendar.getInstance();

            // Comprobar si la cita está en el futuro
            if (citaCalendar.after(now)) {
                // Programar la alarma para la hora de la cita
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                // Establecer la alarma para la hora de la cita
                alarmManager.set(AlarmManager.RTC_WAKEUP, citaCalendar.getTimeInMillis(), pendingIntent);
            }
        }

        // Calcular y actualizar el ingreso diario
        sumarIngresosDiarios();
    }



    private void sumarIngresosDiarios() {
        double suma = 0;
        DbHelper dbHelper = new DbHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Obtener la fecha actual en formato "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        Cursor c = db.rawQuery(
                "SELECT SUM(" + DbHelper.TABLE_SERVICIOS + ".precio) FROM " +
                        DbHelper.TABLE_SERVICIOS + " INNER JOIN " +
                        DbHelper.TABLE_CITAS + " ON " +
                        DbHelper.TABLE_SERVICIOS + ".id = " +
                        DbHelper.TABLE_CITAS + ".id_servicio WHERE " +
                        DbHelper.TABLE_CITAS + ".fecha = ?",
                new String[]{currentDate});

        if (c.moveToFirst()) {
            suma = c.getDouble(0);
        }
        c.close();
        db.close();

        ingresoDiarioTotal = suma;
        // Formatear el precio como una cantidad de dinero en pesos

        // Formatear el precio como una cantidad de dinero en pesos
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String precioFormateado = format.format(ingresoDiarioTotal);

        ingresoDiario.setText(precioFormateado);
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
                        ContextCompat.getColor(requireContext(), startColor),
                        ContextCompat.getColor(requireContext(), centerColor),
                        ContextCompat.getColor(requireContext(), endColor)

                }
        );
        view.setBackground(gradientDrawable);
        setStatusBarColor(ContextCompat.getColor(requireContext(), startColor));
    }


    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(color);
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
                        ContextCompat.getColor(requireContext(), startColor),
                        ContextCompat.getColor(requireContext(), centerColor),
                        ContextCompat.getColor(requireContext(), endColor)
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