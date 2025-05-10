package com.tfg.dietaalplato.firebase.conectors;


import android.util.Log;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.*;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.firebase.tables.parents.BaseObject;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;


import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


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


 public class FireBaseConnector {


    private static FireBaseConnector instance;


    private DatabaseReference ref;
    private FirebaseDatabase bd;
    private static FirebaseFirestore fst;
    private static final String TAG = "Firebase";


    public FireBaseConnector() {
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

    public static FirebaseFirestore getFirestore() {
        return fst;
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





    /**
     * ALEX-----------
     * comprobamos si existe el usuario pasando correo
     * @param email
     * @return true / false
     * @throws FBCException
     */
    public Task<Boolean> verifyUser(String email) throws FBCException {
        if (fst == null) throw new FBCException("La instancia de Firestore no puede ser nula");


        TaskCompletionSource<Boolean> tcs = new TaskCompletionSource<>();
        // vamos la coleccion de usuarios, buscamos por el campo "user" que cogemos el correo
        fst.collection("usuarios")
                .whereEqualTo("name", email) //buscamos el usario por email
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean exists = !queryDocumentSnapshots.isEmpty();
                    tcs.setResult(exists);
                })
                .addOnFailureListener(tcs::setException);


        return tcs.getTask();
    }


//++IP - 27/04/2025 -








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







    public DatabaseReference getRef() {
        return ref;
    }


    public FirebaseDatabase getBd() {
        return bd;
    }












//--IP - 23/04/2025 -


}
