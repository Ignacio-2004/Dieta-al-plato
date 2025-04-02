package com.tfg.dietaalplato.object;

public class Food {
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

    public Food(String id, String idUser, String nombre, int pc, int energia, int proteina, int grasa, int ags,
                    int agmi, int agpi, int colesterol, int hc, int fibra, int vitC, int vitB6, int vitE,
                    int hierro, int sodio, int calcio, int potasio) {
        this.id = id;
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
}

