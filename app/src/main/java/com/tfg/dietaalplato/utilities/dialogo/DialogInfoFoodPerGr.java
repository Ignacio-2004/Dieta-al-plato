package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseRemover;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;

import java.util.ArrayList;

public class DialogInfoFoodPerGr extends DialogFragment {

    private View mainView;
    private Button btnDecrement;
    private Button btnIncrement;
    private Button btnSave;
    private Button btnCancel;
    private Double originalGr;
    private TextView errTv;
    private boolean hasChange;

    private EditText inputAlimento;
    private EditText inputgr;
    private EditText inputPC;
    private EditText inputE100;
    private EditText inputProt100;
    private EditText inputGrasa100;
    private EditText inputAGS100;
    private EditText inputAGMI100;
    private EditText inputAGPI100;
    private EditText inputCol100;
    private EditText inputHC100;
    private EditText inputFibra100;
    private EditText inputVitC100;
    private EditText inputVitB6100;
    private EditText inputVitE100;
    private EditText inputHierro100;
    private EditText inputSodio100;
    private EditText inputCalcio100;
    private EditText inputPotasio100;
    private Food currentFood;
    private FoodDiet currentFoodDiet;


    public static DialogInfoFoodPerGr getInstance() {
        DialogInfoFoodPerGr fragment = new DialogInfoFoodPerGr();
        return fragment;
    }

    private DialogInfoFoodPerGr(){
        currentFood = new Food();
        currentFoodDiet = new FoodDiet();
        hasChange = false;
    }

    public void setFood(Food food) {
        currentFood = food;
    }

