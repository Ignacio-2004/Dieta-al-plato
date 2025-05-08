package com.tfg.dietaalplato.firebase.tables;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Client extends BaseObject {

    private String ape;
    private String idUsr;

    public Client() {
    }

    public Client(String id, String name, String ape, String idUsr) {
        super(id, name);
        this.ape = ape;
        this.idUsr = idUsr;
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

    public String toString() {
        return "Client{" +
                "id='" + super.getId() + '\'' +
                ", cli='" + super.getName() + '\'' +
                ", ape='" + ape + '\'' +
                ", idUsr='" + idUsr + '\'' +
                '}';
    }

//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, String idUsr){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"Name", "ape", "idUsr"};

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