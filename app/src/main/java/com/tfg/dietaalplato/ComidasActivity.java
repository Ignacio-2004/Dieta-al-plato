package com.tfg.dietaalplato;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.DailyNutrition;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.Macros;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;
import com.tfg.dietaalplato.utilities.dialogo.MacrosInfo_Dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText, desayuno_textview, almuerzo_textview, comida_textview, merienda_textview, cena_textview, recena_textview;
    ImageButton botonDesayuno, botonAlmuerzo, botonComida, botonMerienda, botonCena, botonRecena;
    private ImageView btnJust;
    private SaveData saveData;

    private TextView tvNutritionSummary;
    private FireBaseConnector firebaseConnector;
    private Macros nutritionCalculator;
    private Map<String, Food> foodCache = new HashMap<>();
    private ListenerRegistration foodDietListener; // Para el paso 5


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);
        saveData = SaveData.getInstance();

        firebaseConnector = FireBaseConnector.getInstance();
        nutritionCalculator = new Macros();

        diaText = findViewById(R.id.titulo_textview);
        botonDesayuno = findViewById(R.id.desayuno_boton);
        botonAlmuerzo = findViewById(R.id.almuerzo_boton);
        botonComida = findViewById(R.id.comida_boton);
        botonMerienda = findViewById(R.id.merienda_boton);
        botonCena = findViewById(R.id.cena_boton);
        botonRecena = findViewById(R.id.recena_boton);
        btnJust = findViewById(R.id.imageView2);

        if (!saveData.getCurrentDiet().getTip().equals("1")) {
            btnJust.setVisibility(View.INVISIBLE);
            btnJust.setEnabled(false);
        }

        if (saveData.getCurrentDiet().getTip().equals("1")) {
            diaText.setText("¿Qué comida quieres visualizar?");
        } else {
            diaText.setText(String.format("¿Qué comida quieres visualizar del día %d?", saveData.getCurrentDay()));
        }

        View.OnClickListener comidaClickListener = v -> {
            int id = v.getId();

            if (id == R.id.desayuno_boton) {
                saveData.setMomentOfDay("1");
            } else if (id == R.id.almuerzo_boton) {
                saveData.setMomentOfDay("2");
            } else if (id == R.id.comida_boton) {
                saveData.setMomentOfDay("3");
            } else if (id == R.id.merienda_boton) {
                saveData.setMomentOfDay("4");
            } else if (id == R.id.cena_boton) {
                saveData.setMomentOfDay("5");
            } else if (id == R.id.recena_boton) {
                saveData.setMomentOfDay("6");
            }

            abrirAlimentosActivity();
        };

        botonDesayuno.setOnClickListener(comidaClickListener);
        botonAlmuerzo.setOnClickListener(comidaClickListener);
        botonComida.setOnClickListener(comidaClickListener);
        botonMerienda.setOnClickListener(comidaClickListener);
        botonCena.setOnClickListener(comidaClickListener);
        botonRecena.setOnClickListener(comidaClickListener);
    }

    private void loadDayNutrition(String dietId, String day) {
        try {
            FireBaseReader.readFoodsForDay(dietId, Integer.parseInt(day))
                    .addOnSuccessListener(foodResult -> {
                        if (foodResult.isSuccess() && !foodResult.result.isEmpty()) {
                            calculateDailyNutrition(foodResult.result, day);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al cargar nutrición: " + e.getMessage());
                    });
        } catch (FBCException e) {
            Log.e(TAG, "Error al cargar nutrición: " + e.getMessage());
        }
    }

    private void calculateDailyNutrition(List<FoodDiet> foodDiets, String day) {
        DailyNutrition dailyNutrition = new DailyNutrition();

        for (FoodDiet foodDiet : foodDiets) {
            try {
                Food food = saveData.getFoods().get(foodDiet.getIdAlimento());
                if (food != null) {
                    dailyNutrition.addFood(food); // Este método ya suma todo
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener alimento: " + e.getMessage());
            }
        }

        // Guardamos todo en saveData
        saveData.setNutritionForDay(day, dailyNutrition);
    }


    public void just(View view) {
        DialogAddJustification dialog = new DialogAddJustification();
        dialog.show(getSupportFragmentManager(), "DialogAddJustification");
    }

    private void abrirAlimentosActivity() {
        Intent intent = new Intent(this, DietaIDia.class );
        startActivity(intent);
    }

    public void onClickBackNavigation(View view) {
        if (saveData.getCurrentDiet().getTip().equals("1")) {
            Intent intent = new Intent(this, DietasActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, DiasActivity.class);
            startActivity(intent);
        }
    }

    public void mostrarMacros(View view) {
        MacrosInfo_Dialog dialogo = MacrosInfo_Dialog.getInstance();
        dialogo.show(getSupportFragmentManager(), "MacrosInfo_Dialog");
    }


}