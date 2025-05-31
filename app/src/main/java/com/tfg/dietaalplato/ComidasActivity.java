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
    ImageButton botonDesayuno, botonAlmuerzo, botonComida, botonMerienda, botonCena, botonRecena;

    // Datos de la actividad
    private boolean esAdmin;
    private String idUser;
    private String clienteSeleccionado;
    private int dietaSeleccionada;
    private int diaSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);

        // Obtener datos del Intent
        esAdmin = getIntent().getBooleanExtra("esAdmin", false);

        if(esAdmin) {
            idUser = getIntent().getStringExtra("usuarioSeleccionado");
        }

        clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);
        diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 1);

        initViews();
        setupUI();
        setupButtonListeners();
    }

    private void initViews() {
        diaText = findViewById(R.id.titulo_textview);
        botonDesayuno = findViewById(R.id.desayuno_boton);
        botonAlmuerzo = findViewById(R.id.almuerzo_boton);
        botonComida = findViewById(R.id.comida_boton);
        botonMerienda = findViewById(R.id.merienda_boton);
        botonCena = findViewById(R.id.cena_boton);
        botonRecena = findViewById(R.id.recena_boton);
    }

    private void setupUI() {
        if (dietaSeleccionada == 1) {
            diaText.setText("¿Qué comida quieres visualizar?");
        } else {
            diaText.setText(String.format("¿Qué comida quieres visualizar del día %d?", diaSeleccionado));
        }
    }

    private void setupButtonListeners() {
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
        Intent intent = createBaseIntent(AlimentosActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        intent.putExtra("comidaSeleccionada", comidaSeleccionada);
        startActivity(intent);
    }

    private Intent createBaseIntent(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.putExtra("esAdmin", esAdmin);
        intent.putExtra("usuarioSeleccionado", idUser);
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        return intent;
    }

    public void onClickBackNavigation(View view) {
        Intent intent;
        if (dietaSeleccionada == 1) {
            intent = createBaseIntent(DietasActivity.class);
        } else {
            intent = createBaseIntent(DiasActivity.class);
            intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        }
        startActivity(intent);
    }

}