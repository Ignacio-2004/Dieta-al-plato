package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.utilities.SaveData;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText;
    ImageButton botonDesayuno, botonAlmuerzo, botonComida, botonMerienda, botonCena, botonRecena;
    private SaveData saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        diaText = findViewById(R.id.titulo_textview);
        botonDesayuno = findViewById(R.id.desayuno_boton);
        botonAlmuerzo = findViewById(R.id.almuerzo_boton);
        botonComida = findViewById(R.id.comida_boton);
        botonMerienda = findViewById(R.id.merienda_boton);
        botonCena = findViewById(R.id.cena_boton);
        botonRecena = findViewById(R.id.recena_boton);

        if (saveData.getCurrentDiet().getTip().equals("1")) {
            diaText.setText("¿Qué comida quieres visualizar?");
        } else {
            diaText.setText(String.format("¿Qué comida quieres visualizar del día %d?", saveData.getCurrentDay()));
        }

        View.OnClickListener comidaClickListener = v -> {
            String tipoComida;
            int id = v.getId();

            if (id == R.id.desayuno_boton) {
                tipoComida = "desayuno";
            } else if (id == R.id.almuerzo_boton) {
                tipoComida = "almuerzo";
            } else if (id == R.id.comida_boton) {
                tipoComida = "comida";
            } else if (id == R.id.merienda_boton) {
                tipoComida = "merienda";
            } else if (id == R.id.cena_boton) {
                tipoComida = "cena";
            } else if (id == R.id.recena_boton) {
                tipoComida = "recena";
            } else {
                tipoComida = "";
            }
            abrirAlimentosActivity(tipoComida);
        };

        botonDesayuno.setOnClickListener(comidaClickListener);
        botonAlmuerzo.setOnClickListener(comidaClickListener);
        botonComida.setOnClickListener(comidaClickListener);
        botonMerienda.setOnClickListener(comidaClickListener);
        botonCena.setOnClickListener(comidaClickListener);
        botonRecena.setOnClickListener(comidaClickListener);
    }

    private void abrirAlimentosActivity(String comidaSeleccionada) {
        Intent intent = new Intent(this, DietaIDia.class );
        startActivity(intent);
    }

    public void onClickBackNavigation(View view) {
        Intent intent = new Intent(this, DiasActivity.class );
        startActivity(intent);
    }

}