package com.tfg.dietaalplato.object;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

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

//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, String idDieta){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"idAlimento", "comida", "numeroPlato", "dia", "nombreReceta"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(idDieta);
            keys.add(((EditText) data.get(0)).getText().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());
            keys.add(((EditText) data.get(2)).getText().toString().trim());
            keys.add(((EditText) data.get(3)).getText().toString().trim());
            keys.add(((EditText) data.get(4)).getText().toString().trim());

            for (int i = 0; i < keys.size(); i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i).isEmpty()) {
                    throw new Exception("El campo " + data.get(i).getTag().toString() + " no puede estar vacio");
                    //Con tag devuelvo el nombre del campo vacio
                }
            }

            for (int i = 0; i < keys.size(); i++) {
                result.data.put(fieldName[i], keys.get(i));
            }

            result.exit = true;
            result.message = "Datos validos";

        }catch (Exception e){

            result.exit = false;
            result.message = "Error : " + e.getMessage();

        }

        return result;
    }

//--IP - 23/04/2025 -
}

