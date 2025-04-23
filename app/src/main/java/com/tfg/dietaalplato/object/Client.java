package com.tfg.dietaalplato.object;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    private String id;
    private String cli;
    private String ape;
    private String idUsr;

    public Client(String id, String cli, String ape, String idUsr) {
        this.id = id;
        this.cli = cli;
        this.ape = ape;
        this.idUsr = idUsr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCli() {
        return cli;
    }

    public void setCli(String cli) {
        this.cli = cli;
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
                "id='" + id + '\'' +
                ", cli='" + cli + '\'' +
                ", ape='" + ape + '\'' +
                ", idUsr='" + idUsr + '\'' +
                '}';
    }

//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, String idUsr){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"cli", "ape", "idUsr"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(((EditText) data.get(0)).getText().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());

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

            result.data.put("idUsr", idUsr);
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