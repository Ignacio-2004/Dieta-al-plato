package com.tfg.dietaalplato.firebase.tables;


public class Macros {
    private double proteinas;
    private double carbohidratos;
    private double grasas;
    private double calorias;


    public Macros() {
        reset();
    }


    public void reset() {
        this.proteinas = 0;
        this.carbohidratos = 0;
        this.grasas = 0;
        this.calorias = 0;
    }


    public void sumarAlimento(Food food, double gramos) {
        if (food == null) return;


        try {
            double factor = gramos / 100.0;
            this.proteinas += safeParseDouble(food.getProteina()) * factor;
            this.carbohidratos += safeParseDouble(food.getHc()) * factor;
            this.grasas += safeParseDouble(food.getGrasa()) * factor;
            this.calorias += safeParseDouble(food.getEnergia()) * factor;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private double safeParseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


    public String getResumen() {
        return String.format("Totales del día:\n" +
                        "Proteínas: %.1f g\n" +
                        "Carbohidratos: %.1f g\n" +
                        "Grasas: %.1f g\n" +
                        "Calorías: %.1f kcal",
                proteinas, carbohidratos, grasas, calorias);
    }


    // Getters
    public double getProteinas() { return proteinas; }
    public double getCarbohidratos() { return carbohidratos; }
    public double getGrasas() { return grasas; }
    public double getCalorias() { return calorias; }
}
