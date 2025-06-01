package com.tfg.dietaalplato;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.EditarBancoAlimentos_Dialogo;
import com.tfg.dietaalplato.utilities.dialogo.GuardarBancoAlimentos_Dialogo;

public class BancoAlimentosActivity extends AppCompatActivity {

    private LinearLayout layoutSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banco_alimentos);

        layoutSV = findViewById(R.id.layoutAlimentos);
        SaveData saveData = SaveData.getInstance();

        try{
            Blocker.createBlocker(this.findViewById(android.R.id.content),this);
            FireBaseReader.readAllFoodFromUser(saveData.getUser().getId().toUpperCase()).addOnSuccessListener(
                    alimentos -> {
                        if (alimentos.exit){
                            for (Food alimento : alimentos.result.values()) {
                                LinearLayout item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.HORIZONTAL);
                                item.setPadding(12, 5, 5, 12);
                                item.setElevation(8f);
                                layoutSV.addView(item);


                                item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.HORIZONTAL);
                                item.setBackgroundResource(R.drawable.bg_food_background);
                                item.setPadding(12, 12, 12, 12);
                                item.setElevation(8f);

                                // esto es para cuando haga click en el alimento
                                item.setOnClickListener(v -> {
                                    EditarBancoAlimentos_Dialogo dialogo = EditarBancoAlimentos_Dialogo.newInstance(alimento);
                                    dialogo.show(getSupportFragmentManager(), "editarAlimento");
                                });

                                TextView nombre = new TextView(this);
                                nombre.setText(alimento.getName().substring(0, 1).toUpperCase() + alimento.getName().substring(1).toLowerCase());
                                nombre.setTextColor(Color.WHITE);
                                nombre.setTextSize(30);
                                nombre.setGravity(Gravity.CENTER);
                                nombre.setTypeface(null, Typeface.BOLD);
                                nombre.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                item.addView(nombre);
                                layoutSV.addView(item);
                            }
                        }
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }
            ).addOnFailureListener(
                    e -> {
                        Toast.makeText(this, "Error al cargar alimentos", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }
            );
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar alimentos", Toast.LENGTH_SHORT).show();
            Blocker.removeBlocker(this.findViewById(android.R.id.content));
        }

    }


    // metodo para mostrar el dialogo cuando se hace clic en INSERTAR ALIMENTO
    public void insertarAlimento(View view) {
        GuardarBancoAlimentos_Dialogo dialogo = new GuardarBancoAlimentos_Dialogo();
        dialogo.show(getSupportFragmentManager(), "dialogoNuevoAlimento");
    }
    public void onClickBackNavigation(View view){
        Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);
    }
    public void actualizarBancoAlimentos(View view){
        Intent intent = new Intent(this, BancoAlimentosActivity.class );
        startActivity(intent);
    }
}