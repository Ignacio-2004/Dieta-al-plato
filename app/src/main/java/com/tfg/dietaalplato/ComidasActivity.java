package com.tfg.dietaalplato;

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
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.Macros;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText;
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
        tvNutritionSummary = findViewById(R.id.tv_nutrition_summary);
        nutritionCalculator = new Macros();

        loadDailyNutrition();

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
            String tipoComida;
            int id = v.getId();

            if (id == R.id.desayuno_boton) {
                tipoComida = "desayuno";
                saveData.setMomentOfDay("1");
            } else if (id == R.id.almuerzo_boton) {
                tipoComida = "almuerzo";
                saveData.setMomentOfDay("2");
            } else if (id == R.id.comida_boton) {
                tipoComida = "comida";
                saveData.setMomentOfDay("3");
            } else if (id == R.id.merienda_boton) {
                tipoComida = "merienda";
                saveData.setMomentOfDay("4");
            } else if (id == R.id.cena_boton) {
                tipoComida = "cena";
                saveData.setMomentOfDay("5");
            } else if (id == R.id.recena_boton) {
                tipoComida = "recena";
                saveData.setMomentOfDay("6");
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

    @Override
    protected void onStart() {
        super.onStart();
        setupRealTimeUpdates(); // Paso 5: Iniciar listener en tiempo real
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Paso 5: Limpiar listener al salir de la actividad
        if (foodDietListener != null) {
            foodDietListener.remove();
        }
    }

    // --- Paso 5: Actualizaciones en tiempo real ---
    private void setupRealTimeUpdates() {
        String dietId = saveData.getCurrentDiet().getId();
        String day = saveData.getCurrentDiet().getTip().equals("1") ? "1" : String.valueOf(saveData.getCurrentDay());

        foodDietListener = firebaseConnector.getFirestore()
                .collection("comidaDietas")
                .whereEqualTo("idDieta", dietId)
                .whereEqualTo("dia", day)
                .addSnapshotListener((querySnapshots, error) -> {
                    if (error != null) {
                        handleNutritionError(error); // Paso 6: Manejo de errores
                        return;
                    }
                    if (querySnapshots != null) {
                        showLoading(true); // Paso 6: Mostrar carga
                        loadDailyNutrition(); // Recargar datos cuando hay cambios
                    }
                });
    }

    // --- Paso 6: Mejoras adicionales ---
    private void showLoading(boolean show) {
        tvNutritionSummary.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void handleNutritionError(Exception e) {
        Log.e("ComidasActivity", "Error en nutrición", e);
        tvNutritionSummary.setText("Error al cargar datos. Reintente.");
        if (e instanceof FirebaseFirestoreException) {
            Toast.makeText(this, "Error de conexión con Firebase", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDailyNutrition() {
        String dietId = saveData.getCurrentDiet().getId();
        String day = saveData.getCurrentDiet().getTip().equals("1") ? "1" : String.valueOf(saveData.getCurrentDay());

        // Obtener todos los FoodDiet del día específico
        firebaseConnector.getFirestore()
                .collection("comidaDietas")
                .whereEqualTo("idDieta", dietId)
                .whereEqualTo("dia", day)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    nutritionCalculator = new Macros(); // Reset calculator
                    List<Task<Food>> foodTasks = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FoodDiet foodDiet = doc.toObject(FoodDiet.class);
                        if (foodDiet != null) {
                            // Verificar si ya está en cache
                            if (foodCache.containsKey(foodDiet.getIdAlimento())) {
                                Food food = foodCache.get(foodDiet.getIdAlimento());
                                nutritionCalculator.sumarAlimento(food, Double.parseDouble(foodDiet.getG()));
                                updateNutritionSummary();
                            } else {
                                // Si no está en cache, crear tarea para obtenerlo
                                foodTasks.add(createFoodTask(foodDiet));
                            }
                        }
                    }

                    // Cuando todas las tareas de alimentos estén completas
                    if (!foodTasks.isEmpty()) {
                        Tasks.whenAllSuccess(foodTasks)
                                .addOnSuccessListener(foods -> {
                                    for (Object obj : foods) {
                                        if (obj instanceof Food) {
                                            Food food = (Food) obj;
                                            // Encontrar el FoodDiet correspondiente
                                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                                FoodDiet fd = doc.toObject(FoodDiet.class);
                                                if (fd != null && fd.getIdAlimento().equals(food.getId())) {
                                                    nutritionCalculator.sumarAlimento(food, Double.parseDouble(fd.getG()));
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    updateNutritionSummary();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ComidasActivity", "Error al cargar alimentos", e);
                                    tvNutritionSummary.setText("Error al cargar datos nutricionales");
                                });
                    } else {
                        updateNutritionSummary();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ComidasActivity", "Error al cargar comidas", e);
                    tvNutritionSummary.setText("Error al cargar datos nutricionales");
                });
    }

    private Task<Food> createFoodTask(FoodDiet foodDiet) {
        try {
            return firebaseConnector.readFood(foodDiet.getIdAlimento())
                    .addOnSuccessListener(food -> {
                        // Agregar al cache para futuras referencias
                        foodCache.put(food.getId(), food);
                    });
        } catch (FBCException e) {
            Log.e("ComidasActivity", "Error creando tarea de alimento", e);
            TaskCompletionSource<Food> tcs = new TaskCompletionSource<>();
            tcs.setException(e);
            return tcs.getTask();
        }
    }

    private void updateNutritionSummary() {
        tvNutritionSummary.setText(nutritionCalculator.getResumen());
    }

    public void just(View view){
        DialogAddJustification dialog = new DialogAddJustification();
        dialog.show(getSupportFragmentManager(), "DialogAddJustification");
    }

    private void abrirAlimentosActivity(String comidaSeleccionada) {
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

}