package com.tfg.dietaalplato.firebase.tables;

import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

import java.util.HashMap;
import java.util.Map;

public class RelacionRecetaAlimento {

    private String idFooDiet;
    private String idFood;
    private String g;

    public RelacionRecetaAlimento() {
        // Constructor vacío requerido por Firebase
    }

    public RelacionRecetaAlimento(String idFooDiet, String idFood, String gr) {
        this.idFooDiet = idFooDiet;
        this.idFood = idFood;
        this.g = gr;
    }

    // Getters y setters

    public String getIdFooDiet() {
        return idFooDiet;
    }

    public void setIdFooDiet(String idFooDiet) {
        this.idFooDiet = idFooDiet;
    }

    public String getIdFood() {
        return idFood;
    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public static ValidationResult toMap(String gr, String idFood, String idFooDiet) {
         String[] keys = {"gr", "idFood", "idFooDiet"};
         Map<String, String> values = new HashMap<>();

         ValidationResult result = new ValidationResult(true, "Datos válidos",values);

         if (gr == null || gr.isEmpty()) {
             gr =" 0";
         }
         values.put("gr", gr);

         if (idFood == null || idFood.isEmpty()) {
             result.exit = false;
             result.message = "El campo idFood no puede estar vacío";
             return result;
         }

         values.put("idFood", idFood);

         if (idFooDiet == null || idFooDiet.isEmpty()) {
             result.exit = false;
             result.message = "El campo idFooDiet no puede estar vacío";
             return result;
         }

         values.put("idFooDiet", idFooDiet);

         result.data = values;

         return result;
    }

    public static Map<String, String> toDesMapObject(RelacionRecetaAlimento relacion) {

        HashMap<String,String> data = new HashMap<>();
        data.put("idFooDiet", relacion.getIdFooDiet());
        data.put("idFood", relacion.getIdFood());
        data.put("gr", relacion.getG());

        return data;
    }
}
