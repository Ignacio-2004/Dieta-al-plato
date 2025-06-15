package com.tfg.dietaalplato;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.ListenerRegistration;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.utilities.DailyNutrition;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.utilities.Macros;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;
import com.tfg.dietaalplato.utilities.dialogo.MacrosInfo_Dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComidasActivity extends AppCompatActivity {

    TextView diaText, desayuno_textview, almuerzo_textview, comida_textview, merienda_textview, cena_textview, recena_textview;
    ImageButton botonDesayuno, botonAlmuerzo, botonComida, botonMerienda, botonCena, botonRecena;
    private ImageView btnJust;
    private SaveData saveData;
    private String currentDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comidas);
        saveData = SaveData.getInstance();

        currentDay = saveData.getCurrentDiet().getTip().equals("1") ?
                "1" : String.valueOf(saveData.getCurrentDay());

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
        String idDieta = saveData.getCurrentDiet().getId();
        String dia = String.valueOf(saveData.getCurrentDay());

        try {
            FireBaseReader.readFoodDietByDay2(idDieta, dia)
                    .addOnSuccessListener(result -> {
                        if (result.isSuccess()) {
                            // Los datos ya están en SaveData, podemos mostrar el diálogo directamente
                            MacrosInfo_Dialog dialog = MacrosInfo_Dialog.newInstance();
                            dialog.show(getSupportFragmentManager(), "MacrosInfoDialog");
                        } else {
                            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (FBCException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}