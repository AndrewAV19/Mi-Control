package com.example.micontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = context.getString(R.string.recordatorio);
        String mensaje = context.getString(R.string.tienescitaprogramada);

        NotificationUtils.showNotification(context, titulo, mensaje);
    }
}
