package com.tfg.dietaalplato;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.utilities.SaveData;

public class AlimentosSV extends AppCompatActivity {

    private LinearLayout layoutSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos_sv);

        layoutSV = findViewById(R.id.layoutAlimentos);
        SaveData saveData = SaveData.getInstance();

        try{
            FireBaseReader.readAllFoodFromUser(saveData.getUser().getId()).addOnSuccessListener(
                    alimentos -> {
                        for (Food alimento : alimentos.result.values()) {
                            Button btn = new Button(this);
                            btn.setText(alimento.getName());
                            btn.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT)
                            );

                            btn.setOnClickListener(v -> {
                                Toast.makeText(this, "Alimento seleccionado: " + alimento.getName(), Toast.LENGTH_SHORT).show();
                            });
                            layoutSV.addView(btn);
                        }
                    }
            ).addOnFailureListener(
                    e -> {
                        Toast.makeText(this, "Error al cargar alimentos", Toast.LENGTH_SHORT).show();
                    }
            );
        } catch (FBCException e) {
            throw new RuntimeException(e);
        }

    }
}