package com.tfg.dietaalplato.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
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
import androidx.fragment.app.FragmentActivity;

import com.tfg.dietaalplato.utilities.dialogo.DialogInfoFoodPerGr;
import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddFoodToFoodDIet;

import java.util.ArrayList;
import java.util.Formattable;
import java.util.List;

public class TableGenerator {

    private SaveData saveData;
    private ArrayList<HeaderColumns> columns;
    private TableLayout table;
    private Context context;
    private LinearLayout contenedor;
    private View.OnClickListener onClickAddHeader;
    private View.OnClickListener onClickCreateRecepie;
    private FragmentActivity supportFragmentActivity;

    private static TableGenerator instance;

    private TableGenerator (){
        columns = new ArrayList<>();
        columns.add(HeaderColumns.Nombre);
        columns.add(HeaderColumns.Alimentos);
        columns.add(HeaderColumns.Kcal);
        columns.add(HeaderColumns.Proteina);
        columns.add(HeaderColumns.HC);
        columns.add(HeaderColumns.Grasa);
    }

    public static TableGenerator getInstance() {
        if (instance == null) {
            instance = new TableGenerator();
        }
        return instance;
    }

    public void setColumns(ArrayList<HeaderColumns> columns) {
        this.columns = columns;
    }

    public ArrayList<HeaderColumns> getColumns() {
        return columns;
    }

    public void addHeader(HeaderColumns header){
        columns.add(header);
    }

    public void setContenedor(LinearLayout contenedor){
        this.contenedor = contenedor;
    }

    public void setContext(Context context) {
        this.context = context;
        this.table = new TableLayout(context);
    }

    public void setOnClickAddHeader(View.OnClickListener onClickAddHeader){
        this.onClickAddHeader = onClickAddHeader;
    }

    public void setOnClickCreateRecepie(View.OnClickListener onClickCreateRecepie){
        this.onClickCreateRecepie = onClickCreateRecepie;
    }

    public void setSupportFragmentActivity(FragmentActivity supportFragmentActivity) {
        this.supportFragmentActivity = supportFragmentActivity;
    }

