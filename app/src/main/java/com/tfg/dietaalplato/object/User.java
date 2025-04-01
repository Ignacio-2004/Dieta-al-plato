package com.tfg.dietaalplato.object;

public class User {
    private String id;
    private String user;
    private String psw;

    // Constructor vacío (necesario para algunas operaciones como Firebase)
    public User() {
    }

    // Constructor con parámetros
    public User(String id, String user, String psw) {
        this.id = id;
        this.user = user;
        this.psw = psw;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    // Métdo para representar el objeto como String (opcional)
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }
}
