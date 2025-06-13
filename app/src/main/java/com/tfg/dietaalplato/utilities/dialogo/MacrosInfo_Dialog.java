package com.tfg.dietaalplato.utilities.dialogo;




import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;


import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.utilities.DailyNutrition;
import com.tfg.dietaalplato.utilities.SaveData;


public class MacrosInfo_Dialog extends DialogFragment {


    private EditText proteinasInput, carbohidratosInput, grasasInput, caloriasInput;
    private TextView textError;
    private Button cerrarButton;
    private SaveData saveData;
    private View mainView;
    private EditText minKal;
    private EditText maxKal;


    public static MacrosInfo_Dialog getInstance() {
        return new MacrosInfo_Dialog();
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_macros, null);

        proteinasInput = mainView.findViewById(R.id.proteinas_input);
        carbohidratosInput = mainView.findViewById(R.id.carbohidratos_input);
        grasasInput = mainView.findViewById(R.id.grasas_input);
        caloriasInput = mainView.findViewById(R.id.calorias_input);
        cerrarButton = mainView.findViewById(R.id.cerrar_button);
        minKal = mainView.findViewById(R.id.minKal);
        maxKal = mainView.findViewById(R.id.maxKal);


        saveData = SaveData.getInstance();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);


        String currentDay = saveData.getCurrentDiet().getTip().equals("1") ?
                "1" : String.valueOf(saveData.getCurrentDay());

        DailyNutrition nutrition = saveData.getNutritionForDay(currentDay);
        Client client = saveData.getCurrentClient();

        if (nutrition != null) {
            proteinasInput.setText(String.format("Proteínas: %.1fg", nutrition.getTotalProteinas()));
            carbohidratosInput.setText(String.format("Carbohidratos: %.1fg", nutrition.getTotalCarbohidratos()));
            grasasInput.setText(String.format("Grasas: %.1fg", nutrition.getTotalGrasas()));
            caloriasInput.setText(String.format("Calorías: %.1fkcal", nutrition.getTotalCalorias()));
        } else {
            proteinasInput.setText("No hay datos de proteínas");
            carbohidratosInput.setText("No hay datos de carbohidratos");
            grasasInput.setText("No hay datos de grasas");
            caloriasInput.setText("No hay datos de calorías");
        }

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