    public void generarTabla() throws FBCException {
        saveData = SaveData.getInstance();
        /*
            ++ Att of the table
         */
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
        Button addCol = addBtnPlus(context, onClickAddHeader);
        addCol.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        headerRow.addView(addCol);
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
                        try {
                            FireBaseReader.readAllFoodFromUser(saveData.getUser().getId()).addOnFailureListener(
                                    e -> Toast.makeText(context, "Error al leer los alimentos", Toast.LENGTH_SHORT).show()
                            ).addOnSuccessListener(
                                    foods -> {
                                        if (foodDiets.result != null) {
                                            for (ArrayList<FoodDiet> fd: foodDiets.result.values()) {
                                                //La arraylist es cada receta unificada por el nombre

                                                FoodDiet f = fd.get(0);

                                                Log.d("Prueba", "foodDiet: " + f.getDia() +" : "+ f.getComida());
                                                Log.d("Prueba", "SaveData: " + saveData.getCurrentDay() +" : "+ saveData.getMomentOfDay());
                                                if (f.getDia().trim().equalsIgnoreCase(String.valueOf(saveData.getCurrentDay()).trim()) && f.getComida().trim().equalsIgnoreCase(String.valueOf(saveData.getMomentOfDay()).trim())) {
                                                    //Con este filtro cogemos solo los que pertenecen a este dia y ese momento del dia

                                                    ArrayList<Food> foodArrayList = new ArrayList<>();
                                                    ArrayList<Double> grs = new ArrayList<>();
                                                    for (FoodDiet foodDiet: fd) {
                                                        for (Food food: foods.result.values()) {
                                                            if (!foodArrayList.contains(food)){
                                                                try{
                                                                    if (foodDiet.getIdAlimento().equals(food.getId())) {
                                                                        foodArrayList.add(food);
                                                                        grs.add(safeParse(foodDiet.getG()));
                                                                    }
                                                                }catch (Exception e){
                                                                    Toast.makeText(context, "Error al leer algunos alimentos", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    }
                                                    table.addView(generateRow(context, f.getName(), foodArrayList,grs,foodDiets.result.get(f.getName()), columns));

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
                        } catch (FBCException e) {
                            Toast.makeText(context, "Error al leer los alimentos", Toast.LENGTH_SHORT).show();
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

    private static String addAttFood(List<Food>foods, HeaderColumns att,Context context,ArrayList<Double> grs){
        double i = 0;

       try{
           for (int j = 0; j < foods.size(); j++) {
               Food f = foods.get(j);
               switch (att){
                   case Kcal:
                       i += safeParse(f.getEnergia())*(grs.get(j)/100);
                       break;
                   case Proteina:
                       i+= safeParse(f.getProteina())*(grs.get(j)/100);
                       break;
                   case Grasa:
                       i+= safeParse(f.getGrasa())*(grs.get(j)/100);
                       break;
                   case AGs:
                       i+= safeParse(f.getAgs())*(grs.get(j)/100);
                       break;
                   case AGmi:
                       i+= safeParse(f.getAgmi())*(grs.get(j)/100);
                       break;
                   case AGpi:
                       i+= safeParse(f.getAgpi())*(grs.get(j)/100);
                       break;
                   case Colesterol:
                       i+= safeParse(f.getColesterol())*(grs.get(j)/100);
                       break;
                   case HC:
                       i+= safeParse(f.getHc())*(grs.get(j)/100);
                       break;
                   case Fibra:
                       i+= safeParse(f.getFibra())*(grs.get(j)/100);
                       break;
                   case VitC:
                       i += safeParse(f.getVitC())*(grs.get(j)/100);
                       break;
                   case VitB6:
                       i += safeParse(f.getVitB6())*(grs.get(j)/100);
                       break;
                   case VitE:
                       i += safeParse(f.getVitE())*(grs.get(j)/100);
                       break;
                   case Hierro:
                       i += safeParse(f.getHierro())*(grs.get(j)/100);
                       break;
                   case Sodio:
                       i += safeParse(f.getSodio())*(grs.get(j)/100);
                       break;
                   case Calcio:
                       i += safeParse(f.getCalcio())*(grs.get(j)/100);
                       break;
                   case Potasio:
                       i += safeParse(f.getPotasio())*(grs.get(j)/100);
                       break;
               }
           }
       }catch (Exception e){
           Toast.makeText(context, "Error al leer algunos alimentos", Toast.LENGTH_SHORT).show();
           i += 0.0;
       }

        return String.valueOf(i);
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

    private TableRow generateRow (Context context, String nameRecipe, List<Food> foods,ArrayList<Double> gr,ArrayList<FoodDiet> foodsDiet,ArrayList<HeaderColumns> headers){
        TableRow row = new TableRow(context);
        LinearLayout name = generateCommonCell(context, nameRecipe);
        LinearLayout scrollView = generateFoodCell(context, foods,foodsDiet);

        scrollView.setLayoutParams(margin());

        name.setLayoutParams(margin());
        scrollView.setLayoutParams(margin());


        row.addView(name);
        row.addView(scrollView);

        ArrayList<LinearLayout> cells = new ArrayList<>();
        for (HeaderColumns header: headers) {
            if (!header.equals(HeaderColumns.Nombre) && !header.equals(HeaderColumns.Alimentos)) {
                try {
                    HeaderColumns columna = HeaderColumns.valueOf(header.name());
                    LinearLayout cell = generateCommonCell(context, addAttFood(foods, columna, context,gr ));
                    row.addView(cell);
                    cells.add(cell);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(context, "No ha sido posible cargar la siguiente columna: " + header, Toast.LENGTH_SHORT).show();
                }
            }
        }

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
        tv.setText(text.substring(0,1).toUpperCase()+text.substring(1,text.length()).toLowerCase());
        tv.setTextSize(20);
        tv.setPadding(15, 15, 15, 15);
        Typeface customFont = ResourcesCompat.getFont(context, R.font.lily_script_one);
        if (customFont != null) {
            tv.setTypeface(customFont);
        }
        tv.setTextColor(Color.parseColor("#027C68"));
        cell.addView(tv);

        cell.setLayoutParams(margin());
        return cell;
    }

    private LinearLayout generateFoodCell(Context context, List<Food> food, ArrayList<FoodDiet> foodsDiet) {
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
            fRow.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams insiteSVmargin = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            insiteSVmargin.setMargins(10, 5, 10, 0);

            fRow.setLayoutParams(insiteSVmargin);
            fRow.setBackgroundResource(R.drawable.bg_food_background);
            fRow.setLayoutParams(insiteSVmargin());
            fRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInfoFoodPerGr dialog = DialogInfoFoodPerGr.getInstance();
                    dialog.setFood(f);
                    for (FoodDiet fd: foodsDiet) {
                        if (fd.getIdAlimento().equals(f.getId())){
                            dialog.setFoodDiet(fd);
                        }
                    }
                    Activity activity = (Activity) context;
                    dialog.show(supportFragmentActivity.getSupportFragmentManager(), "DialogScrollView");
                }
            });

            TextView name = new TextView(context);
            name.setText(f.getName().substring(0,1).toUpperCase()+f.getName().substring(1,f.getName().length()).toLowerCase());
            name.setTextColor(Color.WHITE);
            name.setTextSize(18);
            Typeface customFont = ResourcesCompat.getFont(context, R.font.lily_script_one);
            if (customFont != null) {
                name.setTypeface(customFont);
            }
            name.setPadding(10, 5, 10, 0);

            ImageButton info = new ImageButton(context);
            info.setImageResource(android.R.drawable.ic_menu_info_details);
            info.setBackgroundColor(Color.TRANSPARENT);
            info.setOnClickListener(v -> {
                if (food.isEmpty()){
                    info.setOnClickListener(onClickCreateRecepie);
                }
            });

            fRow.addView(name);
            fRow.addView(info);

            alimentosLayout.addView(fRow);
        }

        // Botón "+" para añadir alimentos

        Button botonAdd = addBtnPlus(context, onClickAddHeader);
        botonAdd.setLayoutParams(insiteSVmargin());
        botonAdd.setBackgroundResource(R.drawable.bg_food_background);
        botonAdd.setTextColor(Color.WHITE);
        botonAdd.setOnClickListener(v -> {
            if (foodsDiet.get(0).getName() == null || foodsDiet.get(0).getName().isEmpty()){
                onClickCreateRecepie.onClick(v);
            }else{
                DialogAddFoodToFoodDIet dialog = DialogAddFoodToFoodDIet.getInstance();
                dialog.setFoods((ArrayList<Food>) food);
                dialog.setCurrentsfoodDiets(foodsDiet);
                dialog.show(supportFragmentActivity.getSupportFragmentManager(), "DialogScrollView");
            }
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


    private TableRow generateLastRow(Context context, ArrayList<HeaderColumns> header){
        TableRow row = new TableRow(context);

        Button btn = addBtnPlus(context, onClickCreateRecepie);
        LinearLayout scrollView = generateFoodCell(context, new ArrayList<>(), new ArrayList<>());

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

    public void resetAndGenerateTable() throws FBCException {
        if (contenedor != null) contenedor.removeAllViews();
        if (table != null) table.removeAllViews();
        generarTabla();
    }

}
