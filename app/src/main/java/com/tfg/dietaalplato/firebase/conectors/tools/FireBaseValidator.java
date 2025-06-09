package com.tfg.dietaalplato.firebase.conectors.tools;

import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readAllFoodFromUser;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readAllFromCollection;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readClientFromUser;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readDietFromClient;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readFoodDiet;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader.readFoodDietByDiet;

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
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireBaseValidator {
    private static final String TAG = "FireBase/Validator";

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
                                        taskCompletionSource.setResult(new ValidationResult(false, "El usuario ya existe", User.toDesMapObject(user)));
                                        return;
                                    }
                                }
                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(users.size()), result.data));
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Log.d("Firebase", "❌ Error al leer la colección " + collectionName);
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al leer la base de datos" , result.data)));
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

                readClientFromUser(idExt).addOnSuccessListener(
                        clientes -> {
                            if (!clientes.exit) {
                                Log.d(TAG, "No hay clientes");
                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(0), result.data));
                                return;
                            }

                            for (Client client : clientes.result) {
                                if (client.getName().toUpperCase().equals(result.data.get("name").toUpperCase())) {
                                    if (client.getApe().toUpperCase().equals(result.data.get("ape").toUpperCase())){
                                        Log.e(TAG, client.getId());
                                        taskCompletionSource.setResult(new ValidationResult(false, "El cliente ya existe", Client.toDesMapObject(client)));
                                        return;
                                    }
                                }
                            }

                            taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(clientes.result.size()), result.data));
                            return;
                        }
                ).addOnFailureListener(
                        e -> {
                            Log.d(TAG, "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al leer la base de datos", result.data)));
                        }
                );
                break;
            case "alimentos":
                readAllFoodFromUser(idExt).addOnSuccessListener(
                        foods ->{
                            if (foods.exit) {
                                Map<String, Food> foodsCollection = foods.result;

                                for (Food food : foodsCollection.values()) {
                                    if (food.getName().equals(result.data.get("name"))) {
                                        Log.e(TAG, food.getId());
                                        taskCompletionSource.setResult(new ValidationResult(false, "El alimento ya existe", Food.toDesMapObject(food)));
                                        return;
                                    }
                                }

                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(foodsCollection.size()), result.data));
                            }
                            else{
                                Log.d(TAG, "No hay alimentos");
                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(0), result.data));
                            }
                        }
                ).addOnFailureListener(
                        e -> {
                            Log.d(TAG, "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al leer la base de datos", result.data)));
                        }
                );
                break;
            case "dietas":
                readDietFromClient(idExt).addOnSuccessListener(
                        dietas -> {
                            if (dietas.exit) {

                                ArrayList<Diet> diets = new ArrayList<>(dietas.result.values());

                                for (Diet diet : diets){
                                    Log.d("Firebase", diet.getName());
                                    Log.d("Firebase", result.data.get("name"));
                                    if (diet.getName().equalsIgnoreCase(result.data.get("name"))) {
                                        Log.e("Firebase", diet.getId());
                                        taskCompletionSource.setResult(new ValidationResult(false, "La dieta ya existe", Diet.toDesMapObject(diet)));
                                        return;
                                    }
                                }

                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(dietas.result.size()), result.data));
                            }else{
                                taskCompletionSource.setResult(new ValidationResult(true, String.valueOf(0), result.data));
                            }
                        }
                ).addOnFailureListener(
                        e -> {
                            Log.d("Firebase", "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al leer la base de datos", result.data)));
                        }
                );
                break;
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
            case "comidaDietas":

                readFoodDietByDiet(idDiet.toUpperCase()).addOnSuccessListener(
                        foodDiets -> {
                            if (foodDiets.exit) {
                                Map<String, ArrayList<FoodDiet>> foodDietsCollection = foodDiets.result;

                                for (ArrayList<FoodDiet> foodDiet : foodDietsCollection.values()) {
                                    for (FoodDiet foodDiet1 : foodDiet) {
                                        if (foodDiet1.getIdAlimento().equals(idFood)) {
                                            Log.e("Firebase", foodDiet1.getId());
                                            taskCompletionSource.setResult(new ValidationResult(false, "La relacion ya existe", FoodDiet.toDesMapObject(foodDiet1)));
                                            return;
                                        }
                                    }
                                }
                            }
                            taskCompletionSource.setResult(new ValidationResult(true,"0", result.data));
                            return;
                        }
                ).addOnFailureListener(
                        e -> {
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result.data)));
                            return;
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
