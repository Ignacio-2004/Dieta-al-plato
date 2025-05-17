package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import com.tfg.dietaalplato.R;

public class AlimentosSv_Dialogo extends DialogFragment {

    private EditText inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputAGS100,
            inputAGMI100, inputAGPI100, inputCol100, inputHC100, inputFibra100,
            inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
            inputCa100, inputK100;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_nuevo_alimento, null);

        // Vincular campos del formulario
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

        // Configurar botones del layout XML
        Button btnGuardar = view.findViewById(R.id.btnGuardar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            EditText[] campos = new EditText[]{
                    inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputAGS100,
                    inputAGMI100, inputAGPI100, inputCol100, inputHC100, inputFibra100,
                    inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
                    inputCa100, inputK100
            };

            boolean todoCorrecto = true;

            for (EditText campo : campos) {
                String texto = campo.getText().toString().trim();
                if (texto.isEmpty()) {
                    campo.setError("Este campo es obligatorio (usa 0 si no aplica)");
                    todoCorrecto = false;
                } else {
                    campo.setError(null);
                }
            }

            if (!todoCorrecto) {
                Toast.makeText(getContext(), "Completa todos los campos (usa 0 si no aplica)", Toast.LENGTH_LONG).show();
                return;
            }

            // variables de los datos
            String alimento = inputAlimento.getText().toString().trim();
            double pc = Double.parseDouble(inputPC.getText().toString().trim());
            double energia = Double.parseDouble(inputE100.getText().toString().trim());
            double proteina = Double.parseDouble(inputProt100.getText().toString().trim());
            double grasa = Double.parseDouble(inputGrasa100.getText().toString().trim());
            double ags = Double.parseDouble(inputAGS100.getText().toString().trim());
            double agmi = Double.parseDouble(inputAGMI100.getText().toString().trim());
            double agpi = Double.parseDouble(inputAGPI100.getText().toString().trim());
            double colesterol = Double.parseDouble(inputCol100.getText().toString().trim());
            double hc = Double.parseDouble(inputHC100.getText().toString().trim());
            double fibra = Double.parseDouble(inputFibra100.getText().toString().trim());
            double vitC = Double.parseDouble(inputVitC100.getText().toString().trim());
            double vitB6 = Double.parseDouble(inputVitB6100.getText().toString().trim());
            double vitE = Double.parseDouble(inputVitE100.getText().toString().trim());
            double hierro = Double.parseDouble(inputFe100.getText().toString().trim());
            double sodio = Double.parseDouble(inputNa100.getText().toString().trim());
            double calcio = Double.parseDouble(inputCa100.getText().toString().trim());
            double potasio = Double.parseDouble(inputK100.getText().toString().trim());





            Toast.makeText(getContext(), "Alimento guardado correctamente", Toast.LENGTH_SHORT).show();
            dismiss(); // Cierra el diálogo solo si es válido
        });

        // Crear diálogo sin título ni botones extra
        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }
}
