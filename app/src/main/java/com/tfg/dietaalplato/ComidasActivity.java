package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText;
    ImageButton botonimageDesayuno, botonimageAlmuerzo, botonimageComida, botonimageMerienda, botonimageCena, botonimageRecena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comidas);

        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);
        int diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 1);
        String comidaSeleccionada = getIntent().getStringExtra("comidaSeleccionada");

        botonimageDesayuno = findViewById(R.id.desayuno_boton);
        botonimageAlmuerzo = findViewById(R.id.almuerzo_boton);
        botonimageComida = findViewById(R.id.comida_boton);
        botonimageMerienda = findViewById(R.id.merienda_boton);
        botonimageCena = findViewById(R.id.cena_boton);
        botonimageRecena = findViewById(R.id.recena_boton);

        botonimageDesayuno.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "desayuno"));
        botonimageAlmuerzo.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "almuerzo"));
        botonimageComida.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "comida"));
        botonimageMerienda.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "merienda"));
        botonimageCena.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "cena"));
        botonimageRecena.setOnClickListener(v -> abrirAlimentosActivity(clienteSeleccionado, dietaSeleccionada, diaSeleccionado, "recena"));

        diaText = findViewById(R.id.titulo_textview);
        if (dietaSeleccionada == 1) {
            diaText.setText("¿Qué comida quieres visualizar?");
        }
        else {
            diaText.setText("¿Qué comida quieres visualizar del día " + diaSeleccionado + "?");
        }
    }

    private void abrirAlimentosActivity(String clienteSeleccionado, int dietaSeleccionada, int diaSeleccionado, String comidaSeleccionada) {
        Intent intent = new Intent(this, AlimentosActivity.class);
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        intent.putExtra("comidaSeleccionada", comidaSeleccionada);
        startActivity(intent);
    }

    public void onClickReturn(View view){
        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);
        int diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 1);

        Intent intent;
        if(dietaSeleccionada == 1) {
            intent = new Intent(this, DietasActivity.class);
        }
        else {
            intent = new Intent(this, DiasActivity.class);
        }
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        startActivity(intent);
    }

}