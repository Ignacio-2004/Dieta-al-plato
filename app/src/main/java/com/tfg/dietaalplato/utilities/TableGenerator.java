package com.tfg.dietaalplato.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;

import java.util.ArrayList;
import java.util.List;

public class TableGenerator {

    private static SaveData saveData;

    private TableGenerator() {
        throw new UnsupportedOperationException("Clase estática, no instanciable.");
    }

    public static void generarTabla(Context context, LinearLayout contenedor, ArrayList<HeaderColumns> columns) throws FBCException {
        saveData = SaveData.getInstance();

        /*
            ++ Att of the table
         */
        TableLayout table = new TableLayout(context);
        table.setStretchAllColumns(true);
        table.setBackgroundColor(Color.TRANSPARENT);
        table.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        /*
            -- Att of the table
         */

        /*
            ++ Header of the table
         */
        TableRow headerRow = new TableRow(context);
        for (HeaderColumns header: columns) {
            headerRow.addView(generateHeader(context, header));
        }
        headerRow.addView(addBtnPlus(context));
        table.addView(headerRow);
        /*
            -- Header of the table
         */

        /*
            ++ Body of the table
         */

        if (saveData.getCurrentDiet().getId() != null && !saveData.getCurrentDiet().getId().isEmpty()) {
            FireBaseReader.readFoodDietByDiet(saveData.getCurrentDiet().getId()).addOnFailureListener(
                    e -> Toast.makeText(context, "Error al leer los alimentos", Toast.LENGTH_SHORT).show()
            ).addOnSuccessListener(
                    foodDiets -> {
                        Toast.makeText(context, "Alimentos leidos", Toast.LENGTH_SHORT).show();

                        if (foodDiets.result != null) {
                            for (ArrayList<FoodDiet> fd: foodDiets.result.values()) {
                                //La arraylist es cada receta unificada por el nombre

                                FoodDiet f = fd.get(0);

                                if (f.getDia().equals(saveData.getDay()) && f.getNumeroPlato().equals(saveData.getMomentOfDay())) {
                                    //Con este filtro cogemos solo los que pertenecen a este dia y ese momento del dia

                                    try {
                                        FireBaseReader.readAllFoodFromUser(saveData.getCurrentClient().getId()).addOnFailureListener(
                                                e -> Toast.makeText(context, "Error al leer los alimentos", Toast.LENGTH_SHORT).show()
                                        ).addOnSuccessListener(
                                                foods -> {
                                                    ArrayList<Food> foodArrayList = new ArrayList<>();
                                                    for (FoodDiet foodDiet: fd) {
                                                        for (Food food: foods.result.values()) {
                                                            if (foodDiet.getIdAlimento().equals(food.getId())) {
                                                                foodArrayList.add(food);
                                                            }
                                                        }
                                                    }
                                                    table.addView(generateRow(context, f.getName(), foodArrayList, columns));
                                                }
                                        );
                                    } catch (FBCException e) {
                                        Toast.makeText(context, "Error al leer los alimentos", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        }
                         /*
                        ++ Plus last row
                        */

                        table.addView(generateLastRow(context, columns));

                        /*
                        -- Plus last row
                        */

                        /*
                        Add table to the container
                        */
                        if (contenedor != null && context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {
                                contenedor.addView(table);
                            });
                        }

                    }
            );
        }else{
            Toast.makeText(context, "No hay dieta seleccionada", Toast.LENGTH_SHORT).show();
        }

        /*
            -- Body of the table
         */
    }

    private static TextView generateHeader(Context context, HeaderColumns header){
        TextView th = new TextView(context);
        th.setText(header.name());
        th.setTextColor(Color.parseColor("#027C68"));
        th.setTextSize(30);
        th.setPadding(20, 20, 20, 20);
        th.setGravity(Gravity.CENTER);

        Typeface customFont = ResourcesCompat.getFont(context, R.font.lily_script_one);
        if (customFont != null) {
            th.setTypeface(customFont);
        }

        th.setBackgroundResource(R.drawable.bg_food_item);
        th.setLayoutParams(margin());

        return th;
    }

    private static String addAttFood(List<Food>foods, HeaderColumns att,Context context){
        int i = 0;

       try{
           for (Food f: foods) {
               switch (att){
                   case Kcal:
                       i += Integer.parseInt(f.getEnergia());
                       break;
                   case Proteina:
                       i += Integer.parseInt(f.getProteina());
                       break;
                   case Grasa:
                       i += Integer.parseInt(f.getGrasa());
                       break;
                   case AGs:
                       i += Integer.parseInt(f.getAgs());
                       break;
                   case AGmi:
                       i += Integer.parseInt(f.getAgmi());
                       break;
                   case AGpi:
                       i += Integer.parseInt(f.getAgpi());
                       break;
                   case Colesterol:
                       i += Integer.parseInt(f.getColesterol());
                       break;
                   case HC:
                       i += Integer.parseInt(f.getHc());
                       break;
                   case Fibra:
                       i += Integer.parseInt(f.getFibra());
                       break;
                   case VitC:
                       i += Integer.parseInt(f.getVitC());
                       break;
                   case VitB6:
                       i += Integer.parseInt(f.getVitB6());
                       break;
                   case VitE:
                       i += Integer.parseInt(f.getVitE());
                       break;
                   case Hierro:
                       i += Integer.parseInt(f.getHierro());
                       break;
                   case Sodio:
                       i += Integer.parseInt(f.getSodio());
                       break;
                   case Calcio:
                       i += Integer.parseInt(f.getCalcio());
                       break;
                   case Potasio:
                       i += Integer.parseInt(f.getPotasio());
                       break;
               }
           }
       }catch (Exception e){
           Toast.makeText(context, "Error: con uno de los alimantos que imposibilita su lectura", Toast.LENGTH_SHORT).show();
       }

        return String.valueOf(i);
    }

    private static TableRow.LayoutParams margin(){
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 10, 10);

        return params;
    }

    private static Button addBtnPlus(Context context){
        Button addCol = new Button(context);
        addCol.setText("+");
        addCol.setTextSize(30);
        addCol.setTextColor(Color.parseColor("#027C68"));
        addCol.setBackgroundResource(R.drawable.bg_food_item);

        return addCol;
    }

    private static TableRow generateRow (Context context, String nameRecipe, List<Food> foods,ArrayList<HeaderColumns> headers){
        TableRow row = new TableRow(context);
        LinearLayout name = generateCommonCell(context, nameRecipe);
        ScrollView scrollView = generateFoodCell(context, foods);
        Button btn = addBtnPlus(context);

        if (name.getHeight() < scrollView.getHeight()) {
            name.getLayoutParams().height = scrollView.getHeight();
            name.requestLayout();
        }

        name.setLayoutParams(margin());
        scrollView.setLayoutParams(margin());
        btn.setLayoutParams(margin());


        row.addView(name);
        row.addView(scrollView);

        for (HeaderColumns header: headers) {
            try {
                HeaderColumns columna = HeaderColumns.valueOf(header.name());
                row.addView(generateCommonCell(context, addAttFood(foods, columna, context)));
            } catch (IllegalArgumentException e) {
                Toast.makeText(context, "No ha sido posible cargar la siguiente columna: " + header, Toast.LENGTH_SHORT).show();
            }
        }

        row.addView(btn);

        return row;
    }

    private static LinearLayout generateCommonCell(Context context, String text){
        LinearLayout cell = new LinearLayout(context);
        cell.setOrientation(LinearLayout.HORIZONTAL);
        cell.setGravity(Gravity.CENTER);
        cell.setVerticalGravity(Gravity.CENTER);
        cell.setBackgroundResource(R.drawable.bg_food_item);

        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setPadding(15, 15, 15, 15);
        tv.setTextColor(Color.parseColor("#027C68"));
        cell.addView(tv);

        cell.setLayoutParams(margin());
        return cell;
    }

    private static ScrollView generateFoodCell(Context context, List<Food> food){
        ScrollView scrollView = new ScrollView(context);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        scrollParams.height = 200; // Altura máxima en dp
        scrollView.setLayoutParams(scrollParams);
        scrollView.setBackgroundColor(Color.TRANSPARENT);
        scrollView.setFillViewport(true);

        LinearLayout alimentosLayout = new LinearLayout(context);
        alimentosLayout.setOrientation(LinearLayout.HORIZONTAL);
        alimentosLayout.setGravity(Gravity.CENTER_VERTICAL);
        alimentosLayout.setBackgroundColor(Color.TRANSPARENT);

        for (Food f: food) {
            LinearLayout fRow = new LinearLayout(context);
            fRow.setOrientation(LinearLayout.HORIZONTAL);
            fRow.setGravity(Gravity.CENTER_VERTICAL);
            fRow.setBackgroundResource(R.drawable.bg_food_background);

            TextView name = new TextView(context);
            name.setText(f.getName());
            name.setTextSize(14);
            name.setPadding(10, 10, 10, 10);
            name.setOnClickListener(v -> {
                // Aquí puedes abrir un diálogo o ir a una nueva actividad
            });

            ImageButton info = new ImageButton(context);
            info.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            info.setBackgroundColor(Color.TRANSPARENT);
            info.setOnClickListener(v -> {
                // Aquí puedes abrir un diálogo o ir a una nueva actividad
            });

            fRow.addView(name);
            fRow.addView(info);
            alimentosLayout.addView(fRow);

        }

        alimentosLayout.addView(addBtnPlus(context));

        scrollView.addView(alimentosLayout);
        return scrollView;
    }

    private static TableRow generateLastRow(Context context, ArrayList<HeaderColumns> header){
        TableRow row = new TableRow(context);
        Button btn = addBtnPlus(context);
        ScrollView scrollView = generateFoodCell(context, new ArrayList<>());

        btn.setLayoutParams(margin());
        row.addView(btn);
        row.addView(scrollView);

        // Esperamos a que ambas vistas estén medidas
        btn.post(() -> {
            int nameHeight = btn.getHeight();
            int scrollHeight = scrollView.getHeight();

            int maxHeight = Math.max(nameHeight, scrollHeight);

            // Aplicamos misma altura a ambos
            btn.getLayoutParams().height = maxHeight;
            scrollView.getLayoutParams().height = maxHeight;

            // Es necesario llamar a requestLayout para que el cambio se aplique
            btn.requestLayout();
            scrollView.requestLayout();
        });




        for (HeaderColumns headerC: header) {
            row.addView(generateCommonCell(context, ""));
        }

        return row;
    }

}
