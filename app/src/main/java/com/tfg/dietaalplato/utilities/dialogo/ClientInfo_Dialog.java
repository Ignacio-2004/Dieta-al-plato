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

public class ClientInfo_Dialog extends DialogFragment {
    private EditText editTextNombre;
    private EditText editTextApellido;
    private TextView textError;
    private LinearLayout layoutAlergias;
    private LinearLayout layoutPatologias;
    private Button saveButton;
    private Button cancelButton;
    private SaveData saveData;
    private Client client;
    private ArrayList<String> alergias;
    private ArrayList<String> patologias;
    private static boolean haveFill;
    private View mainView;
    private EditText minKal;
    private EditText maxKal;

    public static ClientInfo_Dialog getInstance(boolean fill) {
        haveFill = fill;
        return new ClientInfo_Dialog();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_cliente, null);

        editTextNombre = mainView.findViewById(R.id.editTextNombre);
        editTextApellido = mainView.findViewById(R.id.editTextApellido);
        textError = mainView.findViewById(R.id.textError);
        layoutAlergias = mainView.findViewById(R.id.layoutAlergias);
        layoutPatologias = mainView.findViewById(R.id.layoutPatologias);
        saveButton = mainView.findViewById(R.id.buttonGuardar);
        cancelButton = mainView.findViewById(R.id.buttonCancelar);
        minKal = mainView.findViewById(R.id.minKal);
        maxKal = mainView.findViewById(R.id.maxKal);

        saveData = SaveData.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        if (haveFill){
            client = saveData.getIdCurrentClient();
            Log.d("Client",client.toString());
            editTextNombre.setText(client.getName());
            editTextApellido.setText(client.getApe());
            alergias = client.getAlergias();
            patologias = client.getPatologias();
            minKal.setText(client.getMinKcal());
            maxKal.setText(client.getMaxKcal());
        }else{
            alergias = new ArrayList<>();
            patologias = new ArrayList<>();
        }

        // Rellenar alergias
        for (String alergia : alergias) {
            if (!alergia.isEmpty()) {
                addElement(alergia, layoutAlergias, alergias, mainView.getContext());
            }
        }
        AddButtonPlus("Alergia", layoutAlergias, alergias, mainView.getContext());

        // Rellenar patologías
        for (String patologia : patologias) {
            if (!patologia.isEmpty()) {
                addElement(patologia, layoutPatologias, patologias, mainView.getContext());
            }
        }
        AddButtonPlus("Patología", layoutPatologias, patologias, mainView.getContext());


        saveButton.setOnClickListener(
                v -> {
                    ViewGroup parent = (ViewGroup) mainView.getRootView();
                    Blocker.createBlocker(parent,requireActivity());
                    ArrayList<View> views = new ArrayList<>();
                    views.add(editTextNombre);
                    views.add(editTextApellido);
                    views.add(minKal);
                    views.add(maxKal);

                    ValidationResult result = Client.toMapData(views,alergias, patologias,saveData.getUser().getId());

                    if (!result.exit){
                        textError.setText(result.message);
                        textError.setVisibility(View.VISIBLE);
                        mostrarTextError();
                        Blocker.removeBlocker(parent);
                        return;
                    }else{
                        if (haveFill){
                            FireBaseRemover.remove(saveData.getIdCurrentClient().getId());
                        }

                        FireBaseWriter.saveData(Client.class, result).addOnFailureListener(
                                e -> {
                                    if (e instanceof ComplexFBCE) {
                                        textError.setText(((ComplexFBCE) e).reason.message);
                                    }else{
                                        textError.setText("Error al guardar el cliente");
                                    }
                                    textError.setVisibility(View.VISIBLE);
                                    Blocker.removeBlocker(parent);
                                    mostrarTextError();
                                }
                        ).addOnSuccessListener(
                                validationResult -> {
                                    saveData.setIdCurrentClient((Client) validationResult.result);
                                    Blocker.removeBlocker(parent);
                                    textError.setText("Cliente guardado correctamente");
                                    textError.setVisibility(View.VISIBLE);
                                    mostrarTextError();
                                }
                        );
                    }
                }
        );

        cancelButton.setOnClickListener(
                v ->{
                    dismiss();
                }
        );

        minKal.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        int min = 0;
                        int max = 0;

                        try {
                            min = Integer.parseInt(minKal.getText().toString());
                            max = Integer.parseInt(maxKal.getText().toString());

                            if (min>max){
                                min = max;
                            }

                        }catch (Exception ignored){}

                        minKal.setText(String.valueOf(min));
                        maxKal.setText(String.valueOf(max));

                    }
                }
        );
        maxKal.setOnFocusChangeListener(
                (v, hasFocus) -> {
                    if (!hasFocus) {
                        int min = 0;
                        int max = 0;

                        try {
                            min = Integer.parseInt(minKal.getText().toString());
                            max = Integer.parseInt(maxKal.getText().toString());

                            if (min>max){
                                max = min;
                            }

                        }catch (Exception ignored){}

                        minKal.setText(String.valueOf(min));
                        maxKal.setText(String.valueOf(max));
                    }
                }
        );

        return builder.create();
    }

    private void addElement(String txt, LinearLayout layout, List<String> list, Context context){

        LinearLayout item = new LinearLayout(context);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        item.setPadding(0,8,0,8);

        TextView text = new TextView(context);
        text.setText(txt);
        text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));

        ImageButton remove = new ImageButton(context);

        remove.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        remove.setBackgroundColor(Color.TRANSPARENT);
        remove.setOnClickListener(v -> {
            layout.removeView(item);
            list.remove(txt);
        });

        item.addView(text);
        item.addView(remove);
        layout.addView(item, layout.getChildCount()-1);

    }

    private void AddButtonPlus(String titulo, LinearLayout layout, List<String> list, Context context){

        Button button = new Button(context);
        button.setText("➕ Añadir " + titulo);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(Color.parseColor("#027C68"));
        button.setOnClickListener(
                v ->{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Añadir " + titulo);

                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Aceptar", (dialog, which) -> {
                        String nuevo = input.getText().toString().trim();
                        if (!nuevo.isEmpty() && !list.contains(nuevo)) {
                            list.add(nuevo);
                            addElement(nuevo, layout, list, context);
                        }
                    });

                    builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                    builder.show();
                }
        );

        layout.addView(button);
    }

    private void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 2000);
    }

}
