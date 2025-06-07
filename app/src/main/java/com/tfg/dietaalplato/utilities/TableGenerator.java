package com.tfg.dietaalplato.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
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
    public static ArrayList<HeaderColumns> columns = new ArrayList<>();

    private TableGenerator() {
        throw new UnsupportedOperationException("Clase estática, no instanciable.");
    }

    public static void generarTabla(Context context, LinearLayout contenedor, View.OnClickListener onClickAddHeader) throws FBCException {
        saveData = SaveData.getInstance();
        if (columns.isEmpty()){
            columns.add(HeaderColumns.Nombre);
            columns.add(HeaderColumns.Alimentos);
            columns.add(HeaderColumns.Kcal);
            columns.add(HeaderColumns.Proteina);
            columns.add(HeaderColumns.HC);
            columns.add(HeaderColumns.Grasa);
        }
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
        headerRow.addView(addBtnPlus(context, onClickAddHeader));
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

    private static TableRow.LayoutParams insiteSVmargin(){
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 10, 10, 10);

        return params;
    }

    private static Button addBtnPlus(Context context, View.OnClickListener onClickAddHeader){
        Button addCol = new Button(context);
        addCol.setText("+");
        addCol.setTextSize(30);
        addCol.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        addCol.setTextColor(Color.parseColor("#027C68"));
        addCol.setBackgroundResource(R.drawable.bg_food_item);
        addCol.setOnClickListener(onClickAddHeader);

        return addCol;
    }

    private static TableRow generateRow (Context context, String nameRecipe, List<Food> foods,ArrayList<HeaderColumns> headers){
        TableRow row = new TableRow(context);
        LinearLayout name = generateCommonCell(context, nameRecipe);
        LinearLayout scrollView = generateFoodCell(context, foods);

        //temp
        View.OnClickListener onClickAddHeader = v -> {
            Toast.makeText(context, "Añadir columna", Toast.LENGTH_SHORT).show();
        };
        //temp
        Button btn = addBtnPlus(context, onClickAddHeader);

        scrollView.setLayoutParams(margin());

        name.setLayoutParams(margin());
        scrollView.setLayoutParams(margin());
        btn.setLayoutParams(margin());


        row.addView(name);
        row.addView(scrollView);

        ArrayList<LinearLayout> cells = new ArrayList<>();
        for (HeaderColumns header: headers) {
            if (!header.equals(HeaderColumns.Nombre) && !header.equals(HeaderColumns.Alimentos)) {
                try {
                    HeaderColumns columna = HeaderColumns.valueOf(header.name());
                    LinearLayout cell = generateCommonCell(context, addAttFood(foods, columna, context));
                    row.addView(cell);
                    cells.add(cell);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(context, "No ha sido posible cargar la siguiente columna: " + header, Toast.LENGTH_SHORT).show();
                }
            }
        }

        row.addView(btn);

        // Ajustar alturas tras renderizado
        scrollView.post(() -> {
            int targetHeight = scrollView.getHeight();
            name.getLayoutParams().height = targetHeight;
            name.requestLayout();

            for (LinearLayout cell : cells) {
                cell.getLayoutParams().height = targetHeight;
                cell.requestLayout();
            }
            for (int i = 2; i < row.getChildCount(); i++) {
                View v = row.getChildAt(i);
                ViewGroup.LayoutParams p = v.getLayoutParams();
                p.height = targetHeight;
                v.setLayoutParams(p);
            }
        });

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
        tv.setTextSize(20);
        tv.setPadding(15, 15, 15, 15);
        tv.setTextColor(Color.parseColor("#027C68"));
        cell.addView(tv);

        cell.setLayoutParams(margin());
        return cell;
    }

    private static LinearLayout generateFoodCell(Context context, List<Food> food) {
        // Contenedor externo (la celda que se mete en la tabla)
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(margin());
        container.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        container.setBackgroundResource(R.drawable.bg_food_item); // estilo como otras celdas

        // ScrollView para lista vertical
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(context, 150)
        ));
        scrollView.setFillViewport(true);

        // Layout interno que contendrá los alimentos
        LinearLayout alimentosLayout = new LinearLayout(context);
        alimentosLayout.setOrientation(LinearLayout.VERTICAL);
        alimentosLayout.setLayoutParams(new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Añadir los alimentos
        for (Food f : food) {
            LinearLayout fRow = new LinearLayout(context);
            fRow.setOrientation(LinearLayout.HORIZONTAL);
            fRow.setGravity(Gravity.CENTER_VERTICAL);
            fRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            fRow.setBackgroundResource(R.drawable.bg_food_background);
            fRow.setLayoutParams(insiteSVmargin());

            TextView name = new TextView(context);
            name.setText(f.getName());
            name.setTextSize(14);
            name.setPadding(10, 10, 10, 10);

            ImageButton info = new ImageButton(context);
            info.setImageResource(android.R.drawable.ic_menu_info_details);
            info.setBackgroundColor(Color.TRANSPARENT);
            info.setOnClickListener(v -> {
                Toast.makeText(context, "Ver info de " + f.getName(), Toast.LENGTH_SHORT).show();
            });

            fRow.addView(name);
            fRow.addView(info);

            alimentosLayout.addView(fRow);
        }

        // Botón "+" para añadir alimentos
        //temp
        View.OnClickListener onClickAddHeader = v -> {
            Toast.makeText(context, "Añadir alimento", Toast.LENGTH_SHORT).show();
        };
        //temp
        Button botonAdd = addBtnPlus(context, onClickAddHeader);
        botonAdd.setLayoutParams(insiteSVmargin());
        botonAdd.setBackgroundResource(R.drawable.bg_food_background);
        botonAdd.setTextColor(Color.WHITE);
        botonAdd.setOnClickListener(v -> {
            Toast.makeText(context, "Añadir alimento", Toast.LENGTH_SHORT).show();
        });

        alimentosLayout.addView(botonAdd);

        scrollView.addView(alimentosLayout);
        container.addView(scrollView);
        return container;
    }

    // Utilidad para convertir dp a px
    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


    private static TableRow generateLastRow(Context context, ArrayList<HeaderColumns> header){
        TableRow row = new TableRow(context);

        //temp
        View.OnClickListener onClickAddHeader = v -> {
            Toast.makeText(context, "Añadir receta", Toast.LENGTH_SHORT).show();
        };
        //temp
        Button btn = addBtnPlus(context, onClickAddHeader);
        LinearLayout scrollView = generateFoodCell(context, new ArrayList<>());

        scrollView.setLayoutParams(margin());

        btn.setLayoutParams(margin());
        row.addView(btn);
        row.addView(scrollView);

        if (scrollView.getHeight() < btn.getHeight()) {
            scrollView.getLayoutParams().height = btn.getHeight();
            scrollView.requestLayout();
        }else{
            btn.getLayoutParams().height = scrollView.getHeight();
            btn.requestLayout();
        }



        ArrayList<LinearLayout> cells = new ArrayList<>();
        for (HeaderColumns headerC: header) {
            if (!headerC.equals(HeaderColumns.Nombre) && !headerC.equals(HeaderColumns.Alimentos)) {
                try {
                    LinearLayout cell = generateCommonCell(context, "-- --");
                    row.addView(cell);
                    cells.add(cell);
                }catch (IllegalArgumentException e){
                    Toast.makeText(context, "No ha sido posible cargar la siguiente columna: " + headerC, Toast.LENGTH_SHORT).show();
                }
            }
        }

        scrollView.post(() -> {
            int targetHeight = scrollView.getHeight();
            btn.getLayoutParams().height = targetHeight;
            btn.requestLayout();

            for (LinearLayout cell : cells) {
                cell.getLayoutParams().height = targetHeight;
                cell.requestLayout();
            }

            for (int i = 2; i < row.getChildCount(); i++) {
                View v = row.getChildAt(i);
                ViewGroup.LayoutParams p = v.getLayoutParams();
                p.height = targetHeight;
                v.setLayoutParams(p);
            }
        });

        return row;
    }

}
