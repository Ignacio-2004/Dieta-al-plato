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
import com.tfg.dietaalplato.utilities.exception.ClassUtilities;
import com.tfg.dietaalplato.utilities.exception.FBCException;


import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


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
    private FirebaseFirestore fst;
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


    private void save(User user) throws FBCException {


        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> usuario = new HashMap<>();
        usuario.put("id", user.getId());
        usuario.put("user", user.getName());
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


    /**
     * Metodo que lee un usario desde su nombre
     * @param name nombre del usuario
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readUser(String name, OnResultCallBack<ObjectResult<User>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("usuarios")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        User usuario = document.toObject(User.class);
                        if (usuario != null) {
                            Log.d("Firebase", "📖 Usuario encontrado: " + usuario.getName() + ", Password: " + usuario.getPsw());
                            callback.onResult(new ObjectResult<>(true, "success", usuario));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a User");
                            callback.onResult(new ObjectResult<>(false, "Error al convertir el documento a usuario", null));
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún usuario con el nombre: " + name);
                        callback.onResult(new ObjectResult<>(false, "Usuario no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar usuario por nombre: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar usuario: " + e.getMessage(), null));
                });
    }

    public void delete(String id, OnResultCallBack<ValidationResult> callback) {
        if (fst == null) {
            callback.onResult(new ValidationResult(false, "Firestore no inicializado", null));
            return;
        }

        String prefix = id.substring(0, 3);
        String collection = null;
        String successMsg = "Elemento eliminado correctamente";
        String errorMsg = "Error al eliminar el elemento";

        switch (prefix) {
            case "USU":
                collection = "usuarios";
                successMsg = "Usuario eliminado correctamente";
                errorMsg = "Error al eliminar el usuario";
                break;
            case "CLI":
                collection = "clientes";
                successMsg = "Cliente eliminado correctamente";
                errorMsg = "Error al eliminar el cliente";
                break;
            case "DIE":
                collection = "dietas";
                successMsg = "Dieta eliminada correctamente";
                errorMsg = "Error al eliminar la dieta";
                break;
            case "FDI":
                collection = "dietaAlimentos";
                successMsg = "Alimento eliminado correctamente";
                errorMsg = "Error al eliminar el alimento";
                break;
            case "ALI":
                collection = "comidas";
                successMsg = "Alimento eliminado correctamente";
                errorMsg = "Error al eliminar el alimento";
                break;
            default:
                callback.onResult(new ValidationResult(false, "Tipo de documento no soportado", null));
                return;
        }

        String finalSuccessMsg = successMsg;
        String finalErrorMsg = errorMsg;
        fst.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid ->
                        callback.onResult(new ValidationResult(true, finalSuccessMsg, null))
                ).addOnFailureListener(e ->
                        callback.onResult(new ValidationResult(false, finalErrorMsg, null))
                );
    }



    /**
     * Metrodo que lee una comida de un usuario desde su nombre
     * @param name nombre de la comida
     * @param idUser id del usuario al que le pertenece la comida
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readFood(String name, String idUser, OnResultCallBack<ObjectResult<Food>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("comidas")
                .whereEqualTo("name", name).whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        Food food = document.toObject(Food.class);
                        if (food != null) {
                            Log.d("Firebase", "📖 Comida encontrada: " + food.getName());
                            callback.onResult(new ObjectResult<>(true, "success", food));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Food");
                            callback.onResult(new ObjectResult<>(false, "Error al convertir el documento a comida", null));
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna comida con el nombre: " + name);
                        callback.onResult(new ObjectResult<>(false, "Comida no encontrada", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la comida por nombre: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar la comida: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee todas las comida de un usuario
     * @param idUser id del usuario al que le pertenece las comidas
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readAllFoodFromUser(String idUser, OnResultCallBack<ObjectResult<ArrayList<Food>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("comidas")
                .whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        ArrayList<Food> foods = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Food food = document.toObject(Food.class);
                            if (food != null) {
                                Log.d("Firebase", "📖 Comida encontrada: " + food.getName());
                                foods.add(food);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Food");
                            }
                        }


                        if (!foods.isEmpty()) {
                            callback.onResult(new ObjectResult<>(true, "success", foods));
                        }else{
                            callback.onResult(new ObjectResult<>(false, "No se encontraron comidas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna comida que pertenezca al usuario: " +idUser);
                        callback.onResult(new ObjectResult<>(false, "Comida no encontrada", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la comida por nombre: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar la comida: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee las dietas de un cliente
     * @param idCli cleinte al que pertenecen las dietas
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readDietFromUser(String idCli, OnResultCallBack<ObjectResult<ArrayList<Diet>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("dietas")
                .whereEqualTo("idCliente", idCli)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        ArrayList<Diet> diets = new ArrayList<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Diet diet = document.toObject(Diet.class);
                            if (diet != null) {
                                Log.d("Firebase", "📖 Dieta encontrada: " + diet.getName());
                                diets.add(diet);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Dieta");
                            }
                        }


                        if (!diets.isEmpty()){
                            callback.onResult(new ObjectResult<>(true, "success", diets));
                        }else{
                            callback.onResult(new ObjectResult<>(false, "No se encontraron dietas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna dieta para el cliente con id: " + idCli);
                        callback.onResult(new ObjectResult<>(false, "Dieta no encontrada", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la dieta por el cliente: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar la dieta: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee las dietas de un cliente a partir de un nombre
     * @param idCli id del cliente
     * @param name nombre de la dieta
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readDietByMame(String idCli,String name, OnResultCallBack<ObjectResult<ArrayList<Diet>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("dietas")
                .whereEqualTo("idCliente", idCli)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        ArrayList<Diet> diets = new ArrayList<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Diet diet = document.toObject(Diet.class);
                            if (diet != null) {
                                Log.d("Firebase", "📖 Dieta encontrada: " + diet.getName());
                                diets.add(diet);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Dieta");
                            }
                        }


                        if (!diets.isEmpty()){
                            callback.onResult(new ObjectResult<>(true, "success", diets));
                        }else{
                            callback.onResult(new ObjectResult<>(false, "No se encontraron dietas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna dieta para el cliente con id: " + idCli);
                        callback.onResult(new ObjectResult<>(false, "Dieta no encontrada", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la dieta por el cliente: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar la dieta: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee un cliente a partir de su nombre y apellido
     * @param name nombre del cliente
     * @param ape apellido del cliente
     * @param idUsr id del usuario al que pertenece el cliente
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readClient(String name, String ape, String idUsr, OnResultCallBack<ObjectResult<Client>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("clientes")
                .whereEqualTo("name", name)
                .whereEqualTo("ape", ape)
                .whereEqualTo("idUsr", idUsr)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        Client client = document.toObject(Client.class);
                        if (client != null) {
                            Log.d("Firebase", "📖 Cliente encontrada: " + client.getName());
                            callback.onResult(new ObjectResult<>(true, "success", client));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Cliente");
                            callback.onResult(new ObjectResult<>(false, "Error al convertir el documento a cliente", null));
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún cliente con nombre: " + name +" "+ ape+" y que pertenezca al usuario: "+idUsr);
                        callback.onResult(new ObjectResult<>(false, "Cliente no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el cliente: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar el cliente: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee todos los clientes a partir de su id de usuario
     * @param idUsr id del usuario
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readClientFromUser(String idUsr, OnResultCallBack<ObjectResult<ArrayList<Client>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("cliente")
                .whereEqualTo("idUsr", idUsr)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        ArrayList<Client> clients = new ArrayList<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Client client = document.toObject(Client.class);
                            if (client != null) {
                                Log.d("Firebase", "📖 Cliente encontrado: " + client.getName());
                                clients.add(client);
                            }else{
                                Log.d("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Cliente");
                            }
                        }


                        if (!clients.isEmpty()){
                            callback.onResult(new ObjectResult<>(true, "success", clients));
                        }else{
                            callback.onResult(new ObjectResult<>(false, "No se encontraron clientes", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún cliente que pertenezca al usuario: "+idUsr);
                        callback.onResult(new ObjectResult<>(false, "Cliente no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el cliente: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar el cliente: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee una comidaDiet a partir de su id de dieta y de alimento
     * @param idDieta id de la dieta
     * @param idAlimento id del alimento
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readFoodDiet(String idDieta, String idAlimento, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .whereEqualTo("idAlimento", idAlimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        ArrayList<FoodDiet> foods = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            FoodDiet food = document.toObject(FoodDiet.class);
                            if (food != null) {
                                Log.d("Firebase", "📖 FoodDiet encontrada: " + food.getIdAlimento());
                                foods.add(food);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                            }
                        }

                        callback.onResult(new ObjectResult<>(true, "success", foods));

                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún foodDiet con idDieta: " + idDieta +" y con idAlimento: "+idAlimento);
                        callback.onResult(new ObjectResult<>(false, "FoodDiet no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el FoodDiet: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar el FoodDiet: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee todas las comidasde una dieta a partir de su id de dieta
     * @param idDieta id de la dieta
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readFoodDietByDiet(String idDieta, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        ArrayList<FoodDiet> foods = new ArrayList<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            FoodDiet food = document.toObject(FoodDiet.class);
                            if (food != null) {
                                Log.d("Firebase", "📖 FoodDiet encontrada: " + food.getIdAlimento());
                                foods.add(food);
                            }
                            else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                                callback.onResult(new ObjectResult<>(false, "Error al convertir el documento a FoodDiet", null));
                            }
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún foodDiet con idDieta: " + idDieta );
                        callback.onResult(new ObjectResult<>(false, "FoodDiet no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el FoodDiet: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar el FoodDiet: " + e.getMessage(), null));
                });
    }


    /**
     * Metodo que lee todas las comidas de una dieta a partir de su id de dieta y de dia
     * @param idDieta id de la dieta
     * @param dia  dia de la dieta
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public void readFoodDietByDay(String idDieta, String dia, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .whereEqualTo("dia", dia)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {


                        ArrayList<FoodDiet> foods = new ArrayList<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            FoodDiet food = document.toObject(FoodDiet.class);
                            if (food != null) {
                                Log.d("Firebase", "📖 FoodDiet encontrada: " + food.getIdAlimento());
                                foods.add(food);
                            }
                            else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                                callback.onResult(new ObjectResult<>(false, "Error al convertir el documento a FoodDiet", null));
                            }
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún foodDiet con idDieta: " + idDieta );
                        callback.onResult(new ObjectResult<>(false, "FoodDiet no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el FoodDiet: " + e.getMessage());
                    callback.onResult(new ObjectResult<>(false, "Error al buscar el FoodDiet: " + e.getMessage(), null));
                });
    }


//--IP - 27/04/2025 -


    /**
     * ALEX-----------
     * Metodo para ver si existe el usuario pasando su correo
     * @param email
     * @return devuelve el usuario
     * @throws FBCException
     */
    public Task<User> readUserByEmail(String email) throws FBCException {


        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        final TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();


        // vamos la coleccion de usuarios, buscamos por el campo "user" que cogemos el correo
        fst.collection("usuarios")
                .whereEqualTo("name", email) //buscamos el usario por email
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {


                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        User usuario = document.toObject(User.class);


                        if (usuario != null) {
                            Log.d("Firebase", "📖 Usuario leído: " + usuario.getName() + ", Password: " + usuario.getPsw());
                            taskCompletionSource.setResult(usuario);  // Devolver el objeto User
                        } else {
                            Log.d("Firebase", "⚠️ El documento no pudo convertirse a User");
                            taskCompletionSource.setException(new Exception("Error al convertir documento a usuario"));
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




    private void save(Client cli) throws FBCException {


        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        // Crear un objeto Map con los datos del usuario
        HashMap<Object, Object> client = new HashMap<>();
        client.put("id", cli.getId());
        client.put("cli", cli.getName());
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


    private  void save(Diet diet) throws FBCException {


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


    private void save(Food alimento) throws FBCException {


        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


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


    private void save(FoodDiet dietFood) throws FBCException {
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
        fst.collection("dietaAlimentos").document(dietFood.getIdDieta() + "_" + dietFood.getIdAlimento())
                .set(foodData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "✅ DietFood guardado con éxito en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al guardar DietFood: " + e.getMessage());
                });
    }


    private Task<FoodDiet> readDietFood(String idDieta, String idAlimento) throws FBCException {
        if (fst == null) {
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }


        final TaskCompletionSource<FoodDiet> taskCompletionSource = new TaskCompletionSource<>();


        // Referencia al documento en la colección "diet_food"
        fst.collection("dietaAlimentos").document(idDieta + "_" + idAlimento).get()
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








    public DatabaseReference getRef() {
        return ref;
    }


    public FirebaseDatabase getBd() {
        return bd;
    }


//++IP - 23/04/2025 -
    public <T> ValidationResult saveData(Class<T> classType, ValidationResult result) {

        ClassData classData;
        final String TAG = "SaveData";


        String id;
        try {

            Log.d(TAG, "💾 Guardando datos...");

            if (!result.exit) {
                Log.w(TAG, "❌ Error al validar los datos: " + result.message);
                throw new FBCException(result.message);
            }

            classData = ClassUtilities.collectionData(classType);

            //Compruebo si ya hay guardado un objeto con el mismo nombre

            switch (classData.key) {
                case "USU":
                case "DIE":
                    repeatObject(result, classData.data, (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            result.exit = false;
                            result.message = validationResult.message;
                        }
                    });

                    break;
                case "ALI":
                case "CLI":
                    repeatObject(result, classData.data, result.data.get("idUsr"), (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            result.exit = false;
                            result.message = validationResult.message;
                        }
                    });

                    break;
                case "FDI":
                    repeatObject(result, classData.data, result.data.get("idDieta"), result.data.get("idAlimento"), (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            result.exit = false;
                            result.message = validationResult.message;
                        }
                    });

                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new FBCException("Tipo de dato no soportado");

            }

            if (!result.exit) {
                Log.w(TAG, "Ya existe un objeto con los mismos credenciales: " + result.message);
                return new ValidationResult(false, result.message, result.data);
            }

            id = ClassUtilities.generateId(classData, result.message.toString().trim());

            switch (classData.data) {
                case "usuarios":
                    save(new User(id, result.data.get("name"), result.data.get("psw")));
                    break;
                case "clientes":
                    save(new Client(id, result.data.get("name"), result.data.get("ape"), result.data.get("idUsr")));
                    break;
                case "dietas":
                    save(new Diet(id, result.data.get("tip"), result.data.get("idClient"), result.data.get("just"), result.data.get("idUsr")));
                    break;
                case "alimentos":
                    save(new Food(id, result.data.get("name"), result.data.get("idUsr"), result.data.get("pc"), result.data.get("energia"),
                            result.data.get("proteina"), result.data.get("grasa"), result.data.get("ags"), result.data.get("agmi"),
                            result.data.get("agpi"), result.data.get("colesterol"), result.data.get("hc"), result.data.get("fibra"), result.data.get("vitC"),
                            result.data.get("vitB6"), result.data.get("vitE"), result.data.get("hierro"), result.data.get("sodio"), result.data.get("calcio"),
                            result.data.get("potasio")));
                    break;
                case "dietaAlimentos":
                    save(new FoodDiet(id, result.data.get("idDieta"), result.data.get("idAlimento"), result.data.get("comida"),
                            result.data.get("numeroPlato"), result.data.get("dia"), result.data.get("nombreReceta"), result.data.get("idUsr")));
                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new FBCException("Tipo de dato no soportado");
            }

            Log.d(TAG, "✅ Datos guardados correctamente. ID: " + id);
            Log.d(TAG, "📂 Tipo de colección: " + classData.data);
            Log.d(TAG, "🏁 Fin del proceso");
            return new ValidationResult(true, "success", result.data);

        } catch (FBCException e) {
            Log.e(TAG, "❌ Error al guardar los datos: " + e.getMessage());
            Log.d(TAG, "🏁 Fin del proceso");
            return new ValidationResult(false, e.getMessage(), result.data);
        }
    }

    public <T> ValidationResult saveData(Class<T> classType, ValidationResult result, boolean force) throws FBCException {

        final String TAG = "SaveDataForce";

        /*
         * Si es false llamo al original que filtra los repetidos
         */
        if (!force){
            Log.d(TAG, "force = false, derivando al metodo principal ...");
            return  saveData(classType, result);
        }

        String collectionName;
        ClassData classData;
        AtomicReference<ValidationResult> deleteResult = new AtomicReference<>(new ValidationResult());

        try{
            Log.d(TAG, "💾 Guardando datos...");

            if (!result.exit) {
                Log.w(TAG, "❌ Error al validar los datos: " + result.message);
                throw new FBCException(result.message);
            }

            classData = ClassUtilities.collectionData(classType);
            //Compruebo si ya hay guardado un objeto con el mismo nombre

            switch (classData.key) {
                case "USU":
                case "DIE":
                    repeatObject(result, classData.data, (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            delete(validationResult.data.get("id"), (ValidationResult validationResult1) -> {
                                deleteResult.set(validationResult1);
                            });
                        }
                    });

                    break;
                case "ALI":
                case "CLI":
                    repeatObject(result, classData.data, result.data.get("idUsr"), (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            delete(validationResult.data.get("id"), (ValidationResult validationResult1) -> {
                                deleteResult.set(validationResult1);
                            });
                        }
                    });

                    break;
                case "FDI":
                    repeatObject(result, classData.data, result.data.get("idDieta"), result.data.get("idAlimento"), (ValidationResult validationResult) -> {
                        if (!validationResult.exit) {
                            delete(validationResult.data.get("id"), (ValidationResult validationResult1) -> {
                                deleteResult.set(validationResult1);
                            });
                        }
                    });

                    break;
                default:
                    Log.e(TAG, "❌ Tipo de dato no soportado");
                    throw new FBCException("Tipo de dato no soportado");
            }

        }catch (FBCException e){
            return new ValidationResult(false, e.getMessage(), result.data);
        }

        if (deleteResult.get().message == null){
            Log.d(TAG, "🔍 No había objeto duplicado para eliminar. Guardando sin eliminar.");
            Log.d(TAG, "🏁 Fin del proceso forzado");
            return saveData(classType, result);
        }else if(deleteResult.get().exit){
            Log.d(TAG, "✅ Objeto repetido eliminado");
            Log.d(TAG, "🏁 Fin del proceso forzado, redirigiendo al principal ...");
            return saveData(classType, result);
        }else{
            Log.d(TAG, "❌ Error al eliminar el objeto repetido");
            Log.d(TAG, "🏁 Fin del proceso forzado");
            return deleteResult.get();
        }
    }

    private void repeatObject(ValidationResult result, String collectionName,OnResultCallBack<ValidationResult> callback) throws FBCException {

        switch (collectionName){
            case "usuarios":

                readAllFromCollection(collectionName, User.class).addOnSuccessListener(
                                users -> {
                                    for (User user : users) {
                                        if (user.getName().equals(result.data.get("name"))) {
                                            Log.e("Firebase", user.getId());
                                            callback.onResult(new ValidationResult(false, "El usuario ya existe", result.data));
                                            return;
                                        }
                                    }

                                    callback.onResult(new ValidationResult(true, String.valueOf(users.size()), result.data));
                                    return;

                                }
                        )
                        .addOnFailureListener(
                                e ->{
                                    Log.e("Firebase", "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                                    callback.onResult(new ValidationResult(false, "Error al leer la colección " + collectionName + ": " + e.getMessage(), result.data));
                                    return;
                                }
                        );
                break;
            default:
                Log.d("Firebase", "❌ Tipo de dato no soportado");
        }

    }

    private void repeatObject(ValidationResult result, String collectionName,String idExt,OnResultCallBack<ValidationResult> callback) throws FBCException {

        switch (collectionName){
            case "clientes":

                readClientFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        callback.onResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Client client : result1.result) {
                        if (client.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", client.getId());
                            callback.onResult(new ValidationResult(false, "El cliente ya existe", result.data));
                            return;
                        }
                    }

                    callback.onResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
                break;
            case "alimentos":
                readAllFoodFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        callback.onResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Food food : result1.result) {
                        if (food.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", food.getId());
                            callback.onResult(new ValidationResult(false, "El alimento ya existe", result.data));
                            return;
                        }
                    }

                    callback.onResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
            case "dietas":
                readDietFromUser(idExt, result1 -> {
                    if (!result1.exit) {
                        callback.onResult(new ValidationResult(false, result1.message, result.data));
                        return;
                    }

                    for (Diet diet : result1.result) {
                        if (diet.getName().equals(result.data.get("name"))) {
                            Log.e("Firebase", diet.getId());
                            callback.onResult(new ValidationResult(false, "La dieta ya existe", result.data));
                            return;
                        }
                    }

                    callback.onResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                    return;
                });
            default:
                Log.d("Firebase", "❌ Tipo de dato no soportado");
        }

    }

    private void repeatObject(ValidationResult result, String collectionName,String idDiet, String idFood,OnResultCallBack<ValidationResult> callback) throws FBCException {

        switch (collectionName){
            case "dietaAlimentos":

                readFoodDiet(idDiet, idFood,result1 -> {
                    if (!result1.exit) {
                        for (FoodDiet foodDiet : result1.result){
                            if (foodDiet.getComida().equals(result.data.get("comida")) &&
                                foodDiet.getNumeroPlato().equals(result.data.get("numeroPlato"))&&
                                foodDiet.getDia().equals(result.data.get("dia")) &&
                                foodDiet.getNombreReceta().equals(result.data.get("nombreReceta"))){

                                Log.e("Firebase", foodDiet.getId());
                                callback.onResult(new ValidationResult(false, "El alimento-receta ya existe", result.data));
                                return;
                            }
                        }

                        callback.onResult(new ValidationResult(true, String.valueOf(result1.result.size()), result.data));
                        return;
                    }
                });
                break;
            default:
                Log.d("Firebase", "❌ Tipo de dato no soportado");
                callback.onResult(new ValidationResult(false, "Tipo de dato no soportado", result.data));
        }

    }


//--IP - 23/04/2025 -
//++IP - 25/04/2025 -

/*private<T> ObjectRersult idUsrComparison(Class<T> classType, ValidationResult result,OnResultCallBack callback){

}*/

//--IP - 25/04/2025 -


//--IP - 25/04/2025 -


}
