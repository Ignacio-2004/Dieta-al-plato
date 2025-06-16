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