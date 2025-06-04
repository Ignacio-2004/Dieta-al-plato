package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tfg.dietaalplato.utilities.DietaTableGenerator;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DietaIDia extends AppCompatActivity {

    SaveData saveData = SaveData.getInstance();
    private LinearLayout layoutSV;
    DietaTableGenerator generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dieta_dia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Simulamos datos de prueba
        List<List<String>> datosPrueba = new ArrayList<>();
        datosPrueba.add(Arrays.asList("Receta 1", "Pollo, arroz", "500", "30", "20", "40"));
        datosPrueba.add(Arrays.asList("Receta 2", "Pescado, patata", "400", "25", "15", "30"));

        // Llamamos a la clase generadora
        DietaTableGenerator.generarTabla(this, findViewById(R.id.layoutTabla), datosPrueba);

    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, ComidasActivity.class);
        startActivity(intent);
    }

    public void just(View view){
        DialogAddJustification dialog = new DialogAddJustification();
        dialog.show(getSupportFragmentManager(), "DialogAddJustification");
    }
}