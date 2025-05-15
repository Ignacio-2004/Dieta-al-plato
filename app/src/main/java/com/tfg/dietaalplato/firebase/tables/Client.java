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

public class Client extends BaseObject {

    private String ape;
    private String idUsr;
    private ArrayList<String> alergias;
    private ArrayList<String> patologias;

    public Client() {
        alergias = new ArrayList<>();
        patologias = new ArrayList<>();
    }

    public Client(String id, String name, String ape, String idUsr, ArrayList<String> alergias, ArrayList<String> patologias) {
        super(id, name);
        this.ape = ape;
        this.idUsr = idUsr;
        this.alergias = alergias;
        this.patologias = patologias;
    }

    public String getApe() {
        return ape;
    }

    public void setApe(String ape) {
        this.ape = ape;
    }

    public String getIdUsr() {
        return idUsr;
    }

    public void setIdUsr(String idUsr) {
        this.idUsr = idUsr;
    }

    public ArrayList<String> getAlergias() {
        return alergias;
    }

    public void setAlergias(ArrayList<String> alergias) {
        this.alergias = alergias;
    }

    public ArrayList<String> getPatologias() {
        return patologias;
    }

    public void setPatologias(ArrayList<String> patologias) {
        this.patologias = patologias;
    }

    @Override
    public String toString() {
        return "Client{" +
                "ape='" + ape + '\'' +
                ", idUsr='" + idUsr + '\'' +
                ", alergias=" + alergias +
                ", patologias=" + patologias +
                '}';
    }

    //++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data,ArrayList<String> alergias, ArrayList<String> patologias, String idUsr){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"name", "ape","alergias", "patologias", "idUsr"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(((EditText) data.get(0)).getText().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());
            keys.add(idUsr);

            for (int i = 0; i < keys.size()-1; i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i).isEmpty()) {
                    throw new Exception("El campo " + data.get(i).getTag().toString() + " no puede estar vacio");
                    //Con tag devuelvo el nombre del campo vacio
                }
            }

            for (int i = 0; i < keys.size(); i++) {
                result.data.put(fieldName[i], keys.get(i).toLowerCase());
            }
            result.data.put(fieldName[0], keys.get(0).toLowerCase());
            result.data.put(fieldName[1], keys.get(1).toLowerCase());

            String compoundCharacteristic = "";

            for (String allergy : alergias) {
                compoundCharacteristic = compoundCharacteristic + allergy + ",";
            }
            result.data.put(fieldName[2], compoundCharacteristic);

            compoundCharacteristic = "";

            for (String pathology : patologias) {
                compoundCharacteristic = compoundCharacteristic + pathology + ",";
            }
            result.data.put(fieldName[3], compoundCharacteristic);
            result.data.put(fieldName[4], idUsr);

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

        FireBaseValidator.exist(getName(),getIdUsr(),String.valueOf(TablesNames.clientes), result -> {
            if (result){
                booleanAtomic.set(true);
            }
        });

        return booleanAtomic.get();

    }

//--IP - 23/04/2025 -
}