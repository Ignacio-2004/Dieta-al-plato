package com.tfg.dietaalplato.firebase.conectors.tools;

import static com.tfg.dietaalplato.firebase.conectors.FireBaseConnector.*;
import static com.tfg.dietaalplato.firebase.conectors.tools.FireBaseValidator.repeatObject;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.firebase.utilities.ClassData;
import com.tfg.dietaalplato.firebase.utilities.ClassUtilities;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.SaveData;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FireBaseWriter {

    private static final String TAG = "FireBase/Writer";

    /**
     * Metodo principal para guardar los datos en la base de datos
     * @param classType clase a guardar
     * @param result datos a guardar
     * @return Task<ObjectResult<BaseObject>>
     * @param <T> clase a guardar
     */
    public static <T> Task<ObjectResult<BaseObject>> saveData(Class<T> classType, ValidationResult result) {

        ClassData classData;
        AtomicReference<Integer> rawId = new AtomicReference<>(-1);
        TaskCompletionSource<ObjectResult<BaseObject>> finalresoult = new TaskCompletionSource<>();


        String id;
        try {

            Log.d(TAG, "💾 Guardando datos...");

            if (!result.exit) {
                Log.d(TAG, "❌ Error al validar los datos: " + result.message);
                throw new ComplexFBCE(new ObjectResult<>(false, result.message, result));
            }

            classData = ClassUtilities.collectionData(classType);

            //Compruebo si ya hay guardado un objeto con el mismo nombre

            switch (classData.key) {
                case "USU":
                    repeatObject(result, classData.data).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    Log.w(TAG, "Usuario existente, operacion sin permisos de sobreescritura");
                                    result.exit = false;
                                    result.message = validationResult.message;
                                }else{
                                    Log.d(TAG, "Usuario no existente, inicio del guardado.");
                                    save(result,classData, validationResult.message,"-1").addOnSuccessListener(
                                            saveResult ->{
                                                finalresoult.setResult(saveResult);
                                            }
                                    ).addOnFailureListener(
                                            e -> {
                                                finalresoult.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                                            }
                                    );
                                }
                            }
                    ).addOnFailureListener(
                            e -> {
                                result.exit = false;
                                result.message = e.getMessage();
                            }
                    );

                    break;
                case "DIE":
                    repeatObject(result, classData.data, result.data.get("idCliente")).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    Log.w(TAG, "Dieta existente, operacion sin permisos de sobreescritura");
                                    result.exit = false;
                                    result.message = validationResult.message;
                                }else{
                                    Log.d(TAG, "Dieta no existente, inicio del guardado.");
                                    save(result,classData, validationResult.message,result.data.get("idCliente")).addOnSuccessListener(
                                            saveResult ->{
                                                finalresoult.setResult(saveResult);
                                            }
                                    ).addOnFailureListener(
                                            e -> {
                                                finalresoult.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                                            }
                                    );
                                }
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                result.exit = false;
                                result.message = e.getMessage();
                            }
                    );
                    break;
                case "ALI":
                case "CLI":
                    repeatObject(result, classData.data, result.data.get("idUsr").toUpperCase()).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    Log.w(TAG, classData.data+" existente, operacion sin permisos de sobreescritura");
                                    result.exit = false;
                                    result.message = validationResult.message;
                                }else{
                                    Log.d(TAG, classData.data+" no existente, inicio del guardado.");
                                    save(result,classData, validationResult.message,result.data.get("idUsr")).addOnSuccessListener(
                                            saveResult ->{
                                                finalresoult.setResult(saveResult);
                                            }
                                    ).addOnFailureListener(
                                            e -> {
                                                finalresoult.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                                            }
                                    );
                                }
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                result.exit = false;
                                result.message = e.getMessage();
                            }
                    );
                    break;
                case "FDI":
                    repeatObject(result, classData.data, result.data.get("idDieta"), result.data.get("idAlimento")).addOnSuccessListener(
                            validationResult -> {
                                Log.d(TAG, "FoodDiet no existente, inicio del guardado.");
                                if (!validationResult.exit) {
                                    result.exit = false;
                                    result.message = validationResult.message;
                                } else {
                                    Log.d(TAG, "FoodDiet existente, operacion sin permisos de sobreescritura");
                                    save(result,classData, validationResult.message,result.data.get("idDieta")).addOnSuccessListener(
                                            saveResult ->{
                                                finalresoult.setResult(saveResult);
                                            }
                                    ).addOnFailureListener(
                                            e -> {
                                                finalresoult.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                                            }
                                    );
                                }
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                result.exit = false;
                                result.message = e.getMessage();
                            }
                    );

                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new ComplexFBCE(new ObjectResult<>(false, "Tipo de dato no soportado", null));

            }

        } catch (FBCException e) {
            Log.d(TAG, "❌ Error al guardar los datos: " + e.getMessage());
            Log.d(TAG, "🏁 Fin del proceso");
            finalresoult.setException(e);
        }
        return finalresoult.getTask();
    }

    /**
     * Metodo secundario para guardar los datos en la base de datos forzando sobreescribir en el caso de que ya exista un objeto con los mismos credenciales
     * @param classType clase a guardar
     * @param result datos a guardar
     * @param force forzar a sobreescribir
     * @return Task<ObjectResult<BaseObject>>
     * @param <T> clase a guardar
     * @throws FBCException excepcion
     */
    public static <T> Task<ObjectResult<BaseObject>> saveData(Class<T> classType, ValidationResult result, boolean force) throws FBCException {

        final String TAG = "SaveDataForce";
        AtomicReference<TaskCompletionSource<ObjectResult<BaseObject>>> taskCompletionSource = new AtomicReference<>(new TaskCompletionSource<>());

        /*
         * Si es false llamo al original que filtra los repetidos
         */
        if (!force){
            Log.d(TAG, "force = false, derivando al metodo principal ...");
            saveData(classType, result).addOnSuccessListener(
                    validationResult -> {
                        taskCompletionSource.get().setResult(new ObjectResult<>(validationResult.exit, validationResult.message, validationResult.result));
                    }
            );
        }

        ClassData classData;
        AtomicReference<ValidationResult> deleteResult = new AtomicReference<>(new ValidationResult());

        try{
            Log.d(TAG, "💾 Guardando datos forzados...");

            if (!result.exit) {
                Log.w(TAG, "❌ Error al validar los datos: " + result.message);
                throw new ComplexFBCE(new ObjectResult<>(false, result.message, result));
            }

            classData = ClassUtilities.collectionData(classType);
            //Compruebo si ya hay guardado un objeto con el mismo nombre

            switch (classData.key) {
                case "USU":
                    repeatObject(result, classData.data).addOnSuccessListener( validationResult -> {
                        if (validationResult.exit) {
                            FireBaseRemover.remove(validationResult.data.get("id")).addOnSuccessListener(
                                    validationResult1 -> {
                                        taskCompletionSource.set(finalResultOfForce(validationResult1, classType));
                                    }
                            );
                        }
                    });

                    break;
                case "DIE":
                    repeatObject(result, classData.data, result.data.get("idCliente")).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    FireBaseRemover.remove(validationResult.data.get("id")).addOnSuccessListener(
                                            validationResult1 -> {
                                                taskCompletionSource.set(finalResultOfForce(validationResult1,classType));
                                            });
                                }
                            }
                    );

                    break;
                case "ALI":
                case "CLI":
                    repeatObject(result, classData.data, result.data.get("idUsr")).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    FireBaseRemover.remove(validationResult.data.get("id")).addOnSuccessListener(
                                            validationResult1 -> {
                                                taskCompletionSource.set(finalResultOfForce(validationResult1,classType));
                                            }
                                    );
                                }
                            }
                    );

                    break;
                case "FDI":
                    repeatObject(result, classData.data, result.data.get("idDieta"), result.data.get("idAlimento")).addOnSuccessListener(
                            validationResult -> {
                                if (!validationResult.exit) {
                                    FireBaseRemover.remove(validationResult.data.get("id")).addOnSuccessListener(
                                            validationResult1 -> {
                                                taskCompletionSource.set(finalResultOfForce(validationResult1,classType));
                                            }
                                    );
                                }
                            }
                    );

                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new ComplexFBCE(new ObjectResult<>(false, "Tipo de dato no soportado", result));
            }

        }catch (FBCException e){
            taskCompletionSource.get().setException(e);
        }

        return taskCompletionSource.get().getTask();

    }

    private static Task<ObjectResult<BaseObject>> save(ValidationResult result, ClassData classData, String rawId, String idExt) {

        String id;
        TaskCompletionSource<ObjectResult<BaseObject>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        try{
            if (!result.exit) {
                Log.w(TAG, "Ya existe un objeto con los mismos credenciales: " + result.message);
                taskCompletionSource.setResult(new ObjectResult<>(false, result.message, null));
                return taskCompletionSource.getTask();
            }
            Log.d(TAG, "💾 Metodo save, listo para guardar");
            id = ClassUtilities.generateId(classData, Integer.parseInt(rawId), idExt);
            Log.d(TAG, "💾 ID generado: " + id);

            switch (classData.data) {
                case "usuarios":
                    Log.d(TAG, "💾"+classData.key+" "+classData.data);
                    save(new User(id, result.data.get("name"), result.data.get("psw"))).addOnSuccessListener(
                            userResult -> {
                                Log.d(TAG, "✅ Usuario guardado con éxito en Firestore");
                                taskCompletionSource.setResult(new ObjectResult<>(userResult.exit, userResult.message, userResult.result));
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));

                            }
                    );

                    break;
                case "clientes":
                    Log.d(TAG, "💾"+classData.key+" "+classData.data);

                    ArrayList<String> alergies = new ArrayList<>();
                    ArrayList<String> pathologies = new ArrayList<>();

                    for (String allergy : result.data.get("alergias").split(",")) {
                        alergies.add(allergy);
                    }

                    for (String pathology : result.data.get("patologias").split(",")) {
                        pathologies.add(pathology);
                    }


                    save(new Client(id.toUpperCase(), result.data.get("name"), result.data.get("ape"), result.data.get("idUsr").toUpperCase(), alergies, pathologies)).addOnSuccessListener(
                            clientResult -> {
                                Log.d(TAG, "✅ Cliente guardado con éxito en Firestore");
                                taskCompletionSource.setResult(new ObjectResult<>(clientResult.exit, clientResult.message, clientResult.result));
                                Log.d(TAG,"✅ Cliente guardado con éxito en Local");
                                saveData.addClient(clientResult.result);
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                            }
                    );
                    break;
                case "dietas":
                    Log.d(TAG, "💾"+classData.key+" "+classData.data);
                    save(new Diet(id.toUpperCase(), result.data.get("tip"), result.data.get("idClient").toUpperCase(), result.data.get("just"), result.data.get("idUsr").toUpperCase())).addOnSuccessListener(
                            dietResult -> {
                                Log.d(TAG, "✅ Dieta guardado con éxito en Firestore");
                                taskCompletionSource.setResult(new ObjectResult<>(dietResult.exit, dietResult.message, dietResult.result));
                                Log.d(TAG,"✅ Dieta guardado con éxito en Local");
                                saveData.addDiet(dietResult.result);
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                            }
                    );
                    break;
                case "alimentos":
                    Log.d(TAG, "💾"+classData.key+" "+classData.data);
                    save(new Food(id.toUpperCase(), result.data.get("name"), result.data.get("idUsr").toUpperCase(), result.data.get("pc"), result.data.get("energia"),
                            result.data.get("proteina"), result.data.get("grasa"), result.data.get("ags"), result.data.get("agmi"),
                            result.data.get("agpi"), result.data.get("colesterol"), result.data.get("hc"), result.data.get("fibra"), result.data.get("vitC"),
                            result.data.get("vitB6"), result.data.get("vitE"), result.data.get("hierro"), result.data.get("sodio"), result.data.get("calcio"),
                            result.data.get("potasio"))).addOnSuccessListener(
                            foodResult -> {
                                Log.d(TAG, "✅ Alimento guardado con éxito en Firestore");
                                taskCompletionSource.setResult(new ObjectResult<>(foodResult.exit, foodResult.message, foodResult.result));
                                Log.d(TAG,"✅ Alimento guardado con éxito en Local");
                                saveData.addFood(foodResult.result);
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                            }
                    );
                    break;
                case "dietaAlimentos":
                    Log.d(TAG, "💾"+classData.key+" "+classData.data);
                    save(new FoodDiet(id.toUpperCase(), result.data.get("idDieta").toUpperCase(), result.data.get("idAlimento").toUpperCase(), result.data.get("comida"),
                            result.data.get("numeroPlato"), result.data.get("dia"), result.data.get("nombreReceta"), result.data.get("idUsr"))).addOnSuccessListener(
                            foodDietResult -> {
                                Log.d(TAG, "✅ FoodDiet guardado con éxito en Firestore");
                                taskCompletionSource.setResult(new ObjectResult<>(foodDietResult.exit, foodDietResult.message, foodDietResult.result));
                                Log.d(TAG,"✅ FoodDiet guardado con éxito en Local");
                                saveData.addFoodDiet(foodDietResult.result);
                            }
                    ).addOnFailureListener(
                            e -> {
                                Log.d(TAG, e.getMessage());
                                taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), result)));
                            }
                    );
                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new FBCException("Tipo de dato no soportado");
            }

            Log.d(TAG, "✅ Datos guardados correctamente. ID: " + id);
            Log.d(TAG, "📂 Tipo de colección: " + classData.data);
            Log.d(TAG, "🏁 Fin del proceso");
        }catch (FBCException e){
            Log.e(TAG, "❌ Error al guardar los datos: " + e.getMessage());
            Log.d(TAG, "🏁 Fin del proceso");
            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, e.getMessage(), null)));
        }
        return taskCompletionSource.getTask();
    }

    private static Task<ObjectResult<FoodDiet>> save(FoodDiet dietFood) throws FBCException {

        FirebaseFirestore fst = getInstance().getFirestore();

        // Crear un objeto Map con los datos de la DietFood
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("idDieta", dietFood.getIdDieta());
        foodData.put("idAlimento", dietFood.getIdAlimento());
        foodData.put("comida", dietFood.getComida());
        foodData.put("numeroPlato", dietFood.getNumeroPlato());
        foodData.put("dia", dietFood.getDia());
        foodData.put("nombreReceta", dietFood.getName());

        TaskCompletionSource<ObjectResult<FoodDiet>> callback = new TaskCompletionSource<>();
        // Guardar en Firestore en la colección "diet_food"
        fst.collection("dietaAlimentos").document(dietFood.getIdDieta() + "_" + dietFood.getIdAlimento())
                .set(foodData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ DietFood guardado con éxito en Firestore");
                    callback.setResult(new ObjectResult<>(true, "success", dietFood));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar DietFood: " + e.getMessage());
                    callback.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al guardar DietFood: " + e.getMessage(), null)));
                });
        return callback.getTask();
    }

    private static Task<ObjectResult<Food>> save(Food alimento) throws FBCException {


        FirebaseFirestore fst = getInstance().getFirestore();


        // Crear un objeto Map con los datos del alimento
        HashMap<String, Object> food = new HashMap<>();
        food.put("id", alimento.getId());
        food.put("idUser", alimento.getIdUser());
        food.put("nombre", alimento.getName());
        food.put("pc", alimento.getPc());
        food.put("energia", alimento.getEnergia());
        food.put("proteina", alimento.getProteina());
        food.put("grasa", alimento.getGrasa());
        food.put("ags", alimento.getAgs());
        food.put("agmi", alimento.getAgmi());
        food.put("agpi", alimento.getAgpi());
        food.put("colesterol", alimento.getColesterol());
        food.put("hc", alimento.getHc());
        food.put("fibra", alimento.getFibra());
        food.put("vitC", alimento.getVitC());
        food.put("vitB6", alimento.getVitB6());
        food.put("vitE", alimento.getVitE());
        food.put("hierro", alimento.getHierro());
        food.put("sodio", alimento.getSodio());
        food.put("calcio", alimento.getCalcio());
        food.put("potasio", alimento.getPotasio());

        TaskCompletionSource<ObjectResult<Food>> callback = new TaskCompletionSource<>();
        // Guardar en Firestore en la colección "alimentos"
        fst.collection("alimentos").document(alimento.getId())
                .set(food)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Alimento guardado con éxito en Firestore");
                    callback.setResult(new ObjectResult<>(true, "success", alimento));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar Alimento: " + e.getMessage());
                    callback.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al guardar Alimento: " + e.getMessage(), null)));
                });
        return callback.getTask();
    }

    private static   Task<ObjectResult<Diet>> save(Diet diet) throws FBCException {


        FirebaseFirestore fst = getInstance().getFirestore();


        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> client = new HashMap<>();
        client.put("id", diet.getId());
        client.put("tip", diet.getTip());
        client.put("idClient", diet.getIdCliente());
        client.put("just", diet.getJust());

        TaskCompletionSource<ObjectResult<Diet>> callback = new TaskCompletionSource<>();
        // Guardar en Firestore en la colección "usuarios"
        fst.collection("dietas").document(diet.getId())
                .set(client)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Dieta guardado con éxito en Firestore");
                    callback.setResult(new ObjectResult<>(true, "success", diet));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar Dieta: " + e.getMessage());
                    callback.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al guardar Dieta: " + e.getMessage(), null)));
                });
        return callback.getTask();
    }

    private static Task<ObjectResult<Client>> save(Client cli) throws FBCException {


        FirebaseFirestore fst = getInstance().getFirestore();


        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> client = new HashMap<>();
        client.put("id", cli.getId());
        client.put("name", cli.getName());
        client.put("ape", cli.getApe());
        client.put("idUsr", cli.getIdUsr());
        client.put("alergias", cli.getAlergias());
        client.put("patologias", cli.getPatologias());



        TaskCompletionSource<ObjectResult<Client>> callback = new TaskCompletionSource<>();
        // Guardar en Firestore en la colección "usuarios"
        fst.collection("clientes").document(cli.getId())
                .set(client)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Cliente guardado con éxito en Firestore");
                    callback.setResult(new ObjectResult<>(true, "success", cli));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar Cliente: " + e.getMessage());
                    callback.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al guardar Cliente: " + e.getMessage(), null)));
                });
        return callback.getTask();
    }

    private static Task<ObjectResult<User>> save(User user) throws FBCException {


        FirebaseFirestore fst = getInstance().getFirestore();


        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> usuario = new HashMap<>();
        usuario.put("id", user.getId());
        usuario.put("name", user.getName());
        usuario.put("psw", user.getPsw());

        TaskCompletionSource<ObjectResult<User>> callback = new TaskCompletionSource<>();
        // Guardar en Firestore en la colección "usuarios"
        fst.collection("usuarios").document(user.getId())
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Usuario guardado con éxito en Firestore");
                    callback.setResult(new ObjectResult<>(true, "success", user));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al guardar usuario: " + e.getMessage());
                    callback.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al guardar usuario: " + e.getMessage(), user)));
                });
        return callback.getTask();
    }

    private static TaskCompletionSource<ObjectResult<BaseObject>> finalResultOfForce(ValidationResult result, Class<?> classType){

        TaskCompletionSource<ObjectResult<BaseObject>> taskResult = new TaskCompletionSource<>();

        if (result.message == null){
            Log.d(TAG, "🔍 No había objeto duplicado para eliminar. Guardando sin eliminar.");
            Log.d(TAG, "🏁 Fin del proceso forzado");
            saveData(classType, result).addOnSuccessListener(
                    validationResult -> {
                        taskResult.setResult(validationResult);
                    }
            ).addOnFailureListener(
                    e -> {
                        taskResult.setException(e);
                    }
            );
        }else if(result.exit){
            Log.d(TAG, "✅ Objeto repetido eliminado");
            Log.d(TAG, "🏁 Fin del proceso forzado, redirigiendo al principal ...");
            saveData(classType, result).addOnSuccessListener(
                    validationResult -> {
                        taskResult.setResult(validationResult);
                    }
            ).addOnFailureListener(
                    e -> {
                        taskResult.setException(e);
                    }
            );
        }else{
            Log.d(TAG, "❌ Error al eliminar el objeto repetido");
            Log.d(TAG, "🏁 Fin del proceso forzado");
            saveData(classType, result).addOnSuccessListener(
                    validationResult -> {
                        taskResult.setResult(validationResult);
                    }
            ).addOnFailureListener(
                    e -> {
                        taskResult.setException(e);
                    }
            );;
        }

        return taskResult;
    }
}
