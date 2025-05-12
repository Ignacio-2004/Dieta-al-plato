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
import java.util.concurrent.atomic.AtomicBoolean;

public class Diet extends BaseObject {

    /**
     * El id de esta dieta es el comienzo de todas pero el numero de la dieta (1,3,7)
     */

    private String tip;
    private String idCliente;
    private String just;

    public Diet() {
    }

    public Diet(String id, String name, String tip, String idCliente, String just) {
        super(id, name);
        this.tip = tip;
        this.idCliente = idCliente;
        this.just = just;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
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

        String[] fieldName = {"name","tip", "idCliente", "just"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            if (data.get(0).getTag().toString().trim().equals("1")){
                keys.add("Dieta de "+data.get(0).getTag().toString().trim()+" dias.");
            }else{
                keys.add("Dieta de "+data.get(0).getTag().toString().trim()+" dia.");
            }
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
                result.data.put(fieldName[i], keys.get(i).toLowerCase());
            }

            result.exit = true;
            result.message = "Datos validos";

        }catch (Exception e){

            result.exit = false;
            result.message = "Error : " + e.getMessage();

        }

        return result;
    }

    public boolean exist() throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);

        FireBaseValidator.exist(getName(),getIdCliente(),String.valueOf(TablesNames.dietas), result -> {
            if (result){
                booleanAtomic.set(true);
            }
        });

        return booleanAtomic.get();

    }

//--IP - 23/04/2025 -

}

