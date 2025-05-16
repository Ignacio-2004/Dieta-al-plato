package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comidas);

        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);
        int diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 1);

        diaText = findViewById(R.id.titulo_textview);
        if (dietaSeleccionada == 1) {
            diaText.setText("¿Qué comida quieres visualizar?");
        }
        else {
            diaText.setText("¿Qué comida quieres visualizar del día " + diaSeleccionado + "?");
        }
    }

    public void onClickReturn(View view){
        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);

        Intent intent;
        if(dietaSeleccionada == 1) {
            intent = new Intent(this, DietasActivity.class);
        }
        else {
            intent = new Intent(this, DiasActivity.class);
        }
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }

}