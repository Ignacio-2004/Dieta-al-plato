package com.tfg.dietaalplato.object;
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
}