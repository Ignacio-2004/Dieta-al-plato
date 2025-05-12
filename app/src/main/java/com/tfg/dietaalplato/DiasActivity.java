package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;

public class DiasActivity extends AppCompatActivity {

    Button botonDia1, botonDia2, botonDia3, botonDia4, botonDia5, botonDia6, botonDia7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dias);

        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);

        // Obtener los botones de los días
        Button[] botonesDias = new Button[7];
        botonesDias[0] = findViewById(R.id.botonDia1);
        botonesDias[1] = findViewById(R.id.botonDia2);
        botonesDias[2] = findViewById(R.id.botonDia3);
        botonesDias[3] = findViewById(R.id.botonDia4);
        botonesDias[4] = findViewById(R.id.botonDia5);
        botonesDias[5] = findViewById(R.id.botonDia6);
        botonesDias[6] = findViewById(R.id.botonDia7);

        // Hacer visibles solo los botones correspondientes
        for (int i = 0; i < dietaSeleccionada; i++) {
            botonesDias[i].setVisibility(View.VISIBLE);
            final int dia = i + 1;  // El índice es 0-based, por eso sumamos 1
            botonesDias[i].setOnClickListener(v -> abrirComidasActivity(dia, dietaSeleccionada));
        }

    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, DietasActivity.class );
        startActivity(intent);
    }
}