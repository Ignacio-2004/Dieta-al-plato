package com.tfg.dietaalplato;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.DailyNutrition;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;

import java.util.List;

public class DiasActivity extends AppCompatActivity {

    private SaveData saveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dias);
        saveData = SaveData.getInstance();

        // Obtener los botones de los días
        Button[] botonesDias3 = new Button[3];
        botonesDias3[0] = findViewById(R.id.boton1Dias3);
        botonesDias3[1] = findViewById(R.id.boton2Dias3);
        botonesDias3[2] = findViewById(R.id.boton3Dias3);

        setupDayButtons();
        /*
            ++ Añadimos esto para saber que dia ha elegido
         */
        findViewById(R.id.boton1Dias3).setOnClickListener(v -> abrirComidasActivity(1));
        findViewById(R.id.boton2Dias3).setOnClickListener(v -> abrirComidasActivity(2));
        findViewById(R.id.boton3Dias3).setOnClickListener(v -> abrirComidasActivity(3));
        findViewById(R.id.boton1Dias7).setOnClickListener(v -> abrirComidasActivity(1));
        findViewById(R.id.boton2Dias7).setOnClickListener(v -> abrirComidasActivity(2));
        findViewById(R.id.boton3Dias7).setOnClickListener(v -> abrirComidasActivity(3));
        findViewById(R.id.boton4Dias7).setOnClickListener(v -> abrirComidasActivity(4));
        findViewById(R.id.boton5Dias7).setOnClickListener(v -> abrirComidasActivity(5));
        findViewById(R.id.boton6Dias7).setOnClickListener(v -> abrirComidasActivity(6));

    }

    private void setupDayButtons() {
        // Ocultar todos los botones primero
        hideAllDayButtons();

        // Mostrar solo los botones necesarios según el tipo de dieta
        if (saveData.getCurrentDiet().getTip().equals("3")) {
            setupButtonsFor3DayDiet();
        } else if (saveData.getCurrentDiet().getTip().equals("7")) {
            setupButtonsFor7DayDiet();
        }
    }

    public void just(View view){
        DialogAddJustification dialog = new DialogAddJustification();
        dialog.show(getSupportFragmentManager(), "DialogAddJustification");
    }

    private void hideAllDayButtons() {
        int[] buttonIds3Days = {R.id.boton1Dias3, R.id.boton2Dias3, R.id.boton3Dias3};
        int[] buttonIds7Days = {R.id.boton1Dias7, R.id.boton2Dias7, R.id.boton3Dias7, R.id.boton4Dias7, R.id.boton5Dias7, R.id.boton6Dias7, R.id.boton7Dias7};

        setButtonsVisibility(buttonIds3Days, View.INVISIBLE);
        setButtonsVisibility(buttonIds7Days, View.INVISIBLE);
    }

    private void setupButtonsFor3DayDiet() {
        int[] buttonIds = {R.id.boton1Dias3, R.id.boton2Dias3, R.id.boton3Dias3};
        setButtonsVisibility(buttonIds, View.VISIBLE);

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int dia = i + 1;
            button.setOnClickListener(v -> abrirComidasActivity(dia));
        }
    }

    private void setupButtonsFor7DayDiet() {
        int[] buttonIds = {R.id.boton1Dias7, R.id.boton2Dias7, R.id.boton3Dias7,
                R.id.boton4Dias7, R.id.boton5Dias7, R.id.boton6Dias7,
                R.id.boton7Dias7};
        setButtonsVisibility(buttonIds, View.VISIBLE);

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int dia = i + 1;
            button.setOnClickListener(v -> abrirComidasActivity(dia));
        }
    }

    private void setButtonsVisibility(int[] buttonIds, int visibility) {
        for (int id : buttonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setVisibility(visibility);
            }
        }
    }

    private void abrirComidasActivity(int diaSeleccionado) {
        saveData.setCurrentDay(diaSeleccionado);

        Blocker.createBlocker(this.findViewById(android.R.id.content), this);

        loadDayNutrition(saveData.getCurrentDiet().getId(), String.valueOf(diaSeleccionado), () -> {
            Blocker.removeBlocker(this.findViewById(android.R.id.content));
            Intent intent = new Intent(this, ComidasActivity.class);
            startActivity(intent);
        });
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


    public void onClickBackNavigation(int i){
        Intent intent = new Intent(this, DietasActivity.class );
        startActivity(intent);
    }
}
