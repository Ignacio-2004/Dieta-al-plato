package com.tfg.dietaalplato.object;

public class Diet {

    private String id;
    private int tip;
    private String idCliente;
    private String just;

    public Diet(String id, int tip, String idCliente, String just) {
        this.id = id;
        setTip(tip);  // Usamos el setter para validar el valor de tip
        this.idCliente = idCliente;
        this.just = just;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        if (tip == 1 || tip == 3 || tip == 7) {
            this.tip = tip;
        } else {
            throw new IllegalArgumentException("El valor de 'tip' debe ser 1, 3 o 7");
        }
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getJust() {
        return just;
    }

    public void setJust(String just) {
        this.just = just;
    }

    @Override
    public String toString() {
        return "Dietas{" +
                "id='" + id + '\'' +
                ", tip=" + tip +
                ", idCliente='" + idCliente + '\'' +
                ", just='" + just + '\'' +
                '}';
    }
}

