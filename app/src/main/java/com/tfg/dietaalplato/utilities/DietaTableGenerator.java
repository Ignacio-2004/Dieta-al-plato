package com.tfg.dietaalplato.utilities;

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

import java.util.List;

public final class DietaTableGenerator {

    // Evitar instanciación
    private DietaTableGenerator() {
        throw new UnsupportedOperationException("Clase estática, no instanciable.");
    }

    public static void generarTabla(Context context, LinearLayout contenedor, List<List<String>> recetas) {
        contenedor.removeAllViews();

        //Tabla
        TableLayout table = new TableLayout(context);
        table.setStretchAllColumns(true);
        table.setBackgroundColor(Color.TRANSPARENT); //hacemos trqansparente la tabla
        table.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 🟣 Cabecera
        TableRow headerRow = new TableRow(context);
        String[] columnas = {"Receta", "Alimentos", "Kcal", "Proteínas", "HC", "Grasas"};

        for (String titulo : columnas) {
            TextView th = new TextView(context);
            th.setText(titulo);
            th.setTextColor(Color.parseColor("#027C68"));
            th.setTextSize(30);
            th.setTypeface(null, Typeface.BOLD);
            th.setPadding(20, 20, 20, 20);
            th.setGravity(Gravity.CENTER);

            Typeface customFont = ResourcesCompat.getFont(context, R.font.lily_script_one);
            if (customFont != null) {
                th.setTypeface(customFont);
            }

            th.setBackgroundResource(R.drawable.bg_food_item);

            // 👉 Crear y aplicar márgenes
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 10, 10); // margen izquierdo y derecho de 10dp
            th.setLayoutParams(params);

            headerRow.addView(th);
        }


        // Botón añadir columna ➕
        Button addCol = new Button(context);
        addCol.setText("+");
        addCol.setTextSize(30);
        addCol.setTextColor(Color.parseColor("#027C68"));
        addCol.setBackgroundResource(R.drawable.bg_food_item); // crea este drawable
        headerRow.addView(addCol);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 10, 10); // margen izquierdo y derecho de 10dp
        headerRow.setLayoutParams(params);

        table.addView(headerRow);

        // 🔁 Filas de datos
        for (List<String> data : recetas) {
            TableRow row = new TableRow(context);

            for (int i = 0; i < data.size(); i++) {
                LinearLayout celda = new LinearLayout(context);
                celda.setOrientation(LinearLayout.HORIZONTAL);
                celda.setGravity(Gravity.CENTER);
                celda.setVerticalGravity(Gravity.CENTER);
                celda.setBackgroundResource(R.drawable.bg_food_item);

                if (i != 1) { // 👈 Solo añadir TextView simple si no es la columna de alimentos
                    TextView tv = new TextView(context);
                    tv.setText(data.get(i));
                    tv.setPadding(15, 15, 15, 15);
                    tv.setTextColor(Color.BLACK);
                    celda.addView(tv);
                }



                // Añadir botón solo en columna "Alimentos"
                if (i == 1) {
                    // Creamos un ScrollView para mostrar alimentos
                    ScrollView scrollView = new ScrollView(context);
                    LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    scrollParams.height = dpToPx(context, 200); // Altura máxima en dp
                    scrollView.setLayoutParams(scrollParams);
                    scrollView.setBackgroundColor(Color.TRANSPARENT);
                    scrollView.setFillViewport(true);


                    LinearLayout alimentosLayout = new LinearLayout(context);
                    alimentosLayout.setOrientation(LinearLayout.VERTICAL);
                    scrollView.addView(alimentosLayout);

                    // Simulamos alimentos separados por coma
                    String[] alimentos = data.get(i).split(",");

                    for (String alimento : alimentos) {
                        LinearLayout filaAlimento = new LinearLayout(context);
                        filaAlimento.setOrientation(LinearLayout.HORIZONTAL);
                        filaAlimento.setGravity(Gravity.CENTER_VERTICAL);
                        filaAlimento.setBackgroundResource(R.drawable.bg_food_background);

                        TextView nombre = new TextView(context);
                        nombre.setText(alimento.trim());
                        nombre.setTextSize(14);
                        nombre.setPadding(10, 10, 10, 10);
                        nombre.setOnClickListener(v -> {
                                    Toast.makeText(context, "Información de " + alimento.trim(), Toast.LENGTH_SHORT).show();
                                }
                        );

                        ImageButton info = new ImageButton(context);
                        info.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                        info.setBackgroundColor(Color.TRANSPARENT);
                        info.setOnClickListener(v -> {
                            Toast.makeText(context, "Información de " + alimento.trim(), Toast.LENGTH_SHORT).show();
                            // Aquí puedes abrir un diálogo o ir a una nueva actividad
                        });

                        filaAlimento.addView(nombre);
                        filaAlimento.addView(info);
                        alimentosLayout.addView(filaAlimento);
                    }

                    celda.addView(scrollView);
                }

                row.addView(celda);
            }

            table.addView(row);
        }

        contenedor.addView(table);
    }


    private static TextView crearCeldaTexto(Context context, String texto, boolean esCabecera) {
        TextView tv = new TextView(context);
        tv.setText(texto);
        tv.setPadding(16, 8, 16, 8);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(esCabecera ? Color.parseColor("#FFBB86FC") : Color.WHITE);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        return tv;
    }

    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}
