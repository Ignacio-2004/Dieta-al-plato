package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tfg.dietaalplato.utilities.SaveData;

public class AlimentosActivity extends AppCompatActivity {

    SaveData saveData = SaveData.getInstance();
    private LinearLayout layoutSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dieta_dia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        layoutSV = findViewById(R.id.layoutAlimentos);

        /*try{
            Blocker.createBlocker(this.findViewById(android.R.id.content),this);
            FireBaseReader.readFoodDietByDiet(saveData.getIdCurrentDiet().getId().toUpperCase()).addOnSuccessListener(
                    foodItems -> {
                        if (foodItems.exit){
                            for (ArrayList<FoodDiet>dietArrayList: foodItems.result.values()){
                                for (FoodDiet foodDiet: dietArrayList){
                                    LinearLayout item = new LinearLayout(this);
                                    item.setOrientation(LinearLayout.HORIZONTAL);
                                    item.setPadding(12, 5, 5, 12);
                                    item.setElevation(8f);
                                    layoutSV.addView(item);


                                    item = new LinearLayout(this);
                                    item.setOrientation(LinearLayout.HORIZONTAL);
                                    item.setBackgroundColor(Color.TRANSPARENT);
                                    item.setPadding(12, 12, 12, 12);
                                    item.setElevation(8f);

                                    TextView data = new TextView(this);
                                    data.setText(foodDiet.getName().substring(0, 1).toUpperCase() + foodDiet.getName().substring(1).toLowerCase());
                                    data.setTextColor(Color.WHITE);
                                    data.setBackgroundResource(R.drawable.bg_food_item);
                                    data.setTextSize(30);
                                    data.setGravity(Gravity.CENTER);
                                    data.setTypeface(null, Typeface.BOLD);
                                    data.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    ));
                                    item.addView(data);

                                    try {
                                        FireBaseReader.readAllFoodFromUser(saveData.getUser().getId()).addOnSuccessListener(
                                                foods -> {
                                                    LinearLayout item2 = new LinearLayout(this);
                                                    item2 = new LinearLayout(this);
                                                    item2.setOrientation(LinearLayout.VERTICAL);
                                                    item2.setBackgroundColor(Color.TRANSPARENT);
                                                    item2.setPadding(12, 12, 12, 12);
                                                    item2.setElevation(8f);
                                                    item2.setBackgroundResource(R.drawable.bg_food_item);
                                                    for (Food food : foods.result.values()) {
                                                        if (foodDiet.getIdAlimento().equals(food.getId())){
                                                            TextView data2 = new TextView(this);
                                                            data2.setText(food.getName().substring(0, 1).toUpperCase() + food.getName().substring(1).toLowerCase());
                                                            data2.setTextColor(Color.WHITE);
                                                            data2.setBackgroundColor(Color.TRANSPARENT);
                                                            data2.setTextSize(30);
                                                            data2.setGravity(Gravity.CENTER);
                                                            data2.setTypeface(null, Typeface.BOLD);
                                                            data2.setLayoutParams(new LinearLayout.LayoutParams(
                                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                                            ));
                                                            item2.addView(data2);
                                                        }
                                                    }
                                                }
                                        );
                                    } catch (FBCException e) {
                                        Log.e("FoodDiet", e.getMessage());
                                        Toast.makeText(this, "Error al cargar alimentos: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }


                                    layoutSV.addView(item);
                                }
                            }
                        }
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }
            ).addOnFailureListener(
                    e -> {
                        Toast.makeText(this, "Error al cargar alimentos", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }
            );
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar alimentos", Toast.LENGTH_SHORT).show();
            Blocker.removeBlocker(this.findViewById(android.R.id.content));
        }*/
    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, ComidasActivity.class);;
        startActivity(intent);
    }
}