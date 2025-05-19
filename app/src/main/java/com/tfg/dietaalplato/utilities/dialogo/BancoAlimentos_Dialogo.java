
package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.FirebaseApp;
import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;

public class BancoAlimentos_Dialogo extends DialogFragment {

    private TextView textError;
    private EditText inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputAGS100,
            inputAGMI100, inputAGPI100, inputCol100, inputHC100, inputFibra100,
            inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
            inputCa100, inputK100;
    private View mainView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mainView = inflater.inflate(R.layout.dialog_nuevo_alimento, null);

        // variables de todos los camoos del formulario
        inputAlimento = mainView.findViewById(R.id.inputAlimento);
        inputPC = mainView.findViewById(R.id.inputPC);
        inputE100 = mainView.findViewById(R.id.inputE100);
        inputProt100 = mainView.findViewById(R.id.inputProt100);
        inputGrasa100 = mainView.findViewById(R.id.inputGrasa100);
        inputAGS100 = mainView.findViewById(R.id.inputAGS100);
        inputAGMI100 = mainView.findViewById(R.id.inputAGMI100);
        inputAGPI100 = mainView.findViewById(R.id.inputAGPI100);
        inputCol100 = mainView.findViewById(R.id.inputCol100);
        inputHC100 = mainView.findViewById(R.id.inputHC100);
        inputFibra100 = mainView.findViewById(R.id.inputFibra100);
        inputVitC100 = mainView.findViewById(R.id.inputVitC100);
        inputVitB6100 = mainView.findViewById(R.id.inputVitB6100);
        inputVitE100 = mainView.findViewById(R.id.inputVitE100);
        inputFe100 = mainView.findViewById(R.id.inputFe100);
        inputNa100 = mainView.findViewById(R.id.inputNa100);
        inputCa100 = mainView.findViewById(R.id.inputCa100);
        inputK100 = mainView.findViewById(R.id.inputK100);
        textError = mainView.findViewById(R.id.txtMensajeErrorAlimento);

        // Botones
        Button btnGuardar = mainView.findViewById(R.id.btnGuardar);
        Button btnCancelar = mainView.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> guardarAlimento());

        return new AlertDialog.Builder(requireContext())
                .setView(mainView)
                .create();
    }

    // METODO PARA GUARDAR EL ALIMENTO
    private void guardarAlimento() {

        Blocker.createBlocker((ViewGroup) mainView.getRootView(), requireActivity());

        /* -- IPS 19052025 Los campos se comprueban en el toMap del cliente
        // indicamos los campos obligatorios
        EditText[] camposObligatorios = new EditText[]{
                inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputHC100
        };
        */

        /*// eston son los campos opcionales
        EditText[] camposOpcionales = new EditText[]{
                inputAGS100, inputAGMI100, inputAGPI100, inputCol100, inputFibra100,
                inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
                inputCa100, inputK100
        };*/

        //boolean todoCorrecto = true;

        /*// comprobamos que todos los campos obligatorios esten llenos
        for (EditText campo : camposObligatorios) {
            String texto = campo.getText().toString().trim();
            if (texto.isEmpty() || texto.equals("0")) {
                campo.setError("Este campo es obligatorio");
                todoCorrecto = false;
            } else {
                campo.setError(null);
            }
        }

        if (!todoCorrecto) {
            textError.setText("❌ Fal favor, tan datos obligatorios. Porrevisa los campos resaltados.");
            textError.setVisibility(View.VISIBLE);
            mostrarTextError();
            return;
        }*/

        // si el opcional es vacio lo dejamos en 0
        /*for (EditText campo : camposOpcionales) {
            if (campo.getText().toString().trim().isEmpty()) {
                campo.setText("0");
            }
        }*/

        // los campos del alimento
        ArrayList<View> campos = new ArrayList<>();
        campos.add(inputAlimento);
        campos.add(inputPC);
        campos.add(inputE100);
        campos.add(inputProt100);
        campos.add(inputGrasa100);
        campos.add(inputAGS100);
        campos.add(inputAGMI100);
        campos.add(inputAGPI100);
        campos.add(inputCol100);
        campos.add(inputHC100);
        campos.add(inputFibra100);
        campos.add(inputVitC100);
        campos.add(inputVitB6100);
        campos.add(inputVitE100);
        campos.add(inputFe100);
        campos.add(inputNa100);
        campos.add(inputCa100);
        campos.add(inputK100);

        // id del usuario que guarda el alimento
        String idUsuario = SaveData.getInstance().getUser().getId();

        ValidationResult alimentoData = Food.toMapData(campos, idUsuario);

        if (!alimentoData.exit) {
            textError.setText("❌ " + alimentoData.message);
            textError.setVisibility(View.VISIBLE);
            mostrarTextError();
            Blocker.removeBlocker((ViewGroup) mainView.getRootView());
            return;
        }

        /* -- IPS 19052025 No hace falta comprobar si firebase esta inicializado porque se incializa solo
        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            Log.e("FireBase", "Firebase no se ha inicializado");
            return;
        }*/

        FireBaseWriter.saveData(Food.class, alimentoData)
                .addOnSuccessListener(res -> {
                    textError.setText("✅ Alimento guardado correctamente");
                    textError.setVisibility(View.VISIBLE);
                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof ComplexFBCE) {
                        textError.setText(((ComplexFBCE) e).reason.message);
                    }else{
                        textError.setText("Error al guardar el alimento");
                    }
                    textError.setVisibility(View.VISIBLE);
                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                    mostrarTextError();
                });
    }



    public void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 3000);
    }
}
