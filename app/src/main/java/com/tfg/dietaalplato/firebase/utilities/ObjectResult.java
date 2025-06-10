package com.tfg.dietaalplato.firebase.utilities;
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

    public boolean isSuccess() {
        return exit;
    }

    // Getters y Setters
    public boolean isExit() {
        return exit;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

//--Ip - 25/04/2025 -
