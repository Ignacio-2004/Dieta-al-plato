package com.tfg.dietaalplato;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class InitialLoadingLogo extends Activity {

    private TextView titleText;
    private ImageView logo;
    private final String appName = "NutriBox";
    private int index = 0;
    private final long delayMillis = 125; // Velocidad del efecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_loading_logo);

        titleText = findViewById(R.id.app_title);
        logo = findViewById(R.id.app_logo);

        startTypeWriterEffect();
    }

    private void startTypeWriterEffect() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index <= appName.length()) {
                    titleText.setText(appName.substring(0, index));
                    index++;
                    handler.postDelayed(this, delayMillis);
                } else {
                    // Mostrar el logo después de que se escriba el nombre
                    logo.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .withEndAction(() -> {
                                Intent intent = new Intent(InitialLoadingLogo.this, LogIn_Activity.class);
                                startActivity(intent);
                                finish();
                            })
                            .start();
                }
            }
        }, delayMillis);
    }
}
