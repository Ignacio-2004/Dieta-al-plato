package com.tfg.dietaalplato.object;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Diet extends BaseObject{

    /**
     * El id de esta dieta es el comienzo de todas pero el numero de la dieta (1,3,7)
     */

    private int tip;
    private String idCliente;
    private String just;

    public Diet(String id, String name, int tip, String idCliente, String just) {
        super(id, name);
        this.tip = tip;
        this.idCliente = idCliente;
        this.just = just;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        if (tip == 1 || tip == 3 || tip == 7) {
            this.tip = tip;
        } else {
            throw new IllegalArgumentException("El valor de 'tip' debe ser 1, 3 o 7");
        }
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getJust() {
        return just;
    }

    public void setJust(String just) {
        this.just = just;
    }

    @Override
    public String toString() {
        return "Diet{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                "tip=" + tip +
                ", idCliente='" + idCliente + '\'' +
                ", just='" + just + '\'' +
                '}';
    }

    //++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, String idCliente){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"Name","tip", "idCliente", "just"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(data.get(0).getTag().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());
            keys.add(idCliente);
            keys.add(((EditText) data.get(3)).getText().toString().trim());

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

