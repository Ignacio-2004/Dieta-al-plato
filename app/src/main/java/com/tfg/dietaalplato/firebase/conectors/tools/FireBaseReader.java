package com.tfg.dietaalplato.firebase.conectors.tools;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class FireBaseReader {
    private static final String TAG = "FireBase/Reader";

    /**
     * Metodo que lee un usario desde su nombre
     * @param name nombre del usuario
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static void readUser(String name, OnResultCallBack<ObjectResult<User>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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

    /**
     * Metrodo que lee una comida de un usuario desde su nombre
     * @param name nombre de la comida
     * @param idUser id del usuario al que le pertenece la comida
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static void readFood(String name, String idUser, OnResultCallBack<ObjectResult<Food>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


        fst.collection(String.valueOf(TablesNames.clientes))
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
     * @param idUser id del usuario al que le pertenece las comida
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<ArrayList<Food>>> readAllFoodFromUser(String idUser) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();

        TaskCompletionSource<ObjectResult<ArrayList<Food>>> taskCompletionSource = new TaskCompletionSource<>();
        fst.collection(String.valueOf(TablesNames.alimentos))
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
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", foods));
                        }else{
                            taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron comidas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna comida que pertenezca al usuario: " +idUser);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Comida no encontrada", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la comida por nombre: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE( new ObjectResult<>(false, "Error al buscar la comida: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee las dietas de un cliente
     * @param idCli cleinte al que pertenecen las dietas
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<ArrayList<Diet>>> readDietFromUser(String idCli) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();

        TaskCompletionSource<ObjectResult<ArrayList<Diet>>> taskCompletionSource = new TaskCompletionSource<>();
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
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", diets));
                        }else{
                            taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron dietas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna dieta para el cliente con id: " + idCli);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Dieta no encontrada", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la dieta por el cliente: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar la dieta: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee las dietas de un cliente a partir de un nombre
     * @param idCli id del cliente
     * @param name nombre de la dieta
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static void readDietByMame(String idCli,String name, OnResultCallBack<ObjectResult<ArrayList<Diet>>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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
    public static void readClient(String name, String ape, String idUsr, OnResultCallBack<ObjectResult<Client>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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

     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<ArrayList<Client>>> readClientFromUser(String idUsr) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();

        TaskCompletionSource<ObjectResult<ArrayList<Client>>> taskCompletionSource = new TaskCompletionSource<>();

        fst.collection("clientes")
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
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", clients));
                        }else{
                            taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron clientes", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún cliente que pertenezca al usuario: "+idUsr);
                        taskCompletionSource.setException(new ComplexFBCE( new ObjectResult<>(false, "Cliente no encontrado", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el cliente: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE( new ObjectResult<>(false, "Error al buscar el cliente: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee una comidaDiet a partir de su id de dieta y de alimento
     * @param idDieta id de la dieta
     * @param idAlimento id del alimento
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static void readFoodDiet(String idDieta, String idAlimento, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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
    public static void readFoodDietByDiet(String idDieta, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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
    public static void readFoodDietByDay(String idDieta, String dia, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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


    /**
     * Metodo que lee un usuario a partir de su correo
     * @param email correo del usuario
     * @return objeto User
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<User> readUserByEmail(String email) throws FBCException {


        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


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

    /**
     * Metodo que lee una relacion dieta-comida a partir del id de la dieta y el de la comida
     * @param idDieta  id de la dieta
     * @param idAlimento id de la comida
     * @return objeto FoodDiet
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    private static Task<FoodDiet> readDietFood(String idDieta, String idAlimento) throws FBCException {

        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();

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

    /**
     * Metodo que lee todas una coleccion
     * @param collectionName  nombre de la coleccion
     * @param clazz  clase que convierte el objeto
     * @return lista de objetos
     * @param <T> tipo de objeto
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static <T> Task<List<T>> readAllFromCollection(String collectionName, Class<T> clazz) throws FBCException {

        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();


        TaskCompletionSource<List<T>> taskCompletionSource = new TaskCompletionSource<>();

        Log.d("Firebase", "📂 Leyendo colección: " + collectionName);

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
                    return;
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al leer la colección " + collectionName + ": " + e.getMessage());
                    taskCompletionSource.setException(e);
                    return;
                });
        return taskCompletionSource.getTask();
    }


}
