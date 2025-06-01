package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DiasActivity extends AppCompatActivity {

    private boolean esAdmin;
    private String idUser;
    private String clienteSeleccionado;
    private int dietaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dias);

        // Obtener datos del Intent
        esAdmin = getIntent().getBooleanExtra("esAdmin", false);

        if(esAdmin) {
            idUser = getIntent().getStringExtra("usuarioSeleccionado");
        }

        clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 3);

        // Obtener los botones de los días
        Button[] botonesDias3 = new Button[3];
        botonesDias3[0] = findViewById(R.id.boton1Dias3);
        botonesDias3[1] = findViewById(R.id.boton2Dias3);
        botonesDias3[2] = findViewById(R.id.boton3Dias3);


        setupDayButtons();
    }

    private void setupDayButtons() {
        // Ocultar todos los botones primero
        hideAllDayButtons();

        // Mostrar solo los botones necesarios según el tipo de dieta
        if (dietaSeleccionada == 3) {
            setupButtonsFor3DayDiet();
        } else if (dietaSeleccionada == 7) {
            setupButtonsFor7DayDiet();
        }
    }

    private void hideAllDayButtons() {
        int[] buttonIds3Days = {R.id.boton1Dias3, R.id.boton2Dias3, R.id.boton3Dias3};
        int[] buttonIds7Days = {R.id.boton1Dias7, R.id.boton2Dias7, R.id.boton3Dias7, R.id.boton4Dias7, R.id.boton5Dias7, R.id.boton6Dias7, R.id.boton7Dias7};

        setButtonsVisibility(buttonIds3Days, View.INVISIBLE);
        setButtonsVisibility(buttonIds7Days, View.INVISIBLE);
    }

    private void setupButtonsFor3DayDiet() {
        int[] buttonIds = {R.id.boton1Dias3, R.id.boton2Dias3, R.id.boton3Dias3};
        setButtonsVisibility(buttonIds, View.VISIBLE);

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int dia = i + 1;
            button.setOnClickListener(v -> abrirComidasActivity(dia));
        }
    }

    private void setupButtonsFor7DayDiet() {
        int[] buttonIds = {R.id.boton1Dias7, R.id.boton2Dias7, R.id.boton3Dias7,
                R.id.boton4Dias7, R.id.boton5Dias7, R.id.boton6Dias7,
                R.id.boton7Dias7};
        setButtonsVisibility(buttonIds, View.VISIBLE);

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int dia = i + 1;
            button.setOnClickListener(v -> abrirComidasActivity(dia));
        }
    }

    private void setButtonsVisibility(int[] buttonIds, int visibility) {
        for (int id : buttonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setVisibility(visibility);
            }
        }
    }

    private void abrirComidasActivity(int diaSeleccionado) {
        Intent intent = createBaseIntent(ComidasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        startActivity(intent);
    }

    private Intent createBaseIntent(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.putExtra("esAdmin", esAdmin);
        intent.putExtra("usuarioSeleccionado", idUser);
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        return intent;
    }


    public void onClickBackNavigation(View view){
        Intent intent = createBaseIntent(DietasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }
}
