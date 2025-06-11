package com.tfg.dietaalplato.utilities.dialogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.TableGenerator;

import java.util.ArrayList;
import java.util.Map;

public class NewDietDialog extends DialogFragment {
    private View mainView;
    private SaveData saveData;
    private Food selectedFood;
    private LinearLayout selectedLayout;
    private LinearLayout currentLayout;
    private ImageButton bttReturn;
    private Button bttGuardar;
    private EditText egComida;
    private EditText nameRecepie;

    public static NewDietDialog getInstance() {
        return new NewDietDialog();
    }

    private NewDietDialog(){
        saveData = SaveData.getInstance();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.activity_new_diet_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);
        try {
            currentLayout = mainView.findViewById(R.id.addLayout);
            bttReturn = mainView.findViewById(R.id.bttReturn);
            bttReturn.setOnClickListener(v -> dismiss());
            bttGuardar = mainView.findViewById(R.id.buttonGuardar);
            bttGuardar.setOnClickListener(this::save);
            egComida = mainView.findViewById(R.id.grs);
            nameRecepie = mainView.findViewById(R.id.editTextNombre);

            readFood(foods -> {
                ScrollView sv = new ScrollView(mainView.getContext());
                sv.setBackgroundColor(Color.TRANSPARENT);
                sv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );

                LinearLayout ln = new LinearLayout(mainView.getContext());
                ln.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                ln.setBackgroundColor(Color.TRANSPARENT);
                ln.setOrientation(LinearLayout.VERTICAL);

                for (Food food: foods) {
                    ln.addView(generateList(food));
                }
                sv.addView(ln);
                currentLayout.addView(sv);
            });
        } catch (FBCException e) {
            Toast.makeText(mainView.getContext(),"No se han podido leer los alimentos", Toast.LENGTH_SHORT).show();
        }
        return builder.create();
    }

    private void readFood(OnResultCallBack<ArrayList<Food>> result) throws FBCException {
        Blocker.createBlocker((ViewGroup) mainView.getRootView(),mainView.getContext());
        FireBaseReader.readAllFoodFromUser(saveData.getUser().getId()).addOnFailureListener(
                e -> result.onResult(null)
        ).addOnSuccessListener( food -> {
                    ArrayList<Food> foods = new ArrayList<>(food.result.values());
                    result.onResult(foods);
                }
        );
        Blocker.removeBlocker((ViewGroup) mainView.getRootView());
    }

    private LinearLayout generateList(Food food){
        LinearLayout ln = new LinearLayout(mainView.getContext());
        ln.setBackgroundResource(R.drawable.bg_food_background);
        ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setPadding(10, 10, 10, 10);
        ln.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 5, 20, 5);
        ln.setLayoutParams(params);


        TextView tv = new TextView(mainView.getContext());
        tv.setText(food.getName());
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setOnClickListener(
                v -> {
                    if (selectedFood != food){
                        try{
                            ln.setBackgroundResource(R.drawable.bg_food_selected_background);
                            if (selectedFood != null && selectedLayout != null){
                                selectedLayout.setBackgroundResource(R.drawable.bg_food_background);
                                selectedLayout.removeView(tv);
                            }
                            selectedFood = food;
                            selectedLayout = ln;
                        }catch (Exception e){
                            Toast.makeText(mainView.getContext(),"Error al seleccionar el alimento", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Typeface customFont = ResourcesCompat.getFont(mainView.getContext(), R.font.lily_script_one);
        if (customFont != null){
            tv.setTypeface(customFont);
        }
        ln.addView(tv);
        return ln;
    }

    private void save(View view) {
        Blocker.createBlocker((ViewGroup) mainView.getRootView(),mainView.getContext());

        if (egComida.getText().toString() == null || egComida.getText().toString().equals("")){
            egComida.setText("0");
        }

        ArrayList<String> data = new ArrayList<>();
        data.add(saveData.getMomentOfDay());
        data.add("0"); //TODO  añadir el numeor del plato
        data.add(saveData.getDay());
        data.add(egComida.getText().toString());
        data.add(nameRecepie.getText().toString());

        ValidationResult result = FoodDiet.toMapData(data,saveData.getCurrentDiet().getId(),selectedFood.getId());
        Log.d("RESULT",result.toString());

        FireBaseWriter.saveData(FoodDiet.class,result).addOnFailureListener(
                e -> {
                    Toast.makeText(mainView.getContext(), "Error al guardar, compruebe los datos", Toast.LENGTH_SHORT).show();
                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                }
        ).addOnSuccessListener(
                aVoid -> {
                    Toast.makeText(mainView.getContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    try{
                        TableGenerator tableGenerator = TableGenerator.getInstance();
                        tableGenerator.resetAndGenerateTable();
                    }catch (Exception e){
                        Toast.makeText(mainView.getContext(),"Error al generar la tabla", Toast.LENGTH_SHORT).show();
                    }
                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                }
        );

    }

}