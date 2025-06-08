package com.tfg.dietaalplato.utilities.dialogo;




import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.fragment.app.DialogFragment;


import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseRemover;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;


import java.util.ArrayList;
import java.util.List;


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


        Client client = saveData.getCurrentClient();
        proteinasInput.setText(client.getMinKal());
        carbohidratosInput.setText(client.getMaxKal());
        grasasInput.setText(client.getMinKal());
        caloriasInput.setText(client.getMaxKal());
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
