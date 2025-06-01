package com.tfg.dietaalplato.firebase.tables;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseValidator;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Diet extends BaseObject {

    /**
     * El id de esta dieta es el comienzo de todas pero el numero de la dieta (1,3,7)
     */

    private String tip;
    private String idCli;
    private String just;

    public Diet() {
    }

    public Diet(String id, String name, String tip, String idCliente, String just) {
        super(id, name);
        this.tip = tip; //Los dias
        this.idCli = idCliente;
        this.just = just;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getIdCli() {
        return idCli;
    }

    public void setIdCli(String idCli) {
        this.idCli = idCli;
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
                ", idCli='" + idCli + '\'' +
                ", just='" + just + '\'' +
                '}';
    }

    //++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<String> data, String idCliente){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"name","tip", "idCli", "just"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            if (!data.get(0).trim().equals("1")){
                keys.add("Dieta de "+data.get(0).trim()+" dias.");
            }else{
                keys.add("Dieta de "+data.get(0).trim()+" dia.");
            }
            keys.add(data.get(0).trim().toLowerCase());
            keys.add(idCliente.toUpperCase());
            keys.add(data.get(1).trim());

            for (int i = 0; i < keys.size(); i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i).isEmpty()) {
                    throw new Exception("El campo " + data.get(i).toString() + " no puede estar vacio");
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

    public static Map<String,String> toDesMapObject(Diet diet){
        HashMap<String,String> data = new HashMap<>();
        String[] fieldName = {"id","name","tip", "idCli", "just"};

        data.put(fieldName[0], diet.getId());
        data.put(fieldName[1], diet.getName());
        data.put(fieldName[2], diet.getTip());
        data.put(fieldName[3], diet.getIdCli());
        data.put(fieldName[4], diet.getJust());


        return data;
    }

    public boolean exist() throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);

        FireBaseValidator.exist(getName(), getIdCli(),String.valueOf(TablesNames.dietas), result -> {
            if (result){
                booleanAtomic.set(true);
            }
        });

        return booleanAtomic.get();

    }

//--IP - 23/04/2025 -

}

