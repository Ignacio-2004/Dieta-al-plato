package com.tfg.dietaalplato.utilities;

import com.tfg.dietaalplato.firebase.tables.Food;

import java.util.HashMap;
import java.util.Map;

public class DailyNutrition {
    private double totalProteinas;
    private double totalCarbohidratos;
    private double totalGrasas;
    private double totalCalorias;

    public DailyNutrition() {
        // Inicializar todos los valores en 0
        totalProteinas = 0;
        totalCarbohidratos = 0;
        totalGrasas = 0;
        totalCalorias = 0;
    }

    public void addFood(Food food, double gramos) {
        if (food == null) return;

        double factor = gramos / 100.0; // Conversión a porcentaje

        // Macronutrientes (ajustados por gramos)
        double proteinas = parseDoubleSafe(food.getProteina()) * factor;
        double carbohidratos = parseDoubleSafe(food.getHc()) * factor;
        double grasas = parseDoubleSafe(food.getGrasa()) * factor;

        totalProteinas += proteinas;
        totalCarbohidratos += carbohidratos;
        totalGrasas += grasas;

        // Calorías: Usamos valor directo si existe, si no calculamos
        double energia = parseDoubleSafe(food.getEnergia());
        totalCalorias += (energia > 0) ? (energia * factor) :
                ((proteinas * 4) + (carbohidratos * 4) + (grasas * 9));
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Getters
    public double getTotalProteinas() { return totalProteinas; }
    public double getTotalCarbohidratos() { return totalCarbohidratos; }
    public double getTotalGrasas() { return totalGrasas; }
    public double getTotalCalorias() { return totalCalorias; }
}