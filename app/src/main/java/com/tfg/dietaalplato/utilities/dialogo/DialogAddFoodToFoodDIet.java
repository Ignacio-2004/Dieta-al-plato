package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseRemover;
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

public class DialogAddFoodToFoodDIet extends DialogFragment {

    private View mainView; //R
    private SaveData saveData; //R
    private TableGenerator tableGenerator; //R

    private ArrayList<Food> currentFoods; //R
    private ArrayList<Food> foods; //R
    private ArrayList<FoodDiet> currentsfoodDiets; //R
    private Food selectedFood;
    private LinearLayout selectedLayout;

    private LinearLayout currentLayout; //R
    private LinearLayout toAddLayout; //R
    private ImageButton bttReturn; //R
    private Button bttSave; //R
    private Button bttIncrement; //R
    private Button bttDecrement; //R
    private EditText inputgr;//R
    private TextView errTv; //R





    public static DialogAddFoodToFoodDIet getInstance() {
        return new DialogAddFoodToFoodDIet();
    }

    private DialogAddFoodToFoodDIet() {
        tableGenerator = TableGenerator.getInstance();
        saveData = SaveData.getInstance();
    }

    public void setFoods(ArrayList<Food> foods){
        this.currentFoods = foods;
    }
    public void setCurrentsfoodDiets(ArrayList<FoodDiet> currentsfoodDiets){this.currentsfoodDiets = currentsfoodDiets;}

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_food_to_food_diet, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        try {
            loadFood(o -> {
                currentLayout = mainView.findViewById(R.id.actualLayout);
                toAddLayout = mainView.findViewById(R.id.addLayout);
                bttReturn = mainView.findViewById(R.id.bttReturn);
                bttSave = mainView.findViewById(R.id.buttonGuardar);
                bttIncrement = mainView.findViewById(R.id.btnIncrementProt100);
                bttDecrement = mainView.findViewById(R.id.btnDecrementProt100);
                inputgr = mainView.findViewById(R.id.inputgr);
                errTv = mainView.findViewById(R.id.txtMensajeErrorAlimento);

                View.OnClickListener listener = v -> {
                    if (selectedFood != null){
                        if (currentFoods.contains(selectedFood)){

                            for (FoodDiet foodDiet : currentsfoodDiets) {
                                if (foodDiet.getIdAlimento().equals(selectedFood.getId())) {
                                    ArrayList<String> data = new ArrayList<>();
                                    data.add(foodDiet.getComida());
                                    data.add(foodDiet.getNumeroPlato());
                                    data.add(foodDiet.getDia());
                                    data.add(inputgr.getText().toString());
                                    data.add(foodDiet.getName());

                                    ValidationResult validationResult = FoodDiet.toMapData(data, foodDiet.getIdDieta(), selectedFood.getId(), foodDiet.getId());

                                    if (validationResult.exit) {
                                        Blocker.createBlocker((ViewGroup) mainView.getRootView(), mainView.getContext());

                                        FireBaseRemover.remove(foodDiet.getId()).addOnFailureListener(
                                                e -> {
                                                    errTv.setText("Error al guardar");
                                                    errTv.setVisibility(View.VISIBLE);
                                                    mostrarTextError();
                                                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                                                }
                                        ).addOnSuccessListener(
                                                aVoid -> {

                                                    FireBaseWriter.saveData(FoodDiet.class,validationResult).addOnFailureListener(
                                                            e -> {
                                                                errTv.setText("Error al guardar");
                                                                errTv.setVisibility(View.VISIBLE);
                                                                mostrarTextError();
                                                                Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                                                            }
                                                    ).addOnSuccessListener(
                                                            aVoid1 ->{
                                                                errTv.setText("Cambio guardado correctamente");
                                                                errTv.setVisibility(View.VISIBLE);
                                                                mostrarTextError();

                                                                for (int i = 0; i < currentsfoodDiets.size(); i++) {
                                                                    if (currentsfoodDiets.get(i).getId().equals(foodDiet.getId())){
                                                                        currentsfoodDiets.remove(i);
                                                                        break;
                                                                    }
                                                                }
                                                                currentsfoodDiets.add((FoodDiet) aVoid1.result);

                                                                Activity activity = getActivity();
                                                                // Esperar un poco para que el mensaje se muestre
                                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                                    dismiss();

                                                                    // Esperamos a que se cierre bien para abrir el nuevo diálogo
                                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                                        if (activity != null && !currentFoods.isEmpty()) {
                                                                            DialogAddFoodToFoodDIet nuevoDialog = new DialogAddFoodToFoodDIet();
                                                                            nuevoDialog.setFoods(currentFoods);
                                                                            nuevoDialog.setCurrentsfoodDiets(currentsfoodDiets);
                                                                            nuevoDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), "DialogAddFoodToFoodDIet");
                                                                        }
                                                                    }, 100);

                                                                }, 600); // le das medio segundo para que se vea el mensaje

                                                                Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                                                            }
                                                    );
                                                }
                                        );
                                    }
                                }
                            }
                        }else{
                            for (FoodDiet foodDiet : currentsfoodDiets) {
                                if (!currentsfoodDiets.isEmpty()) {
                                    ArrayList<String> data = new ArrayList<>();
                                    data.add(saveData.getMomentOfDay());
                                    data.add("0");
                                    data.add(saveData.getDay());
                                    data.add(inputgr.getText().toString());
                                    data.add(currentsfoodDiets.get(0).getName());

                                    ValidationResult validationResult = FoodDiet.toMapData(data, currentsfoodDiets.get(0).getIdDieta(), selectedFood.getId(),foodDiet.getId());

                                    if (validationResult.exit) {
                                        Blocker.createBlocker((ViewGroup) mainView.getRootView(), mainView.getContext());

                                        FireBaseWriter.saveData(FoodDiet.class,validationResult).addOnFailureListener(
                                                e -> {
                                                    errTv.setText("Error al guardar");
                                                    errTv.setVisibility(View.VISIBLE);
                                                    mostrarTextError();
                                                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                                                }
                                        ).addOnSuccessListener(
                                                aVoid -> {
                                                    errTv.setText("Cambio guardado correctamente");
                                                    errTv.setVisibility(View.VISIBLE);
                                                    mostrarTextError();

                                                    if (!currentsfoodDiets.contains(aVoid.result)) {
                                                        currentsfoodDiets.add((FoodDiet) aVoid.result);
                                                    }
                                                    if (!currentFoods.contains(selectedFood)) {
                                                        currentFoods.add(selectedFood);
                                                    }

                                                    Activity activity = getActivity();
                                                    // Esperar un poco para que el mensaje se muestre
                                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                        dismiss();

                                                        // Esperamos a que se cierre bien para abrir el nuevo diálogo
                                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                            if (activity != null && !currentFoods.isEmpty()) {
                                                                DialogAddFoodToFoodDIet nuevoDialog = new DialogAddFoodToFoodDIet();
                                                                nuevoDialog.setFoods(currentFoods);
                                                                nuevoDialog.setCurrentsfoodDiets(currentsfoodDiets);
                                                                nuevoDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), "DialogAddFoodToFoodDIet");
                                                            }
                                                        }, 100);

                                                    }, 600); // le das medio segundo para que se vea el mensaje

                                                    Blocker.removeBlocker((ViewGroup) mainView.getRootView());
                                                }
                                        );
                                    }
                                } else {
                                    Log.w("GuardarAlimento", "currentsfoodDiets está vacío al intentar guardar un nuevo alimento.");
                                }
                            }
                        }
                    }
                };

                bttSave.setOnClickListener(listener);

                bttDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inputgr.getText() != null || !inputgr.getText().toString().equals("")){
                            double gr = Double.parseDouble(inputgr.getText().toString())-1;
                            inputgr.setText(String.valueOf(gr));
                        }
                    }
                });

                bttIncrement.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inputgr.getText() != null || !inputgr.getText().toString().equals("")){
                            double gr = Double.parseDouble(inputgr.getText().toString())+1;
                            inputgr.setText(String.valueOf(gr));
                        }
                    }
                });

                bttReturn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         dismiss();
                     }
                });

                ScrollView csv = new ScrollView(mainView.getContext());
                csv.setBackgroundColor(Color.TRANSPARENT);
                csv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );

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

                LinearLayout cln = new LinearLayout(mainView.getContext());
                cln.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                );
                cln.setBackgroundColor(Color.TRANSPARENT);
                cln.setOrientation(LinearLayout.VERTICAL);

                for (Food food : foods) {
                    if (!currentFoods.contains(food)){
                        ln.addView(generateList(food, v -> {}));
                    }
                }
                sv.addView(ln);

                for (Food food : currentFoods) {
                    for (FoodDiet foodDiet : currentsfoodDiets) {
                        if (foodDiet.getIdAlimento().equals(food.getId())) {
                            cln.addView(addRermoveBotton(generateList(food, v -> {}), foodDiet));
                        }
                    }
                }
                csv.addView(cln);

                currentLayout.addView(csv);
                toAddLayout.addView(sv);
            });
        }catch (Exception e){
            Toast.makeText(mainView.getContext(), "Error al cargar los alimentos", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return builder.create();
    }

    private LinearLayout generateList(Food food,View.OnClickListener listener){
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
        tv.setOnClickListener(listener);
        Typeface customFont = ResourcesCompat.getFont(mainView.getContext(), R.font.lily_script_one);
        if (customFont != null){
            tv.setTypeface(customFont);
        }

        View.OnClickListener listener2 = v -> {
            if (selectedFood != food){
                try{
                    ln.setBackgroundResource(R.drawable.bg_food_selected_background);
                    if (selectedFood != null && selectedLayout != null){
                        selectedLayout.setBackgroundResource(R.drawable.bg_food_background);
                        selectedLayout.removeView(tv);
                    }
                    selectedFood = food;
                    selectedLayout = ln;

                    if (currentFoods.contains(food)){
                        for (FoodDiet foodDiet : currentsfoodDiets) {
                            if (foodDiet.getIdAlimento().equals(food.getId())){
                                inputgr.setText(String.valueOf(foodDiet.getG()));
                            }
                        }
                    }else{
                        inputgr.setText("0");
                    }

                }catch (Exception e){
                    Toast.makeText(mainView.getContext(),"Error al seleccionar el alimento", Toast.LENGTH_SHORT).show();
                }
            }
        };
        ln.setOnClickListener(listener2);
        tv.setOnClickListener(listener2);
        ln.addView(tv);
        return ln;
    }

    private LinearLayout addRermoveBotton(LinearLayout ln,FoodDiet foodDiet){

        ImageButton remove = new ImageButton(mainView.getContext());

        remove.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        remove.setBackgroundColor(Color.TRANSPARENT);
        remove.setScaleX(0.8f);
        remove.setScaleY(0.8f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,0);
        remove.setLayoutParams(params);

        View.OnClickListener listener2 = v -> {
            Blocker.createBlocker((ViewGroup) mainView.getRootView() ,mainView.getContext());
            FireBaseRemover.remove(foodDiet.getId()).addOnFailureListener(e -> {
                Toast.makeText(mainView.getContext(),"Error al eliminar el alimento", Toast.LENGTH_SHORT).show();
                Blocker.removeBlocker((ViewGroup) mainView.getRootView());
            }).addOnSuccessListener(aVoid -> {
                for (int i = 0; i < currentFoods.size(); i++) {
                    if (currentFoods.get(i).getId().equals(foodDiet.getIdAlimento())){
                        currentFoods.remove(i);
                        for (int j = 0; j < currentsfoodDiets.size(); j++) {
                            if (currentsfoodDiets.get(j).getId().equals(foodDiet.getId())) {
                                currentsfoodDiets.remove(j);
                                break;
                            }
                        }
                        break;
                    }
                }

                Activity activity = getActivity();
                dismiss();

                //Esperamos a que se cierrer bn para abrirlo sin problemas
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (activity != null && !currentFoods.isEmpty()) {
                            DialogAddFoodToFoodDIet nuevoDialog = new DialogAddFoodToFoodDIet();
                            nuevoDialog.setFoods(currentFoods);
                            nuevoDialog.setCurrentsfoodDiets(currentsfoodDiets);
                            nuevoDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), "DialogAddFoodToFoodDIet");
                        }
                    }, 100);

                }, 100);
                Blocker.removeBlocker((ViewGroup) mainView.getRootView());
            });
        };

        remove.setOnClickListener(listener2);

        ln.addView(remove);

        return ln;
    }

    private void loadFood(OnResultCallBack<Void> onResultCallBack) throws FBCException {
        FireBaseReader.readAllFoodFromUser(saveData.getUser().getId()).addOnFailureListener(
                e -> foods = null
        ).addOnSuccessListener(
                foods -> {
                    if (foods.result == null){
                        this.foods = new ArrayList<>();
                    }else{
                        this.foods = new ArrayList<>(foods.result.values());
                    }
                    onResultCallBack.onResult(null);
                }
        );
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        try {
            TableGenerator.getInstance().resetAndGenerateTable();
        } catch (FBCException e) {
            Toast.makeText(mainView.getContext(), "No ha sido posible regenerar la tabla", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarTextError() {
        errTv.postDelayed(() -> errTv.setVisibility(View.GONE), 2000);
    }
}