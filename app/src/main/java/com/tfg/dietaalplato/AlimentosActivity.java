package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AlimentosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alimentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickReturn(View view){
        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        int dietaSeleccionada = getIntent().getIntExtra("dietaSeleccionada", 1);
        int diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 1);
        String comidaSeleccionada = getIntent().getStringExtra("comidaSeleccionada");

        Intent intent = new Intent(this, ComidasActivity.class);;
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        intent.putExtra("diaSeleccionado", diaSeleccionado);
        intent.putExtra("comidaSeleccionada", comidaSeleccionada);

        startActivity(intent);
    }
}