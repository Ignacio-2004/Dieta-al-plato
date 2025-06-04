package com.tfg.dietaalplato.firebase.tables;

public class Macros {
    private double proteinas;
    private double carbohidratos;
    private double grasas;
    private double calorias;

    // Constructor, getters y setters
    public Macros() {
        this.proteinas = 0;
        this.carbohidratos = 0;
        this.grasas = 0;
        this.calorias = 0;
    }

    // Métodos para sumar valores
    public void sumarAlimento(Food food, double gramos) {
        double factor = gramos / 100.0; // Asumiendo nutrientes por 100g
        this.proteinas += Double.parseDouble(food.getProteina()) * factor;
        this.carbohidratos += Double.parseDouble(food.getHc()) * factor;
        this.grasas += Double.parseDouble(food.getGrasa()) * factor;
        this.calorias += Double.parseDouble(food.getEnergia()) * factor;
    }

    public String getResumen() {
        return String.format("Totales del día:\n" +
                        "Proteínas: %.1f g\n" +
                        "Carbohidratos: %.1f g\n" +
                        "Grasas: %.1f g\n" +
                        "Calorías: %.1f kcal",
                proteinas, carbohidratos, grasas, calorias);
    }
}

