package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.BancoAlimentos_Dialogo;

public class BancoAlimentosActivity extends AppCompatActivity {

    private LinearLayout layoutSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banco_alimentos);

        layoutSV = findViewById(R.id.layoutAlimentos);
        SaveData saveData = SaveData.getInstance();

        try{
            FireBaseReader.readAllFoodFromUser(saveData.getUser().getId().toUpperCase()).addOnSuccessListener(
                    alimentos -> {
                        if (alimentos.exit){
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


    // metodo para mostrar el dialogo cuando se hace clic en INSERTAR ALIMENTO
    public void insertarAlimento(View view) {
        BancoAlimentos_Dialogo dialogo = new BancoAlimentos_Dialogo();
        dialogo.show(getSupportFragmentManager(), "dialogoNuevoAlimento");
    }
    public void onClickBackNavigation(View view){
        Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);
    }
}