    public void setFoodDiet(FoodDiet foodDiet) {
        currentFoodDiet = foodDiet;
        originalGr = safeParse(foodDiet.getG());
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_info_food_per_gr, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        inputAlimento = mainView.findViewById(R.id.inputAlimento);
        inputgr = mainView.findViewById(R.id.inputgr);
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
        inputHierro100 = mainView.findViewById(R.id.inputFe100);
        inputSodio100 = mainView.findViewById(R.id.inputNa100);
        inputCalcio100 = mainView.findViewById(R.id.inputCa100);
        inputPotasio100 = mainView.findViewById(R.id.inputK100);
        btnDecrement = mainView.findViewById(R.id.btnDecrementProt100);
        btnIncrement = mainView.findViewById(R.id.btnIncrementProt100);
        btnSave = mainView.findViewById(R.id.btnGuardar);
        btnCancel = mainView.findViewById(R.id.btnCancelar);
        errTv = mainView.findViewById(R.id.txtMensajeErrorAlimento);

        inputgr.setText(String.valueOf(currentFoodDiet.getG()));

        btnCancel.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (hasChange) {
                     Activity activity = getActivity();
                     Intent intent = activity.getIntent();
                     activity.finish();
                     activity.startActivity(intent);

                 }
                 dismiss();
             }
        });

        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalGr != safeParse(inputgr.getText().toString())) {

                    ArrayList<String> data = new ArrayList<>();
                    data.add(currentFoodDiet.getComida());
                    data.add(currentFoodDiet.getNumeroPlato());
                    data.add(currentFoodDiet.getDia());
                    data.add(inputgr.getText().toString());
                    data.add(currentFoodDiet.getName());


                    ValidationResult validationResult = FoodDiet.toMapData(data, currentFoodDiet.getIdDieta(), currentFood.getId(), currentFoodDiet.getId());

                    if (validationResult.exit) {
                        ViewGroup parent = (ViewGroup) mainView.getRootView();
                        Blocker.createBlocker(parent,requireActivity());

                        FireBaseRemover.remove(currentFoodDiet.getId()).addOnFailureListener(
                                e -> {
                                    errTv.setText("Error al guardar");
                                    errTv.setVisibility(View.VISIBLE);
                                    mostrarTextError();
                                    Blocker.removeBlocker(parent);
                                }
                        ).addOnSuccessListener(
                                aVoid -> {

                                    FireBaseWriter.saveData(FoodDiet.class,validationResult).addOnFailureListener(
                                            e -> {
                                                errTv.setText("Error al guardar");
                                                errTv.setVisibility(View.VISIBLE);
                                                mostrarTextError();
                                                Blocker.removeBlocker(parent);
                                            }
                                    ).addOnSuccessListener(
                                            aVoid1 ->{
                                                errTv.setText("Cambio guardado correctamente");
                                                errTv.setVisibility(View.VISIBLE);
                                                hasChange = true;
                                                mostrarTextError();
                                                Blocker.removeBlocker(parent);
                                            }
                                    );
                                }
                        );
                    }
                }else{
                    errTv.setText("No se ha realizado ningún cambio");
                    errTv.setVisibility(View.VISIBLE);
                    mostrarTextError();
                }
            }
        });

        btnDecrement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputgr.setText(String.valueOf(safeParse(inputgr.getText().toString())-1));
                refreshUI();
            }
        });

        btnIncrement.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputgr.setText(String.valueOf(safeParse(inputgr.getText().toString())+1));
                refreshUI();
            }
        });


        refreshUI();

        inputgr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    refreshUI();
                }
            }
        });


        return builder.create();
    }

    private void refreshUI() {
        inputAlimento.setText(currentFood.getName());
        inputPC.setText(String.valueOf(safeParse(currentFood.getPc())*safeParse(inputgr.getText().toString())/100));
        inputE100.setText(String.valueOf(safeParse(currentFood.getEnergia())*safeParse(inputgr.getText().toString())/100));
        inputProt100.setText(String.valueOf(safeParse(currentFood.getProteina())*safeParse(inputgr.getText().toString())/100));
        inputGrasa100.setText(String.valueOf(safeParse(currentFood.getGrasa())*safeParse(inputgr.getText().toString())/100));
        inputAGS100.setText(String.valueOf(safeParse(currentFood.getAgs())*safeParse(inputgr.getText().toString())/100));
        inputAGMI100.setText(String.valueOf(safeParse(currentFood.getAgmi())*safeParse(inputgr.getText().toString())/100));
        inputAGPI100.setText(String.valueOf(safeParse(currentFood.getAgpi())*safeParse(inputgr.getText().toString())/100));
        inputCol100.setText(String.valueOf(safeParse(currentFood.getColesterol())*safeParse(inputgr.getText().toString())/100));
        inputHC100.setText(String.valueOf(safeParse(currentFood.getHc())*safeParse(inputgr.getText().toString())/100));
        inputFibra100.setText(String.valueOf(safeParse(currentFood.getFibra())*safeParse(inputgr.getText().toString())/100));
        inputVitC100.setText(String.valueOf(safeParse(currentFood.getVitC())*safeParse(inputgr.getText().toString())/100));
        inputVitB6100.setText(String.valueOf(safeParse(currentFood.getVitB6())*safeParse(inputgr.getText().toString())/100));
        inputVitE100.setText(String.valueOf(safeParse(currentFood.getVitE())*safeParse(inputgr.getText().toString())/100));
        inputHierro100.setText(String.valueOf(safeParse(currentFood.getHierro())*safeParse(inputgr.getText().toString())/100));
        inputSodio100.setText(String.valueOf(safeParse(currentFood.getSodio())*safeParse(inputgr.getText().toString())/100));
        inputCalcio100.setText(String.valueOf(safeParse(currentFood.getCalcio())*safeParse(inputgr.getText().toString())/100));
        inputPotasio100.setText(String.valueOf(safeParse(currentFood.getPotasio())*safeParse(inputgr.getText().toString())/100));
    }

    private static double safeParse(String value) {
        if (value == null) return 0.0;
        value = value.trim().replace(",", ".");
        if (value.isEmpty() || value.equals("-")) return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void mostrarTextError() {
        errTv.postDelayed(() -> errTv.setVisibility(View.GONE), 2000);
    }

}