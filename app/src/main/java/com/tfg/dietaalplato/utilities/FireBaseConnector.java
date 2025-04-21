package com.tfg.dietaalplato.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.object.Client;
import com.tfg.dietaalplato.object.Diet;
import com.tfg.dietaalplato.object.Food;
import com.tfg.dietaalplato.object.FoodDiet;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.exception.FBCException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class FireBaseConnector {

    private static FireBaseConnector instance;

    private DatabaseReference ref;
    private FirebaseDatabase bd;
    private FirebaseFirestore fst;
    private static final String TAG = "Firebase";

    private FireBaseConnector() {
        bd = FirebaseDatabase.getInstance("https://dieta-al-plato-20-default-rtdb.europe-west1.firebasedatabase.app");
        fst = FirebaseFirestore.getInstance();
        this.ref = bd.getReference();
    }

    public static FireBaseConnector getInstance() {
        if (instance == null) {
            instance = new FireBaseConnector();
        }
        return instance;
    }

    public void testFirebaseConnection() throws FBCException {

        if (ref != null) {
            String testKey = "test_" + System.currentTimeMillis();

            Log.d(TAG, "============= testFirebaseConnection: " + ref.toString());

            // Escritura
            ref.child(testKey).setValue("conexion_exitosa")
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "✅ Datos escritos en Firebase Europa");

                        // Lectura para verificar
                        ref.child(testKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String value = snapshot.getValue(String.class);
                                Log.d(TAG, "📖 Valor leído: " + value);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.e(TAG, "❌ Error lectura: " + error.getMessage());
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "❌ Error escritura: " + e.getMessage());
                    });
            Log.d(TAG, "============= testFirebaseConnection: " + ref.toString());
        }else{
            throw new FBCException("La referencia no puede ser nula");
        }

    }

    public void monitorConnectionStatus() throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        if (ref != null){
            DatabaseReference connectedRef = ref.getRoot().child(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Boolean connected = snapshot.getValue(Boolean.class);
                    Log.d(TAG, "🔌 Estado conexión: " + (connected ? "CONECTADO" : "DESCONECTADO"));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG, "Monitor conexión cancelado: " + error.getMessage());
                }
            });
        }else{
            throw new FBCException("La referencia no puede ser nula");
        }
    }

    public void saveUser(User user) throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> usuario = new HashMap<>();
        usuario.put("id", user.getId());
        usuario.put("user", user.getUser());
        usuario.put("psw", user.getPsw());

        // Guardar en Firestore en la colección "usuarios"
        fst.collection("usuarios").document(user.getId())
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ Usuario guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar usuario: " + e.getMessage());
                });
    }
