package com.tfg.dietaalplato.object;

public class FoodDiet {
    private String idDieta;
    private String idAlimento;
    private int comida; // 1: Desayuno, 2: Almuerzo, 3: Comida, 4: Merienda, 5: Cena, 6: ReCena
    private int numeroPlato;
    private int dia;
    private String nombreReceta;

    // Constructor vacío requerido para Firestore
    public FoodDiet() {
    }

    // Constructor con parámetros
    public FoodDiet(String idDieta, String idAlimento, int comida, int numeroPlato, int dia, String nombreReceta) {
        this.idDieta = idDieta;
        this.idAlimento = idAlimento;
        setComida(comida); // Usamos el setter para validar
        this.numeroPlato = numeroPlato;
        this.dia = dia;
        this.nombreReceta = nombreReceta;
    }

    // Getters y Setters
    public String getIdDieta() {
        return idDieta;
    }

    public void setIdDieta(String idDieta) {
        this.idDieta = idDieta;
    }

    public String getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(String idAlimento) {
        this.idAlimento = idAlimento;
    }

    public int getComida() {
        return comida;
    }

    public void setComida(int comida) {
        if (comida < 1 || comida > 6) {
            throw new IllegalArgumentException("El valor de comida debe estar entre 1 y 6.");
        }
        this.comida = comida;
    }

    public int getNumeroPlato() {
        return numeroPlato;
    }

    public void setNumeroPlato(int numeroPlato) {
        this.numeroPlato = numeroPlato;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public String getNombreReceta() {
        return nombreReceta;
    }

    public void setNombreReceta(String nombreReceta) {
        this.nombreReceta = nombreReceta;
    }

    @Override
    public String toString() {
        return "DietFood{" +
                "idDieta='" + idDieta + '\'' +
                ", idAlimento='" + idAlimento + '\'' +
                ", comida=" + comida +
                ", numeroPlato=" + numeroPlato +
                ", dia=" + dia +
                ", nombreReceta='" + nombreReceta + '\'' +
                '}';
    }
}

