
package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;

public class BancoAlimentos_Dialogo extends DialogFragment {

    private FireBaseConnector DateBase;
    private TextView textError;
    private EditText inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputAGS100,
            inputAGMI100, inputAGPI100, inputCol100, inputHC100, inputFibra100,
            inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
            inputCa100, inputK100;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nuevo_alimento, null);

        // variables de todos los camoos del formulario
        inputAlimento = view.findViewById(R.id.inputAlimento);
        inputPC = view.findViewById(R.id.inputPC);
        inputE100 = view.findViewById(R.id.inputE100);
        inputProt100 = view.findViewById(R.id.inputProt100);
        inputGrasa100 = view.findViewById(R.id.inputGrasa100);
        inputAGS100 = view.findViewById(R.id.inputAGS100);
        inputAGMI100 = view.findViewById(R.id.inputAGMI100);
        inputAGPI100 = view.findViewById(R.id.inputAGPI100);
        inputCol100 = view.findViewById(R.id.inputCol100);
        inputHC100 = view.findViewById(R.id.inputHC100);
        inputFibra100 = view.findViewById(R.id.inputFibra100);
        inputVitC100 = view.findViewById(R.id.inputVitC100);
        inputVitB6100 = view.findViewById(R.id.inputVitB6100);
        inputVitE100 = view.findViewById(R.id.inputVitE100);
        inputFe100 = view.findViewById(R.id.inputFe100);
        inputNa100 = view.findViewById(R.id.inputNa100);
        inputCa100 = view.findViewById(R.id.inputCa100);
        inputK100 = view.findViewById(R.id.inputK100);
        textError = view.findViewById(R.id.txtMensajeErrorAlimento);

        // Botones
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> guardarAlimento());

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }

    // METODO PARA GUARDAR EL ALIMENTO
    private void guardarAlimento() {
        // indicamos los campos obligatorios
        EditText[] camposObligatorios = new EditText[]{
                inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputHC100
        };

        // eston son los campos opcionales
        EditText[] camposOpcionales = new EditText[]{
                inputAGS100, inputAGMI100, inputAGPI100, inputCol100, inputFibra100,
                inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
                inputCa100, inputK100
        };

        boolean todoCorrecto = true;

        // comprobamos que todos los campos obligatorios esten llenos
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
            textError.setText("❌ Faltan datos obligatorios. Por favor, revisa los campos resaltados.");
            textError.setVisibility(View.VISIBLE);
            mostrarTextError();
            return;
        }

        // si el opcional es vacio lo dejamos en 0
        for (EditText campo : camposOpcionales) {
            if (campo.getText().toString().trim().isEmpty()) {
                campo.setText("0");
            }
        }

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
            return;
        }

        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            Log.e("FireBase", "Firebase no se ha inicializado");
            return;
        }

        DateBase = FireBaseConnector.getInstance();

        FireBaseWriter.saveData(Food.class, alimentoData)
                .addOnSuccessListener(res -> {
                    Toast.makeText(getContext(), "✅ Alimento guardado correctamente", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    textError.setText("Error al guardar el alimento: " + e.getMessage());
                    textError.setVisibility(View.VISIBLE);
                    mostrarTextError();
                });
    }



    public void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 3000);
    }
}
