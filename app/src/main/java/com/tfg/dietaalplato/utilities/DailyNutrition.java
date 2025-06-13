package com.tfg.dietaalplato.utilities;

import com.tfg.dietaalplato.firebase.tables.Food;

import java.util.HashMap;
import java.util.Map;

public class DailyNutrition {
    private double totalProteinas;
    private double totalCarbohidratos;
    private double totalGrasas;
    private double totalCalorias;
    private double totalAGS;
    private double totalAGMI;
    private double totalAGPI;
    private double totalColesterol;
    private double totalFibra;
    private double totalVitC;
    private double totalVitB6;
    private double totalVitE;
    private double totalHierro;
    private double totalSodio;
    private double totalCalcio;
    private double totalPotasio;

    // Mapas para almacenar los valores por tipo de comida
    private Map<String, Double> proteinasPorComida = new HashMap<>();
    private Map<String, Double> carbohidratosPorComida = new HashMap<>();
    private Map<String, Double> grasasPorComida = new HashMap<>();
    private Map<String, Double> caloriasPorComida = new HashMap<>();
    // Puedes añadir más mapas para otros nutrientes si los necesitas

    public DailyNutrition() {
        // Inicializar todos los valores en 0
        totalProteinas = 0;
        totalCarbohidratos = 0;
        totalGrasas = 0;
        totalCalorias = 0;
        totalAGS = 0;
        totalAGMI = 0;
        totalAGPI = 0;
        totalColesterol = 0;
        totalFibra = 0;
        totalVitC = 0;
        totalVitB6 = 0;
        totalVitE = 0;
        totalHierro = 0;
        totalSodio = 0;
        totalCalcio = 0;
        totalPotasio = 0;
    }

    public void addFood(Food food) {
        // Convertimos los valores String a double
        double proteinas = parseDoubleSafe(food.getProteina());
        double carbohidratos = parseDoubleSafe(food.getHc());
        double grasas = parseDoubleSafe(food.getGrasa());
        double ags = parseDoubleSafe(food.getAgs());
        double agmi = parseDoubleSafe(food.getAgmi());
        double agpi = parseDoubleSafe(food.getAgpi());
        double colesterol = parseDoubleSafe(food.getColesterol());
        double fibra = parseDoubleSafe(food.getFibra());
        double vitC = parseDoubleSafe(food.getVitC());
        double vitB6 = parseDoubleSafe(food.getVitB6());
        double vitE = parseDoubleSafe(food.getVitE());
        double hierro = parseDoubleSafe(food.getHierro());
        double sodio = parseDoubleSafe(food.getSodio());
        double calcio = parseDoubleSafe(food.getCalcio());
        double potasio = parseDoubleSafe(food.getPotasio());

        // Sumamos los valores al total del día
        totalProteinas += proteinas;
        totalCarbohidratos += carbohidratos;
        totalGrasas += grasas;
        totalCalorias = (proteinas * 4) + (carbohidratos * 4) + (grasas * 9);
        totalAGS += ags;
        totalAGMI += agmi;
        totalAGPI += agpi;
        totalColesterol += colesterol;
        totalFibra += fibra;
        totalVitC += vitC;
        totalVitB6 += vitB6;
        totalVitE += vitE;
        totalHierro += hierro;
        totalSodio += sodio;
        totalCalcio += calcio;
        totalPotasio += potasio;

    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0; // Si hay error en la conversión, devolvemos 0
        }
    }

    // Getters
    public double getTotalProteinas() { return totalProteinas; }
    public double getTotalCarbohidratos() { return totalCarbohidratos; }
    public double getTotalGrasas() { return totalGrasas; }
    public double getTotalCalorias() { return totalCalorias; }
    public double getTotalAGS() { return totalAGS; }
    public double getTotalAGMI() { return totalAGMI; }
    public double getTotalAGPI() { return totalAGPI; }
    public double getTotalColesterol() { return totalColesterol; }
    public double getTotalFibra() { return totalFibra; }
    public double getTotalVitC() { return totalVitC; }
    public double getTotalVitB6() { return totalVitB6; }
    public double getTotalVitE() { return totalVitE; }
    public double getTotalHierro() { return totalHierro; }
    public double getTotalSodio() { return totalSodio; }
    public double getTotalCalcio() { return totalCalcio; }
    public double getTotalPotasio() { return totalPotasio; }

    public Map<String, Double> getProteinasPorComida() { return proteinasPorComida; }
    public Map<String, Double> getCarbohidratosPorComida() { return carbohidratosPorComida; }
    public Map<String, Double> getGrasasPorComida() { return grasasPorComida; }
    public Map<String, Double> getCaloriasPorComida() { return caloriasPorComida; }
}