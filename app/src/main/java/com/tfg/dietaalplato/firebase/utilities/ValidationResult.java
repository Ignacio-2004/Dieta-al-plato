package com.tfg.dietaalplato.firebase.utilities;

import java.util.HashMap;
import java.util.Map;

//++IP - 23/04/2025 -

/*
 * == Clase de resultado de la creacion de la lista previa al guardado de datos en Firestore.
 * == Objetivo: Dar un resultado completo sobre la operacion de la creacion de la lista previa.
 *
 * Exit: True si la operacion se ha realizado correctamente y se peude pasar a la siguiente operacion.
 * Exit: False si la operacion no se ha realizado correctamente y se debe notificar al usuario.
 *
 * Message: Mensaje de notificacion que se mostrara al usuario.
 *
 * Map<String, String>: Mapa de datos que se el usuario a ingresado y que esta lsita pora guardar en Firestore.
 */

public class ValidationResult {
    public boolean exit;
    public String message;
    public Map<String, String> data;

    public ValidationResult(boolean exit, String message, Map<String, String> data) {
        this.exit = exit;
        this.message = message;
        this.data = data;
    }

    public ValidationResult() {
        data = new HashMap<>();
    }
}

//--IP - 23/04/2025 -
