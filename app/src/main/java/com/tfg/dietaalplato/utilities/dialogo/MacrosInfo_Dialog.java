package com.tfg.dietaalplato.utilities.dialogo;

import static java.lang.Double.parseDouble;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;


import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.utilities.DailyNutrition;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;

public class MacrosInfo_Dialog extends DialogFragment {

    private EditText proteinasInput, carbohidratosInput, grasasInput, caloriasInput;
    private Button cerrarButton;
    private SaveData saveData;
    private View mainView;
    private EditText minKal;
    private EditText maxKal;
    private ArrayList<FoodDiet> alimentosDelDia;
    private static final String ARG_ALIMENTOS = "alimentos";

    public static MacrosInfo_Dialog newInstance() {
        return new MacrosInfo_Dialog();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_macros, null);
        saveData = SaveData.getInstance();

        proteinasInput = mainView.findViewById(R.id.proteinas_input);
        carbohidratosInput = mainView.findViewById(R.id.carbohidratos_input);
        grasasInput = mainView.findViewById(R.id.grasas_input);
        caloriasInput = mainView.findViewById(R.id.calorias_input);
        cerrarButton = mainView.findViewById(R.id.cerrar_button);
        minKal = mainView.findViewById(R.id.minKal);
        maxKal = mainView.findViewById(R.id.maxKal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        ArrayList<FoodDiet> alimentosDelDia = saveData.getCurrentDayFoodDiets();

        double totalProteinas = 0, totalCarbohidratos = 0, totalGrasas = 0, totalCalorias = 0;

        for (FoodDiet alimentoDieta : alimentosDelDia) {
            try {
                // 1. Obtener información nutricional
                Food alimentoInfo = saveData.getFoodById(alimentoDieta.getIdAlimento());
                if (alimentoInfo == null) continue;

                // 2. Convertir valores
                double gramos = parseDouble(alimentoDieta.getG());
                double factor = gramos / 100.0;

                // 3. Calcular macros
                totalProteinas += parseDouble(alimentoInfo.getProteina()) * factor;
                totalCarbohidratos += parseDouble(alimentoInfo.getHc()) * factor;
                totalGrasas += parseDouble(alimentoInfo.getGrasa()) * factor;
                totalCalorias += parseDouble(alimentoInfo.getEnergia()) * factor;

            } catch (NumberFormatException e) {
                Log.e("MacrosError", "Error procesando alimento: " + alimentoDieta.getName(), e);
            }
        }

        // Mostrar resultados
        proteinasInput.setText(String.format("Proteínas: %.1fg", totalProteinas));
        carbohidratosInput.setText(String.format("Carbohidratos: %.1fg", totalCarbohidratos));
        grasasInput.setText(String.format("Grasas: %.1fg", totalGrasas));
        caloriasInput.setText(String.format("Calorías: %.1fkcal", totalCalorias));

        Client client = saveData.getCurrentClient();
        minKal.setText(client.getMinKal());
        maxKal.setText(client.getMaxKal());

        cerrarButton.setOnClickListener(
                v ->{
                    dismiss();
                }
        );

        return builder.create();
    }
}
