package com.tfg.dietaalplato.object;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodDiet {
    private String id;
    private String idDieta;
    private String idAlimento;
    private String comida; // 1: Desayuno, 2: Almuerzo, 3: Comida, 4: Merienda, 5: Cena, 6: ReCena
    private String numeroPlato;
    private String dia;
    private String g;
    private String nombreReceta;

    // Constructor vacío requerido para Firestore
    public FoodDiet() {
    }

    // Constructor con parámetros

    public FoodDiet(String id, String idDieta, String idAlimento, String comida, String numeroPlato, String dia, String g, String nombreReceta) {
        this.id = id;
        this.idDieta = idDieta;
        this.idAlimento = idAlimento;
        this.comida = comida;
        this.numeroPlato = numeroPlato;
        this.dia = dia;
        this.g = g;
        this.nombreReceta = nombreReceta;
    }


    // Getters y Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getComida() {
        return comida;
    }

    public void setComida(String comida) {
        this.comida = comida;
    }

    public String getNumeroPlato() {
        return numeroPlato;
    }

    public void setNumeroPlato(String numeroPlato) {
        this.numeroPlato = numeroPlato;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
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
                ", g=" + g +
                '}';
    }

//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, String idDieta,String idAlimento){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"idDieta","idAlimento", "comida", "numeroPlato", "dia", "gramos", "nombreReceta"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(idDieta);
            keys.add(idAlimento);
            keys.add(((EditText) data.get(0)).getText().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());
            keys.add(((EditText) data.get(2)).getText().toString().trim());
            keys.add(((EditText) data.get(3)).getText().toString().trim());
            keys.add(((EditText) data.get(4)).getText().toString().trim());

            for (int i = 0; i < keys.size()-2; i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i+2).isEmpty()) {
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

