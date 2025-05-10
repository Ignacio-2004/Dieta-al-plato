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

public class Food extends BaseObject {

    private String idUser;
    private String pc;
    private String energia;
    private String proteina;
    private String grasa;
    private String ags;
    private String agmi;
    private String agpi;
    private String colesterol;
    private String hc;
    private String fibra;
    private String vitC;
    private String vitB6;
    private String vitE;
    private String hierro;
    private String sodio;
    private String calcio;
    private String potasio;

    public Food() {
    }

    public Food(String id, String name, String idUser, String pc, String energia, String proteina,
                String grasa, String ags, String agmi, String agpi, String colesterol, String hc,
                String fibra, String vitC, String vitB6, String vitE, String hierro, String sodio,
                String calcio, String potasio) {
        super(id, name);
        this.idUser = idUser;
        this.pc = pc;
        this.energia = energia;
        this.proteina = proteina;
        this.grasa = grasa;
        this.ags = ags;
        this.agmi = agmi;
        this.agpi = agpi;
        this.colesterol = colesterol;
        this.hc = hc;
        this.fibra = fibra;
        this.vitC = vitC;
        this.vitB6 = vitB6;
        this.vitE = vitE;
        this.hierro = hierro;
        this.sodio = sodio;
        this.calcio = calcio;
        this.potasio = potasio;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getEnergia() {
        return energia;
    }

    public void setEnergia(String energia) {
        this.energia = energia;
    }

    public String getProteina() {
        return proteina;
    }

    public void setProteina(String proteina) {
        this.proteina = proteina;
    }

    public String getGrasa() {
        return grasa;
    }

    public void setGrasa(String grasa) {
        this.grasa = grasa;
    }

    public String getAgs() {
        return ags;
    }

    public void setAgs(String ags) {
        this.ags = ags;
    }

    public String getAgmi() {
        return agmi;
    }

    public void setAgmi(String agmi) {
        this.agmi = agmi;
    }

    public String getAgpi() {
        return agpi;
    }

    public void setAgpi(String agpi) {
        this.agpi = agpi;
    }

    public String getColesterol() {
        return colesterol;
    }

    public void setColesterol(String colesterol) {
        this.colesterol = colesterol;
    }

    public String getHc() {
        return hc;
    }

    public void setHc(String hc) {
        this.hc = hc;
    }

    public String getFibra() {
        return fibra;
    }

    public void setFibra(String fibra) {
        this.fibra = fibra;
    }

    public String getVitC() {
        return vitC;
    }

    public void setVitC(String vitC) {
        this.vitC = vitC;
    }

    public String getVitB6() {
        return vitB6;
    }

    public void setVitB6(String vitB6) {
        this.vitB6 = vitB6;
    }

    public String getVitE() {
        return vitE;
    }

    public void setVitE(String vitE) {
        this.vitE = vitE;
    }

    public String getHierro() {
        return hierro;
    }

    public void setHierro(String hierro) {
        this.hierro = hierro;
    }

    public String getSodio() {
        return sodio;
    }

    public void setSodio(String sodio) {
        this.sodio = sodio;
    }

    public String getCalcio() {
        return calcio;
    }

    public void setCalcio(String calcio) {
        this.calcio = calcio;
    }

    public String getPotasio() {
        return potasio;
    }

    public void setPotasio(String potasio) {
        this.potasio = potasio;
    }

    //++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data,String idUser){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"idUsr","Name","pc","energia","proteina","grasa","ags","agmi","agpi","colesterol","hc","fibra","vitC","vitB6","vitE","hierro","sodio","calcio","potasio"};

        try{
            result.data = new HashMap<>();

            ArrayList<String> keys = new ArrayList<>();
            keys.add(idUser);
            keys.add(((EditText) data.get(0)).getText().toString().trim());
            keys.add(((EditText) data.get(1)).getText().toString().trim());
            keys.add(((EditText) data.get(2)).getText().toString().trim());
            keys.add(((EditText) data.get(3)).getText().toString().trim());
            keys.add(((EditText) data.get(4)).getText().toString().trim());
            keys.add(((EditText) data.get(5)).getText().toString().trim());
            keys.add(((EditText) data.get(6)).getText().toString().trim());
            keys.add(((EditText) data.get(7)).getText().toString().trim());
            keys.add(((EditText) data.get(8)).getText().toString().trim());
            keys.add(((EditText) data.get(9)).getText().toString().trim());
            keys.add(((EditText) data.get(10)).getText().toString().trim());
            keys.add(((EditText) data.get(11)).getText().toString().trim());
            keys.add(((EditText) data.get(12)).getText().toString().trim());
            keys.add(((EditText) data.get(13)).getText().toString().trim());
            keys.add(((EditText) data.get(14)).getText().toString().trim());
            keys.add(((EditText) data.get(15)).getText().toString().trim());
            keys.add(((EditText) data.get(16)).getText().toString().trim());

            for (int i = 0; i < keys.size()-1; i++) {

                //Comprobamos que no haya campos vacios

                if (keys.get(i).isEmpty()) {
                    throw new Exception("El campo " + data.get(i-1).getTag().toString() + " no puede estar vacio");
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

        FireBaseValidator.exist(getName(),getIdUser(),String.valueOf(TablesNames.alimentos), result -> {
            if (result){
                booleanAtomic.set(true);
            }
            });

        return booleanAtomic.get();

    }

//--IP - 23/04/2025 -
}

