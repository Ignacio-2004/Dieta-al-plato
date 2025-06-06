package com.tfg.dietaalplato.utilities.tipe_collection;

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
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.utilities.HeaderColumns;
import com.tfg.dietaalplato.utilities.SaveData;

import java.util.List;

public class TableGenerator {

    SaveData saveData;

    private TableGenerator() {
        throw new UnsupportedOperationException("Clase estática, no instanciable.");
    }

    public void generarTabla(Context context, LinearLayout contenedor, String[] columns){
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
        for (String header: columns) {
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


        /*
            -- Body of the table
         */

        /*
        Add table to the container
         */
        contenedor.addView(table);
    }

    private static TextView generateHeader(Context context, String header){
        TextView th = new TextView(context);
        th.setText(header);
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

    private static TableRow generateRow (Context context, String nameRecipe, List<Food> foods,String[] headers){
        TableRow row = new TableRow(context);

        row.addView(generateCommonCell(context, nameRecipe));
        row.addView(generateFoodCell(context,foods));

        for (String header: headers) {
            row.addView(generateCommonCell(context, addAttFood(foods, HeaderColumns.valueOf(header),context)));
        }

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
            scrollView.addView(fRow);
            scrollView.addView(addBtnPlus(context));
        }

        return scrollView;
    }

}
