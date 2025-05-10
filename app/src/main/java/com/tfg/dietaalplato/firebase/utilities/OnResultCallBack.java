package com.tfg.dietaalplato.firebase.utilities;

/**
  Interfaz funcional utilizada para manejar resultados asíncronos.

  Se emplea cuando una operación (como guardar datos en base de datos)
  no puede devolver un resultado inmediatamente, debido a que se ejecuta
  de forma asíncrona (por ejemplo, mediante listeners de Firebase).

  Esta interfaz permite notificar al llamador cuando la operación ha finalizado,
  indicando si fue exitosa o no mediante un booleano, junto con un mensaje
  que explica el resultado.

 */

/*
Ejemplo de uso:

saveData(User.class, result, (success, message) -> {
        if (success) {
        Log.d("Guardar", "Éxito: " + message);
    } else {
            Log.e("Guardar", "Error: " + message);
    }
            });
*/

public interface OnResultCallBack<T> {
    void onResult( T result );
}
