package com.tfg.dietaalplato.firebase.tables;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseValidator;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FoodDiet extends BaseObject {

    private String idUsr;
    private String idDieta;
    private String idAlimento;
    private String tipoComida; // 1: Desayuno, 2: Almuerzo, 3: Comida, 4: Merienda, 5: Cena, 6: ReCena
    private String numeroPlato;
    private String dia;
    private String g;

    // Constructor vacío requerido para Firestore
    public FoodDiet() {
    }

    // Constructor con parámetros

    public FoodDiet(String id, String idDieta, String idAlimento, String comida, String numeroPlato, String dia, String g, String name) {

        super(id,name);
        this.idDieta = idDieta;
        this.idAlimento = idAlimento;
        this.tipoComida = comida;
        this.numeroPlato = numeroPlato;
        this.dia = dia;
        this.g = g;

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

    public String getComida() {
        return tipoComida;
    }

    public void setComida(String comida) {
        this.tipoComida = comida;
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

    @Override
    public String toString() {
        return "DietFood{" +
                "id='" + getId() + '\'' +
                "idDieta='" + idDieta + '\'' +
                ", idAlimento='" + idAlimento + '\'' +
                ", comida=" + tipoComida +
                ", numeroPlato=" + numeroPlato +
                ", dia=" + dia +
                ", nombreReceta='" + getName() + '\'' +
                ", g=" + g +
                '}';
    }

//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<String> data, String idDieta,String idAlimento,String id){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"id","idDieta","idAlimento", "comida", "numeroPlato", "dia", "gramos", "nombreReceta"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(id);
            keys.add(idDieta);
            keys.add(idAlimento);
            keys.add(data.get(0).trim());
            keys.add(data.get(1).trim());
            keys.add(data.get(2).trim());
            keys.add(data.get(3).trim());
            keys.add(data.get(4).trim());

            for (int i = 1; i < keys.size()-2; i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i+2).isEmpty()) {
                    throw new Exception("El campo " + data.get(i) + " no puede estar vacio");
                    //Con tag devuelvo el nombre del campo vacio
                }
            }

            for (int i = 0; i < keys.size(); i++) {
                if (fieldName[i].contains("id")){
                    result.data.put(fieldName[i], keys.get(i).toUpperCase());
                }else{
                    result.data.put(fieldName[i], keys.get(i).toLowerCase());
                }
            }

            result.exit = true;
            result.message = "Datos validos";

        }catch (Exception e){

            result.exit = false;
            result.message = "Error : " + e.getMessage();

        }

        return result;
    }

    public static Map<String,String> toDesMapObject(FoodDiet foodDiet) {

        HashMap<String,String> data = new HashMap<>();
        data.put("idDieta", foodDiet.getIdDieta());
        data.put("idAlimento", foodDiet.getIdAlimento());
        data.put("comida", foodDiet.getComida());
        data.put("numeroPlato", foodDiet.getNumeroPlato());
        data.put("dia", foodDiet.getDia());
        data.put("gramos", foodDiet.getG());
        data.put("id", foodDiet.getId());
        data.put("nombreReceta", foodDiet.getName());

        return data;
    }

    public boolean exist() throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);


        FireBaseValidator.exist(getName(),getIdDieta(),getIdAlimento(), String.valueOf(TablesNames.comidaDietas), result -> {
            if (result){
                booleanAtomic.set(true);
            }
        });

        return booleanAtomic.get();

    }

    public String getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(String idUsr) {
        this.idUsr = idUsr;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", getId());
        map.put("idDieta", idDieta);
        map.put("idAlimento", idAlimento);
        map.put("idUsr", idUsr);
        map.put("dia", dia);
        map.put("tipoComida", tipoComida);
        return map;
    }

//--IP - 23/04/2025 -
}

