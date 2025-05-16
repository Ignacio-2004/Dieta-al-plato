package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DietasActivity extends AppCompatActivity {

    Button boton1dias, boton3dias, boton7dias;
    ImageButton botonimage1dias, botonimage3dias, botonimage7dias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas);

        // Referenciar los botones
        boton1dias = findViewById(R.id.boton1dias_button);
        boton3dias = findViewById(R.id.boton3dias_button);
        boton7dias = findViewById(R.id.boton7dias_button);

        botonimage1dias = findViewById(R.id.boton1dias_imagebutton);
        botonimage3dias = findViewById(R.id.boton3dias_imagebutton);
        botonimage7dias = findViewById(R.id.boton7dias_imagebutton);


        boton1dias.setOnClickListener(v -> abrirComidasActivity());
        boton3dias.setOnClickListener(v -> abrirDiasActivity(3));
        boton7dias.setOnClickListener(v -> abrirDiasActivity(7));

        botonimage1dias.setOnClickListener(v -> abrirComidasActivity());
        botonimage3dias.setOnClickListener(v -> abrirDiasActivity(3));
        botonimage7dias.setOnClickListener(v -> abrirDiasActivity(7));
    }

    private void abrirDiasActivity(int dietaSeleccionada) {
        Intent intent = new Intent(this, DiasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }

    private void abrirComidasActivity() { // en caso de que se seleccione la dieta de 1 día, pasará directamente a la ventana de comidas
        Intent intent = new Intent(this, ComidasActivity.class);
        intent.putExtra("dietaSeleccionada", 1);
        startActivity(intent);
    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);
    }
}