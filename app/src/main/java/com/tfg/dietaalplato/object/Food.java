package com.tfg.dietaalplato.object;

import android.view.View;
import android.widget.EditText;

import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;

public class Food extends BaseObject{
    private String id;
    private String idUser;
    private String nombre; // Alimento
    private int pc;
    private int energia;
    private int proteina;
    private int grasa;
    private int ags;
    private int agmi;
    private int agpi;
    private int colesterol;
    private int hc;
    private int fibra;
    private int vitC;
    private int vitB6;
    private int vitE;
    private int hierro;
    private int sodio;
    private int calcio;
    private int potasio;

    public Food(String id, String name, String id1, String idUser, String nombre, int pc, int energia,
                int proteina, int grasa, int ags, int agmi, int agpi, int colesterol, int hc, int fibra,
                int vitC, int vitB6, int vitE, int hierro, int sodio, int calcio, int potasio) {

        super(id, name);
        this.id = id1;
        this.idUser = idUser;
        this.nombre = nombre;
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

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getPc() { return pc; }
    public void setPc(int pc) { this.pc = pc; }

    public int getEnergia() { return energia; }
    public void setEnergia(int energia) { this.energia = energia; }

    public int getProteina() { return proteina; }
    public void setProteina(int proteina) { this.proteina = proteina; }

    public int getGrasa() { return grasa; }
    public void setGrasa(int grasa) { this.grasa = grasa; }

    public int getAgs() { return ags; }
    public void setAgs(int ags) { this.ags = ags; }

    public int getAgmi() { return agmi; }
    public void setAgmi(int agmi) { this.agmi = agmi; }

    public int getAgpi() { return agpi; }
    public void setAgpi(int agpi) { this.agpi = agpi; }

    public int getColesterol() { return colesterol; }
    public void setColesterol(int colesterol) { this.colesterol = colesterol; }

    public int getHc() { return hc; }
    public void setHc(int hc) { this.hc = hc; }

    public int getFibra() { return fibra; }
    public void setFibra(int fibra) { this.fibra = fibra; }

    public int getVitC() { return vitC; }
    public void setVitC(int vitC) { this.vitC = vitC; }

    public int getVitB6() { return vitB6; }
    public void setVitB6(int vitB6) { this.vitB6 = vitB6; }

    public int getVitE() { return vitE; }
    public void setVitE(int vitE) { this.vitE = vitE; }

    public int getHierro() { return hierro; }
    public void setHierro(int hierro) { this.hierro = hierro; }

    public int getSodio() { return sodio; }
    public void setSodio(int sodio) { this.sodio = sodio; }

    public int getCalcio() { return calcio; }
    public void setCalcio(int calcio) { this.calcio = calcio; }

    public int getPotasio() { return potasio; }
    public void setPotasio(int potasio) { this.potasio = potasio; }


//++IP - 23/04/2025 -

    public static ValidationResult toMapData(ArrayList<View> data,String idUser){
        ValidationResult result = new ValidationResult();

        String[] fieldName = {"idUser","Name","pc","energia","proteina","grasa","ags","agmi","agpi","colesterol","hc","fibra","vitC","vitB6","vitE","hierro","sodio","calcio","potasio"};

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

