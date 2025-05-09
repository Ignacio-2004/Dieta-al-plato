package com.tfg.dietaalplato.firebase.conectors.tools;

import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readAllFoodFromUser;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readAllFromCollection;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readClientFromUser;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readDietFromUser;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readFoodDiet;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireBaseValidator {

    /**
     * Metodo para validar si un usuario ya existe en la base de datos
     * @param result datos a validar
     * @param collectionName nombre de la coleccion a validar
     * @return Task<ValidationResult>
     * @throws FBCException excepcion
     */
    public static Task<ValidationResult> repeatObject(ValidationResult result, String collectionName) throws FBCException {

        TaskCompletionSource<ValidationResult> taskCompletionSource = new TaskCompletionSource<>();
        if (collectionName.equals("usuarios")) {
            /*No lee la coleccion*/
            readAllFromCollection(collectionName, User.class).addOnSuccessListener(
                            users -> {

                                for (User user : users) {
                                    if (user.getName().equals(result.data.get("name"))) {
                                        Log.e("Firebase", user.getId());
                                        taskCompletionSource.setResult(new ValidationResult(false, "El usuario ya existe", result.data));
                                        return;
                                    }
                                }
                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(users.size()), result.data));
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Log.d("Firebase", "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result.data)));
                            }
                    );
        } else {
            Log.d("Firebase", "❌ Tipo de dato no soportado");
            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Tipo de dato no soportado", result.data)));
        }
        return taskCompletionSource.getTask();
    }

    public static void exist(String name,String collectionName, OnResultCallBack<Boolean> result) throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        ValidationResult param = new ValidationResult(false, "El usuario ya existe", data);

        repeatObject(param, collectionName).addOnSuccessListener(
                validationResult -> {
                    result.onResult(validationResult.exit);
                }
        ).addOnFailureListener(
                e -> {
                    result.onResult(booleanAtomic.get());
                }
        );

    }

    /**
     * Metodo para validar si un cliente, alimento o dieta ya existe en la base de datos
     * @param result datos a validar
     * @param collectionName nombre de la coleccion a validar
     * @param idExt id del usuario
     * @return Task<ValidationResult>
     * @throws FBCException excepcion
     */
    public static Task<ValidationResult> repeatObject(ValidationResult result, String collectionName,String idExt) throws FBCException {

        TaskCompletionSource<ValidationResult> taskCompletionSource = new TaskCompletionSource<>();

        switch (collectionName){
            case "clientes":

                readClientFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        taskCompletionSource.setResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Client client : result1.result) {
                        if (client.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", client.getId());
                            taskCompletionSource.setResult(new ValidationResult(false, "El cliente ya existe", result.data));
                            return;
                        }
                    }

                    taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
                break;
            case "alimentos":
                readAllFoodFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        taskCompletionSource.setResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Food food : result1.result) {
                        if (food.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", food.getId());
                            taskCompletionSource.setResult(new ValidationResult(false, "El alimento ya existe", result.data));
                            return;
                        }
                    }

                    taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
            case "dietas":
                readDietFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        taskCompletionSource.setResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Diet diet : result1.result) {
                        if (diet.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", diet.getId());
                            taskCompletionSource.setResult(new ValidationResult(false, "La dieta ya existe", result.data));
                            return;
                        }
                    }

                    taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
            default:
                Log.d("Firebase", "❌ Tipo de dato no soportado");
                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Tipo de dato no soportado", result.data)));
        }
        return taskCompletionSource.getTask();
    }

    public static void exist(String name,String idExt, String collectionName, OnResultCallBack<Boolean> result) throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        ValidationResult param = new ValidationResult(false, "El usuario ya existe", data);

        repeatObject(param, collectionName, idExt).addOnSuccessListener(
                validationResult -> {
                    result.onResult(validationResult.exit);
                }
        ).addOnFailureListener(
                e -> {
                    result.onResult(booleanAtomic.get());
                }
        );
    }

    /**
     * Metodo para validar si una relacion alimento dieta existe ya existe en la base de datos
     * @param result datos a validar
     * @param collectionName nombre de la coleccion a validar
     * @param idDiet  id de la dieta
     * @param idFood id del alimento
     * @return Task<ValidationResult>
     * @throws FBCException excepcion
     */
    public static Task<ValidationResult> repeatObject(ValidationResult result, String collectionName,String idDiet, String idFood) throws FBCException {

        TaskCompletionSource<ValidationResult> taskCompletionSource = new TaskCompletionSource<>();

        switch (collectionName){
            case "dietaAlimentos":

                readFoodDiet(idDiet, idFood,result1 -> {
                    if (!result1.exit) {
                        for (FoodDiet foodDiet : result1.result){
                            if (foodDiet.getComida().equals(result.data.get("comida")) &&
                                    foodDiet.getNumeroPlato().equals(result.data.get("numeroPlato"))&&
                                    foodDiet.getDia().equals(result.data.get("dia")) &&
                                    foodDiet.getName().equals(result.data.get("nombreReceta"))){

                                Log.e("Firebase", foodDiet.getId());
                                taskCompletionSource.setResult(new ValidationResult(false, "El alimento ya existe", result.data));
                                return;
                            }
                        }

                        taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                        return;
                    }
                });
                break;
            default:
                Log.d("Firebase", "❌ Tipo de dato no soportado");
                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Tipo de dato no soportado", result.data)));
        }
        return taskCompletionSource.getTask();
    }

    public static void exist(String name,String idDiet, String idFood,String collectionName, OnResultCallBack<Boolean> result) throws FBCException {

        AtomicBoolean booleanAtomic = new AtomicBoolean(false);

        Map<String, String> data = new HashMap<>();
        data.put("name", name);
        ValidationResult param = new ValidationResult(false, "El usuario ya existe", data);

        repeatObject(param, collectionName, idDiet, idFood).addOnSuccessListener(
                validationResult -> {
                    result.onResult(validationResult.exit);
                }
        ).addOnFailureListener(
                e -> {
                    result.onResult(booleanAtomic.get());
                }
        );
    }

}
