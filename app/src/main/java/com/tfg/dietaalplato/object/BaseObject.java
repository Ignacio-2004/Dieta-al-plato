package com.tfg.dietaalplato.object;
//++IP - 24/04/2025 -

/**
   Con esta clase buscamos tener un nexo en comun entre todas las clases que componen la bd, de esta forma
   en metodos genericos podemos sacar informacion de las clases sin tener que buscar la clase ezxacta a la que pertenece ahorrando asi codigo.
 */

public class BaseObject {
    private String id;
    private String name;

    public BaseObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//--IP - 24/04/2025 -