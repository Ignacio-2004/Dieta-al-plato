package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DiasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dias);

        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);

        // Obtener los botones de los días
        Button[] botonesDias3 = new Button[3];
        botonesDias3[0] = findViewById(R.id.boton1Dias3);
        botonesDias3[1] = findViewById(R.id.boton2Dias3);
        botonesDias3[2] = findViewById(R.id.boton3Dias3);

        Button[] botonesDias7 = new Button[7];
        botonesDias7[0] = findViewById(R.id.boton1Dias7);
        botonesDias7[1] = findViewById(R.id.boton2Dias7);
        botonesDias7[2] = findViewById(R.id.boton3Dias7);
        botonesDias7[3] = findViewById(R.id.boton4Dias7);
        botonesDias7[4] = findViewById(R.id.boton5Dias7);
        botonesDias7[5] = findViewById(R.id.boton6Dias7);
        botonesDias7[6] = findViewById(R.id.boton7Dias7);

        if(dietaSeleccionada == 3) {
            for (int i = 0; i < 3; i++) {
                botonesDias3[i].setVisibility(View.VISIBLE);
                final int dia = i + 1;  // El índice es 0-based, por eso sumamos 1
                botonesDias3[i].setOnClickListener(v -> abrirComidasActivity(dia, dietaSeleccionada));
            }
            for (int i = 0; i < 7; i++) {
                botonesDias7[i].setVisibility(View.INVISIBLE);
            }
        }
        if(dietaSeleccionada == 7) {
            for (int i = 0; i < 7; i++) {
                botonesDias7[i].setVisibility(View.VISIBLE);
                final int dia = i + 1;  // El índice es 0-based, por eso sumamos 1
                botonesDias7[i].setOnClickListener(v -> abrirComidasActivity(dia, dietaSeleccionada));
            }
            for (int i = 0; i < 7; i++) {
                botonesDias3[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    private void abrirComidasActivity(int diaSeleccionado, int dietaSeleccionada) {
        Intent intent = new Intent(this, DiasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        startActivity(intent);
    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, DietasActivity.class );
        startActivity(intent);
    }
}