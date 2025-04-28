package com.tfg.dietaalplato.utilities;
//++IP - 25/04/2025 -

/**
 * Esta clase sirve para devolver el resultado de una operacion con el que luego se podra crear el tipo necesario para la siguiente operacion.
 */
public class ObjectResult<T> {
    public boolean exit;
    public String message;
    public T result;

    public ObjectResult(boolean exit, String message, T result) {
        this.exit = exit;
        this.message = message;
        this.result = result;
    }
}

//--Ip - 25/04/2025 -
