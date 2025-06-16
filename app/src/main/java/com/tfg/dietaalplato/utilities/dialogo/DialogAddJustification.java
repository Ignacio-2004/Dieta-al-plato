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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseRemover;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;
import java.util.Objects;

public class DialogAddJustification extends DialogFragment {


    private Button btnGuardar;
    private Button btnCancelar;
    private EditText editTextMultiline;
    private TextView txtMensajeErrorAlimento;
    private SaveData saveData;

    private View mainView;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mainView = inflater.inflate(R.layout.activity_dialog_add_justification, null);
        editTextMultiline = mainView.findViewById(R.id.editTextMultiline);
        txtMensajeErrorAlimento = mainView.findViewById(R.id.txtMensajeErrorAlimento);

        btnGuardar = mainView.findViewById(R.id.btnGuardar);
        btnCancelar = mainView.findViewById(R.id.btnCancelar);
        saveData = SaveData.getInstance();

        if (saveData.getCurrentDiet().getJust() != null && !saveData.getCurrentDiet().getJust().isEmpty() && !saveData.getCurrentDiet().getJust().equalsIgnoreCase("null")){
            Diet currentDiet = saveData.getCurrentDiet();

            editTextMultiline.setText(currentDiet.getJust());
        }

        btnCancelar.setOnClickListener(v -> dismiss());
        btnGuardar.setOnClickListener(v ->{
            ViewGroup parent = (ViewGroup) mainView.getRootView();
            Blocker.createBlocker(parent,requireActivity());
            String just = editTextMultiline.getText().toString();
            String idCli = saveData.getCurrentClient().getId();
            String tip = saveData.getCurrentDiet().getTip();

            ArrayList <String> data = new ArrayList<>();
            data.add(tip);
            data.add(just);

            ValidationResult result = Diet.toMapData(data, idCli);

            try {
                FireBaseRemover.remove(saveData.getCurrentDiet().getId()).addOnSuccessListener(
                        aVoid -> {
                            FireBaseWriter.saveData(Diet.class,result).addOnSuccessListener(
                                    aVoid2 -> {
                                        Log.d("TAG", "✅ Justificacion guardada correctamente");
                                        txtMensajeErrorAlimento.setText("✅ Justificacion guardada correctamente");
                                        txtMensajeErrorAlimento.setVisibility(View.VISIBLE);
                                        saveData.setCurrentDiet((Diet) aVoid2.result);
                                        Blocker.removeBlocker(parent);
                                        mostrarTextError();
                                        getActivity().recreate();
                                    }
                            ).addOnFailureListener(
                                    e -> {
                                        Log.d("TAG", "❌ Error al guardar la justificacion");
                                        txtMensajeErrorAlimento.setText("❌ Error al guardar la justificacion");
                                        txtMensajeErrorAlimento.setVisibility(View.VISIBLE);
                                        Blocker.removeBlocker(parent);
                                        mostrarTextError();
                                    }
                            );
                        }
                ).addOnFailureListener(
                        e -> {
                            Log.d("TAG", "❌ Error al eliminar la dieta");
                            txtMensajeErrorAlimento.setText("❌ Error al eliminar la dieta");
                            txtMensajeErrorAlimento.setVisibility(View.VISIBLE);
                            Blocker.removeBlocker(parent);
                            mostrarTextError();
                        }
                );


            } catch (Exception e) {
                Log.d("TAG", "No se ha podido gaurdar la justificacion");
                txtMensajeErrorAlimento.setText( "No se ha podido gaurdar la justificacion");
                txtMensajeErrorAlimento.setVisibility(View.VISIBLE);
                Blocker.removeBlocker(parent);
                mostrarTextError();
            }
            Log.d("TAG", "Guardando justificacion");
        });

        return new AlertDialog.Builder(requireContext())
                .setView(mainView)
                .create();
    }
    public void mostrarTextError() {
        txtMensajeErrorAlimento.postDelayed(() -> txtMensajeErrorAlimento.setVisibility(View.GONE), 3000);
    }
}