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

import com.tfg.dietaalplato.R;

import java.util.List;

public final class DietaTableGenerator {

    // Evitar instanciación
    private DietaTableGenerator() {
        throw new UnsupportedOperationException("Clase estática, no instanciable.");
    }

    public static void generarTabla(Context context, LinearLayout contenedor, List<List<String>> datos) {
        contenedor.removeAllViews();

        TableLayout table = new TableLayout(context);
        table.setStretchAllColumns(true);
        table.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // 🟣 Cabecera
        TableRow headerRow = new TableRow(context);
        String[] columnas = {"Receta", "Alimentos", "Kcal", "Proteínas", "HC", "Grasas"};

        for (String titulo : columnas) {
            TextView th = new TextView(context);
            th.setText(titulo);
            th.setTextColor(Color.parseColor("027C68"));
            th.setTextSize(30);
            th.setTypeface(null, Typeface.BOLD);
            th.setPadding(20, 20, 20, 20);
            th.setGravity(Gravity.CENTER);
            th.setBackgroundResource(R.drawable.bg_food_item);
            headerRow.addView(th);
        }

        // Botón añadir columna ➕
        Button addCol = new Button(context);
        addCol.setText("+");
        addCol.setTextSize(30);
        addCol.setTextColor(Color.parseColor("027C68"));
        addCol.setBackgroundResource(R.drawable.bg_food_item); // crea este drawable
        headerRow.addView(addCol);

        table.addView(headerRow);

        // 🔁 Filas de datos
        for (List<String> fila : datos) {
            TableRow row = new TableRow(context);

            for (int i = 0; i < fila.size(); i++) {
                LinearLayout celda = new LinearLayout(context);
                celda.setOrientation(LinearLayout.HORIZONTAL);
                celda.setGravity(Gravity.CENTER_VERTICAL);

                TextView tv = new TextView(context);
                tv.setText(fila.get(i));
                tv.setPadding(15, 15, 15, 15);
                tv.setTextColor(Color.BLACK);
                celda.addView(tv);

                // Añadir botón solo en columna "Alimentos"
                if (i == 1) {
                    // Creamos un ScrollView para mostrar alimentos
                    ScrollView scrollView = new ScrollView(context);
                    scrollView.setLayoutParams(new ViewGroup.LayoutParams(400, 400)); // tamaño limitado
                    scrollView.setFillViewport(true);

                    LinearLayout alimentosLayout = new LinearLayout(context);
                    alimentosLayout.setOrientation(LinearLayout.VERTICAL);
                    scrollView.addView(alimentosLayout);

                    // Simulamos alimentos separados por coma
                    String[] alimentos = fila.get(i).split(",");

                    for (String alimento : alimentos) {
                        LinearLayout filaAlimento = new LinearLayout(context);
                        filaAlimento.setOrientation(LinearLayout.HORIZONTAL);
                        filaAlimento.setGravity(Gravity.CENTER_VERTICAL);

                        TextView nombre = new TextView(context);
                        nombre.setText(alimento.trim());
                        nombre.setTextSize(14);
                        nombre.setPadding(10, 10, 10, 10);

                        ImageButton info = new ImageButton(context);
                        info.setImageResource(android.R.drawable.ic_menu_info_details);
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
}
