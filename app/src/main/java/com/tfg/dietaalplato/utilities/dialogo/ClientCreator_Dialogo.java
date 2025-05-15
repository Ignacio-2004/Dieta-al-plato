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
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.ArrayList;

public class ClientCreator_Dialogo extends DialogFragment {
    private TextView textError;
    private EditText nombre;
    private EditText apellido;
    private SaveData saveData;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // mostramos el layout (Inflamos)
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_nuevo_cliente, null);

        // se inicia los campos
        nombre = view.findViewById(R.id.editTextNombre);
        apellido = view.findViewById(R.id.editTextApellido);
        textError = view.findViewById(R.id.textError);
        saveData = SaveData.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setCancelable(true);

        Button btnCancelar = view.findViewById(R.id.buttonCancelar);
        btnCancelar.setOnClickListener(v -> onCancelClick(view));

        Button btnConfirm = view.findViewById(R.id.buttonConfirmar);
        btnConfirm.setOnClickListener(v -> onConfirmationClick(view));

        return builder.create();
    }

    private void onConfirmationClick(View view){
        // guardamos los valores introducidos
        String nombre_introducido = nombre.getText().toString().trim();
        String apellido_introducido = apellido.getText().toString().trim();

        if(!nombre_introducido.isEmpty())
        {
            if(!apellido_introducido.isEmpty()) {

                if (nombre.getText().toString().matches("[a-zA-Z]+")) {

                    if (apellido.getText().toString().matches("[a-zA-Z]+")) {

                        ArrayList<View> views = new ArrayList<>();
                        views.add(nombre);
                        views.add(apellido);

                        ValidationResult userData  = Client.toMapData(views, getAlergias(view), getPatologias(view),saveData.getUser().getId());

                        // guardamos los datos en la base de datos
                        FireBaseWriter.saveData(Client.class,userData).addOnSuccessListener(
                                validationResult -> {
                                    nombre.setText("");
                                    apellido.setText("");
                                    textError.setText("  Cliente creado correctamente.  ");
                                    textError.setVisibility(View.VISIBLE); // el textView se puede ver
                                    mostrarTextError();// se oculta el mensaje
                                }
                        ).addOnFailureListener(
                                e -> {
                                    textError.setText("  Error: " + e.getMessage() + "  ");
                                    textError.setVisibility(View.VISIBLE); // el textView se puede ver
                                    mostrarTextError();// se oculta el mensaje
                                }
                        );

                    }else{
                        textError.setText("  Error: apellido de usuario debe ser alfabético.  ");
                        textError.setVisibility(View.VISIBLE); // el textView se puede ver
                        mostrarTextError();// se oculta el mensaje
                    }

                }else{
                    textError.setText("  Error: nombre de usuario debe ser alfabético.  ");
                    textError.setVisibility(View.VISIBLE); // el textView se puede ver
                    mostrarTextError();// se oculta el mensaje
                }

            }else{
                textError.setText("  Error: apellido de usuario vacío.  ");
                textError.setVisibility(View.VISIBLE); // el textView se puede ver
                mostrarTextError();// se oculta el mensaje
            }
        }else{
            textError.setText("  Error: nombre de usuario vacío.  ");
            textError.setVisibility(View.VISIBLE); // el textView se puede ver
            mostrarTextError();// se oculta el mensaje
        }
    }

    private void onCancelClick(View view){
        dismiss();
    }

    private void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 2000);
    }

    private ArrayList<String> getAlergias(View view){
        EditText editText = view.findViewById(R.id.editTextAlergias);
        String alergias = editText.getText().toString();
        ArrayList<String> alergiasList = new ArrayList<>();

        if (!alergias.isEmpty()) {
            String[] alergiasArray = alergias.split(",");
            for (String alergia : alergiasArray) {
                alergiasList.add(alergia.trim());
            }
        }

        return alergiasList;
    }

    private ArrayList<String> getPatologias(View view){
        EditText editText = view.findViewById(R.id.editTextPatologias);
        String patologias = editText.getText().toString();
        ArrayList<String> patologiasList = new ArrayList<>();

        if (!patologias.isEmpty()) {
            String[] patologiasArray = patologias.split(",");
            for (String patologia : patologiasArray) {
                patologiasList.add(patologia.trim());
            }
        }
        return patologiasList;
    }
}