private boolean correcto = false; // variable para comprobar que existe
    public boolean verifyUser(String id) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento del usuario
        fst.collection("usuarios").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el documento a un objeto User
                        User usuario = documentSnapshot.toObject(User.class);
                        if (usuario != null) {
                            Log.d("Firebase", "📖 Usuario leído: " + usuario.getUser() + ", Password: " + usuario.getPsw());
                            taskCompletionSource.setResult(usuario);  // Devolver el objeto User
                            correcto =true;
                        }
                    } else {
                        Log.d("Firebase", "⚠️ Usuario no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("Usuario no encontrado"));

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer usuario: " + e.getMessage());
                    taskCompletionSource.setException(e);  // Devolver la excepción

                });

        return correcto;
    }
    public Task<User> readUsuario(String id) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento del usuario
        fst.collection("usuarios").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el documento a un objeto User
                        User usuario = documentSnapshot.toObject(User.class);
                        if (usuario != null) {
                            Log.d("Firebase", "📖 Usuario leído: " + usuario.getUser() + ", Password: " + usuario.getPsw());
                            taskCompletionSource.setResult(usuario);  // Devolver el objeto User
                        }
                    } else {
                        Log.d("Firebase", "⚠️ Usuario no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("Usuario no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer usuario: " + e.getMessage());
                    taskCompletionSource.setException(e);  // Devolver la excepción
                });

        return taskCompletionSource.getTask();
    }

    public void saveClient(Client cli) throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> client = new HashMap<>();
        client.put("id", cli.getId());
        client.put("cli", cli.getCli());
        client.put("ape", cli.getApe());
        client.put("idUsr", cli.getIdUsr());

        // Guardar en Firestore en la colección "usuarios"
        fst.collection("clientes").document(cli.getId())
                .set(client)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ Cliente guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar Cliente: " + e.getMessage());
                });
    }

    public Task<Client> readClient(String id) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<Client> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento del usuario
        fst.collection("clientes").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el documento a un objeto User
                        Client client = documentSnapshot.toObject(Client.class);
                        if (client != null) {
                            Log.d("Firebase", "📖 cliente leído: " + client);
                            taskCompletionSource.setResult(client);  // Devolver el objeto User
                        }
                    } else {
                        Log.d("Firebase", "⚠️ Cliente no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("Cliente no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer Cliente: " + e.getMessage());
                    taskCompletionSource.setException(e);  // Devolver la excepción
                });

        return taskCompletionSource.getTask();
    }

    public void saveDiet(Diet diet) throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> client = new HashMap<>();
        client.put("id", diet.getId());
        client.put("tip", diet.getTip());
        client.put("idClient", diet.getIdCliente());
        client.put("just", diet.getJust());

        // Guardar en Firestore en la colección "usuarios"
        fst.collection("dietas").document(diet.getId())
                .set(client)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ Dieta guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar Dieta: " + e.getMessage());
                });
    }

    public Task<Diet> readDieta(String id) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<Diet> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento del usuario
        fst.collection("dietas").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el documento a un objeto User
                        Diet diet = documentSnapshot.toObject(Diet.class);
                        if (diet != null) {
                            Log.d("Firebase", "📖 cliente leído: " + diet);
                            taskCompletionSource.setResult(diet);  // Devolver el objeto User
                        }
                    } else {
                        Log.d("Firebase", "⚠️ Dieta no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("Dieta no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer Dieta: " + e.getMessage());
                    taskCompletionSource.setException(e);  // Devolver la excepción
                });

        return taskCompletionSource.getTask();
    }

    public void saveFood(Food alimento) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        // Crear un objeto Map con los datos del alimento
        HashMap<String, Object> food = new HashMap<>();
        food.put("id", alimento.getId());
        food.put("idUser", alimento.getIdUser());
        food.put("nombre", alimento.getNombre());
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

        // Guardar en Firestore en la colección "alimentos"
        fst.collection("alimentos").document(alimento.getId())
                .set(food)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ Alimento guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar Alimento: " + e.getMessage());
                });
    }

    public Task<Food> readFood(String id) throws FBCException {

        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<Food> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento del alimento
        fst.collection("alimentos").document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convertir el documento a un objeto Alimento
                        Food food = documentSnapshot.toObject(Food.class);
                        if (food != null) {
                            Log.d("Firebase", "📖 Alimento leído: " + food);
                            taskCompletionSource.setResult(food);
                        }
                    } else {
                        Log.d("Firebase", "⚠️ Alimento no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("Alimento no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer Alimento: " + e.getMessage());
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public void saveDietFood(FoodDiet dietFood) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        // Crear un objeto Map con los datos de la DietFood
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("idDieta", dietFood.getIdDieta());
        foodData.put("idAlimento", dietFood.getIdAlimento());
        foodData.put("comida", dietFood.getComida());
        foodData.put("numeroPlato", dietFood.getNumeroPlato());
        foodData.put("dia", dietFood.getDia());
        foodData.put("nombreReceta", dietFood.getNombreReceta());

        // Guardar en Firestore en la colección "diet_food"
        fst.collection("diet_food").document(dietFood.getIdDieta() + "_" + dietFood.getIdAlimento())
                .set(foodData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ DietFood guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar DietFood: " + e.getMessage());
                });
    }

    public Task<FoodDiet> readDietFood(String idDieta, String idAlimento) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        final TaskCompletionSource<FoodDiet> taskCompletionSource = new TaskCompletionSource<>();

        // Referencia al documento en la colección "diet_food"
        fst.collection("diet_food").document(idDieta + "_" + idAlimento).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        FoodDiet dietFood = documentSnapshot.toObject(FoodDiet.class);
                        if (dietFood != null) {
                            Log.d("Firebase", "📖 DietFood leído: " + dietFood);
                            taskCompletionSource.setResult(dietFood);
                        }
                    } else {
                        Log.d("Firebase", "⚠️ DietFood no encontrado en Firestore");
                        taskCompletionSource.setException(new Exception("DietFood no encontrado"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer DietFood: " + e.getMessage());
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

    public <T> Task<List<T>> readAllFromCollection(String collectionName, Class<T> clazz) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        TaskCompletionSource<List<T>> taskCompletionSource = new TaskCompletionSource<>();

        fst.collection(collectionName).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<T> itemList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        T item = document.toObject(clazz);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                    Log.d("Firebase", "📖 Se leyeron " + itemList.size() + " documentos de " + collectionName);
                    taskCompletionSource.setResult(itemList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }

/*
    Ejemplo de uso de metodos Task
        FirestoreHelper firestoreHelper = new FirestoreHelper(FirebaseFirestore.getInstance());

            firestoreHelper.readAllFromCollection("diet_food", DietFood.class)
                    .addOnSuccessListener(dietFoods -> {
                        for (DietFood food : dietFoods) {
                            Log.d("Firebase", "🍽️ DietFood: " + food);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "❌ Error al obtener los DietFood", e));
*/

/*
    🔹 ¿Qué es la clase Task en Firebase?
            La clase Task<T> de Firebase representa una tarea asincrónica que devuelve un resultado de tipo T o un error si la tarea
            falla. Se usa para manejar operaciones en segundo plano sin bloquear el hilo principal.

        🔹 ¿Para qué se usa?
            En Firestore, Task se usa para leer, escribir y consultar datos sin que la app se congele.

        🔹 ¿Cómo funciona?
            Cuando llamas a un métdo que devuelve un Task<T>, la operación no se ejecuta inmediatamente. En su lugar, puedes usar listeners
            (addOnSuccessListener, addOnFailureListener) para ejecutar código cuando la tarea finaliza.
*/


    public DatabaseReference getRef() {
        return ref;
    }

    public FirebaseDatabase getBd() {
        return bd;
    }
}
