package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
        View view = inflater.inflate(R.layout.dialog_nuevo_alimento, null); // tu layout con ScrollView

        //  EditText de los datos que debe incluir
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

        // Creamos el AlertDialog con botones
        return new AlertDialog.Builder(requireContext())
                .setTitle("Añadir Alimento")
                .setView(view)
                .setPositiveButton("Guardar", null) // Se sobrescribe para evitar auto-cierre
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                // Validación de campos
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

                // Aquí puedes procesar los datos si todo está bien
                String alimento = inputAlimento.getText().toString().trim();
                double pc = Double.parseDouble(inputPC.getText().toString().trim());
                double energia = Double.parseDouble(inputE100.getText().toString().trim());
                double proteina = Double.parseDouble(inputProt100.getText().toString().trim());
                // Y así con el resto...

                // TODO: Guardar o enviar datos...

                Toast.makeText(getContext(), "Alimento guardado correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // Cierra solo si todo es válido
            });
        }
    }
}
