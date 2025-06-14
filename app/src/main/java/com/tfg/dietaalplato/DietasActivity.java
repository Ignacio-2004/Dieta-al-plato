package com.tfg.dietaalplato;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.DailyNutrition;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.ClientInfo_Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DietasActivity extends AppCompatActivity {

    private SaveData saveData;
    ImageView imagenCliente;
    Button boton1dias, boton3dias, boton7dias;
    ImageButton botonimage1dias, botonimage3dias, botonimage7dias;
    TextView nombreClienteText;
    private static final String msgErrorRepeatDiet = "Ya existe una dieta con las mismas credenciales, para modificarla entre en la ficha de la dieta.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas);

        saveData = SaveData.getInstance();
        imagenCliente = findViewById(R.id.imgCliente);
        nombreClienteText = findViewById(R.id.nombrePaciente_textview);
        nombreClienteText.setText((saveData.getCurrentClient().getName().substring(0, 1).toUpperCase() + saveData.getCurrentClient().getName().substring(1)));

        // Referenciar los botones
        boton1dias = findViewById(R.id.boton1dias_button);
        boton3dias = findViewById(R.id.boton3dias_button);
        boton7dias = findViewById(R.id.boton7dias_button);

        botonimage1dias = findViewById(R.id.boton1dias_imagebutton);
        botonimage3dias = findViewById(R.id.boton3dias_imagebutton);
        botonimage7dias = findViewById(R.id.boton7dias_imagebutton);

        // Configurar listeners para los botones
        View.OnClickListener listener1Dia = v -> abrirDiasActivity(1);
        View.OnClickListener listener3Dias = v -> abrirDiasActivity(3);
        View.OnClickListener listener7Dias = v -> abrirDiasActivity(7);

        boton1dias.setOnClickListener(listener1Dia);
        boton3dias.setOnClickListener(listener3Dias);
        boton7dias.setOnClickListener(listener7Dias);

        botonimage1dias.setOnClickListener(listener1Dia);
        botonimage3dias.setOnClickListener(listener3Dias);
        botonimage7dias.setOnClickListener(listener7Dias);

        // Listeners para la información del cliente
        View.OnClickListener infoListener = v -> onClickOpenInfo(v, callback -> {
            super.onRestart();
        });
        imagenCliente.setOnClickListener(infoListener);
        nombreClienteText.setOnClickListener(infoListener);
    }

    private void abrirDiasActivity( int dietaSeleccionada) {
        Blocker.createBlocker(this.findViewById(android.R.id.content), this);
        ArrayList<String> data = new ArrayList<>();
        data.add(String.valueOf(dietaSeleccionada));
        data.add("null");
        ValidationResult result = Diet.toMapData(data, saveData.getCurrentClient().getId());

        FireBaseWriter.saveData(Diet.class, result).addOnSuccessListener(
                validationResult -> {
                    try {
                        Diet diet = (Diet) validationResult.result;
                        saveData.setCurrentDiet(diet);

                        if (diet.getTip().equals("1")) {
                            // Cargar alimentos y nutrición para dieta de 1 día
                            loadDayNutrition(diet.getId(), "1", () -> {
                                Blocker.removeBlocker(this.findViewById(android.R.id.content));
                                saveData.setCurrentDay(1);
                                Intent intent = new Intent(this, ComidasActivity.class);
                                startActivity(intent);
                            });
                        } else {
                            Blocker.removeBlocker(this.findViewById(android.R.id.content));
                            Intent intent = new Intent(this, DiasActivity.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }
                }
        ).addOnFailureListener(
                e -> {
                    if (!((ComplexFBCE) e).reason.message.equals(msgErrorRepeatDiet)) {
                        Toast.makeText(this, "Error al guardar la dieta", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }else{
                        try{
                            Map<String,String> map = (Map<String, String>) ((ComplexFBCE) e).reason.result;
                            Diet diet = new Diet(map.get("id"),map.get("name"),map.get("tip"),map.get("idCli"),map.get("just"));

                            Blocker.removeBlocker(this.findViewById(android.R.id.content));
                            Intent intent;
                            if (!Objects.equals(diet.getTip(), "1")){
                                intent = new Intent(this, DiasActivity.class);
                            }else{
                                saveData.setCurrentDay(1);
                                intent = new Intent(this, ComidasActivity.class);
                            }

                            saveData.setCurrentDiet(diet);

                            startActivity(intent);

                        }catch (Exception e2){
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                            Blocker.removeBlocker(this.findViewById(android.R.id.content));
                        }
                    }
                }
        );
    }

    private void loadDayNutrition(String dietId, String day, Runnable onComplete) {
        try {
            FireBaseReader.readFoodsForDay(dietId, Integer.parseInt(day))
                    .addOnSuccessListener(foodResult -> {
                        if (foodResult.isSuccess() && !foodResult.result.isEmpty()) {
                            calculateDailyNutrition(foodResult.result, day);
                        }
                        onComplete.run();
                    })
                    .addOnFailureListener(e -> onComplete.run());
        } catch (FBCException e) {
            onComplete.run();
        }
    }

    private void calculateDailyNutrition(List<FoodDiet> foodDiets, String day) {
        DailyNutrition dailyNutrition = new DailyNutrition();

        for (FoodDiet foodDiet : foodDiets) {
            try {
                // Asumiendo que tienes los alimentos en caché o los puedes obtener
                Food food = saveData.getFoods().get(foodDiet.getIdAlimento());
                if (food != null) {
                    dailyNutrition.addFood(food);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener alimento: " + e.getMessage());
            }
        }

        saveData.setNutritionForDay(day, dailyNutrition);
    }


    public void onClickBackNavigation(View view){
        Intent intent = new Intent(this, PacientesActivity.class );
        startActivity(intent);
    }


    public void onClickOpenInfo(View view, OnResultCallBack<Boolean> callback){
        ClientInfo_Dialog dialogo = ClientInfo_Dialog.getInstance(true);
        dialogo.show(getSupportFragmentManager(), "dialogoInfoCliente");
    }
}
