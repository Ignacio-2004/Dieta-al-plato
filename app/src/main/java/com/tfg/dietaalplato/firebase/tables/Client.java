package com.tfg.dietaalplato.firebase.tables;

import android.util.Log;
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

public class Client extends BaseObject {

    private String ape;
    private String idUsr;
    private String minKal;
    private String maxKal;
    private ArrayList<String> alergias;
    private ArrayList<String> patologias;

    public Client() {
        alergias = new ArrayList<>();
        patologias = new ArrayList<>();
    }

    public Client(String id, String name, String ape, String idUsr, String minKcal, String maxKcal, ArrayList<String> alergias, ArrayList<String> patologias) {
        super(id, name);
        this.ape = ape;
        this.idUsr = idUsr;
        this.minKal = minKcal;
        this.maxKal = maxKcal;
        this.alergias = alergias;
        this.patologias = patologias;
    }

    public String getMinKal() {
        return minKal;
    }

    public void setMinKal(String minKal) {
        this.minKal = minKal;
    }

    public String getMaxKal() {
        return maxKal;
    }

    public void setMaxKal(String maxKal) {
        this.maxKal = maxKal;
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
                ", minKcal='" + minKal + '\'' +
                ", maxKcal='" + maxKal + '\'' +
                ", alergias=" + alergias +
                ", patologias=" + patologias +
                '}';
    }

    //++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data, ArrayList<String> alergias, ArrayList<String> patologias, String idUsr) {
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"name", "ape", "minKcal", "maxKcal", "idUsr", "alergias", "patologias"};
        String[] userFriendlyNames = {
                "el nombre del cliente",
                "el apellido del cliente",
                "las calorías mínimas",
                "las calorías máximas"
        };

        try {
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();

            // Recolectamos datos
            for (int i = 0; i < 4; i++) {
                String value = ((EditText) data.get(i)).getText().toString().trim();
                if (value.isEmpty()) {
                    throw new Exception("Debes ingresar " + userFriendlyNames[i] + ".");
                }
                keys.add(value);
            }

            // Validamos ID de usuario
            if (idUsr == null || idUsr.trim().isEmpty()) {
                throw new Exception("Falta el identificador de usuario.");
            }
            keys.add(idUsr);

            // Llenamos el mapa con los datos
            for (int i = 0; i < keys.size(); i++) {
                result.data.put(fieldName[i], keys.get(i).toLowerCase());
            }

            // Procesamos alergias
            String compoundCharacteristic = "";
            for (String allergy : alergias) {
                compoundCharacteristic += allergy + ",";
            }
            result.data.put(fieldName[5], compoundCharacteristic);

            // Procesamos patologías
            compoundCharacteristic = "";
            for (String pathology : patologias) {
                compoundCharacteristic += pathology + ",";
            }
            result.data.put(fieldName[6], compoundCharacteristic);

            result.exit = true;
            result.message = "Datos válidos";

        } catch (Exception e) {
            result.exit = false;
            result.message = e.getMessage();
        }

        Log.d("Client", result.data.toString());

        return result;
    }


    public static Map<String,String> toDesMapObject(Client client){

        HashMap<String,String> data = new HashMap<>();
        data.put("name",client.getName());
        data.put("ape",client.getApe());
        data.put("minKcal",client.getMinKal());
        data.put("maxKcal",client.getMaxKal());
        data.put("idUsr",client.getIdUsr());

        String compoundCharacteristic = "";
        for (String allergy : client.getAlergias()) {
            compoundCharacteristic = compoundCharacteristic + allergy + ",";
        }
        data.put("alergias",compoundCharacteristic);

        compoundCharacteristic = "";
        for (String pathology : client.getPatologias()) {
            compoundCharacteristic = compoundCharacteristic + pathology + ",";
        }
        data.put("patologias",compoundCharacteristic);

        return data;
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