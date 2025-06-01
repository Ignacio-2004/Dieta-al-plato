package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;
import java.util.Locale;

public class GuardarBancoAlimentos_Dialogo extends DialogFragment {

    private TextView textError;
    private EditText inputAlimento, inputPC, inputE100, inputProt100, inputGrasa100, inputAGS100,
            inputAGMI100, inputAGPI100, inputCol100, inputHC100, inputFibra100,
            inputVitC100, inputVitB6100, inputVitE100, inputFe100, inputNa100,
            inputCa100, inputK100;
    private View mainView;

    private final float STEP = 0.1f;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mainView = inflater.inflate(R.layout.dialog_nuevo_alimento, null);

        // Inicializar EditTexts
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

        // Configurar botones con los IDs correctos del XML
        setupIncrementDecrement(R.id.btnPlusPC, R.id.btnMinusPC, inputPC);
        setupIncrementDecrement(R.id.btnIncrementE100, R.id.btnDecrementE100, inputE100);
        setupIncrementDecrement(R.id.btnIncrementGrasa100, R.id.btnDecrementGrasa100, inputGrasa100);
        setupIncrementDecrement(R.id.btnIncrementProt100, R.id.btnDecrementProt100, inputProt100);
        setupIncrementDecrement(R.id.btnIncrementAgs100, R.id.btnDecrementAgs100, inputAGS100);
        setupIncrementDecrement(R.id.btnIncrementAgmi100, R.id.btnDecrementAgmi100, inputAGMI100);
        setupIncrementDecrement(R.id.btnIncrementAgpi100, R.id.btnDecrementAgpi100, inputAGPI100);
        setupIncrementDecrement(R.id.btnIncrementColesterol100, R.id.btnDecrementColesterol100, inputCol100);
        setupIncrementDecrement(R.id.btnPlusHC, R.id.btnMinusHC, inputHC100);
        setupIncrementDecrement(R.id.btnPlusFibra, R.id.btnMinusFibra, inputFibra100);
        setupIncrementDecrement(R.id.btnPlusVitC, R.id.btnMinusVitC, inputVitC100);
        setupIncrementDecrement(R.id.btnPlusVitB6, R.id.btnMinusVitB6, inputVitB6100);
        setupIncrementDecrement(R.id.btnPlusVitE, R.id.btnMinusVitE, inputVitE100);
        setupIncrementDecrement(R.id.btnPlusFe, R.id.btnMinusFe, inputFe100);
        setupIncrementDecrement(R.id.btnPlusNa, R.id.btnMinusNa, inputNa100);
        setupIncrementDecrement(R.id.btnPlusCa, R.id.btnMinusCa, inputCa100);
        setupIncrementDecrement(R.id.btnPlusK, R.id.btnMinusK, inputK100);

        // Botones guardar y cancelar
        Button btnGuardar = mainView.findViewById(R.id.btnGuardar);
        Button btnCancelar = mainView.findViewById(R.id.btnCancelar);

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v -> guardarAlimento());

        return new AlertDialog.Builder(requireContext())
                .setView(mainView)
                .create();
    }


    private void guardarAlimento() {
        Blocker.createBlocker((ViewGroup) mainView.getRootView(), requireActivity());

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

        String idUsuario = SaveData.getInstance().getUser().getId();
        ValidationResult alimentoData = Food.toMapData(campos, idUsuario);

        if (!alimentoData.exit) {
            textError.setText("❌ " + alimentoData.message);
            textError.setVisibility(View.VISIBLE);
            mostrarTextError();
            Blocker.removeBlocker((ViewGroup) mainView.getRootView());
            return;
        }

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
                    } else {
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

    private void modificarValor(EditText input, float delta) {
        float valorActual = 0f;
        String texto = input.getText().toString();

        if (!texto.isEmpty()) { // en caso de tener valor aumentamos
            try {
                valorActual = Float.parseFloat(texto);
            } catch (NumberFormatException e) {
                input.setError("Número inválido");
                return;
            }
        } else {
            // esta vacio usamos el hint como referencia
            String hint = input.getHint() != null ? input.getHint().toString() : "0";
            try {
                valorActual = Float.parseFloat(hint);
            } catch (NumberFormatException e) {
                valorActual = 0f;
            }
        }

        // disminuye si es mayor que cero
        if (delta < 0 && valorActual <= 0f) {
            return; // No hacemos nada si se intenta reducir por debajo de 0
        }

        float nuevoValor = valorActual + delta;
        if (nuevoValor < 0f) nuevoValor = 0f;

        input.setText(String.format(Locale.getDefault(), "%.1f", nuevoValor));
    }


    private void setupIncrementDecrement(int btnIncrementId, int btnDecrementId, EditText editText) {
        Button btnIncrement = mainView.findViewById(btnIncrementId);
        Button btnDecrement = mainView.findViewById(btnDecrementId);

        btnIncrement.setOnClickListener(v -> modificarValor(editText, STEP));
        btnDecrement.setOnClickListener(v -> modificarValor(editText, -STEP));
    }
}
