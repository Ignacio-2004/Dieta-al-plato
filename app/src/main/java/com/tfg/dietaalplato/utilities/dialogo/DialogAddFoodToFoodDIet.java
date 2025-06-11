package com.tfg.dietaalplato.utilities.dialogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.HeaderColumns;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.TableGenerator;

import java.util.ArrayList;

public class DialogAddFoodToFoodDIet extends DialogFragment {

    private View mainView; //R
    private SaveData saveData; //R
    private TableGenerator tableGenerator; //R

    private ArrayList<Food> currentFoods; //R
    private ArrayList<Food> foods; //R

    private LinearLayout currentLayout; //R
    private LinearLayout toAddLayout; //R
    private ImageButton bttReturn; //R
    private Button bttSave; //R





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
                    cln.addView(addRermoveBotton(generateList(food, v -> {}),v -> {}));
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
        ln.addView(tv);
        return ln;
    }

    private LinearLayout addRermoveBotton(LinearLayout ln,View.OnClickListener listener){

        ImageButton remove = new ImageButton(mainView.getContext());

        remove.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        remove.setBackgroundColor(Color.TRANSPARENT);
        remove.setScaleX(0.8f);
        remove.setScaleY(0.8f);
        remove.setOnClickListener(listener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,0);
        remove.setLayoutParams(params);

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
}