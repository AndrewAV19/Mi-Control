package com.example.micontrol;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CustomCaptureActivity extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Configurar orientación vertical
    }
}
