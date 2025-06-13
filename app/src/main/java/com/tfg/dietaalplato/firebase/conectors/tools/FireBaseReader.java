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
import com.tfg.dietaalplato.firebase.tables.RelacionRecetaAlimento;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.utilities.ObjectResult;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.tipe_collection.CacheCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseReader {
    private static final String TAG = "FireBase/Reader";

    /**
     * Metodo que lee un usario desde su nombre (Fill)
     * @param name nombre del usuario
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<User>> readUser(String name) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<User>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        /*Comprobar que no haya en el cache*/
        if (saveData.getUser() != null && saveData.getUser().getName().equals(name)){
            Log.d(TAG,"📖 Usuario encontrado en cache: " + saveData.getUser().getName() + ", Password: " + saveData.getUser().getPsw());
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getUser()));
            return taskCompletionSource.getTask();
        }

        fst.collection("usuarios")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        User usuario = document.toObject(User.class);
                        if (usuario != null) {
                            /*Cache collection*/
                            saveData.setUser(usuario);

                            if (saveData.getUser()!=new User("","","")|| saveData.getUser()==null){
                                Log.d(TAG, "📖 Usuario guardado en cache: " + saveData.getUser().getName() + ", Password: " + saveData.getUser().getPsw());
                            }

                            Log.d(TAG, "📖 Usuario encontrado: " + usuario.getName() + ", Password: " + usuario.getPsw());
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", usuario));
                        } else {
                            Log.e(TAG, "⚠️ Documento encontrado pero no se pudo convertir a User");
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al convertir el documento a usuario", null)));
                        }
                    } else {
                        Log.d(TAG, "⚠️ No se encontró ningún usuario con el nombre: " + name);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Usuario no encontrado", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al buscar usuario por nombre: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar usuario: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }

    /**
     * Metrodo que lee una comida de un usuario desde su nombre
     * @param name nombre de la comida
     * @param idUser id del usuario al que le pertenece la comida
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Food>> readFood(String name, String idUser) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<Food>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        if (saveData.getFoods().isLoaded() && saveData.getFoods().contains(name)){
            Log.d(TAG, "📖 Comida encontrada en cache: " + name);
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getFoods().get(name)));
            return taskCompletionSource.getTask();
        };


        fst.collection(String.valueOf(TablesNames.clientes))
                .whereEqualTo("name", name).whereEqualTo("idUser", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        Food food = document.toObject(Food.class);
                        if (food != null) {
                            Log.d("Firebase", "📖 Comida encontrada: " + food.getName());
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", food));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Food");
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al convertir el documento a comida", null)));
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna comida con el nombre: " + name);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Comida no encontrada", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la comida por nombre: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar la comida: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee todas las comida de un usuario (Fill)
     * @param idUser id del usuario al que le pertenece las comida
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Map<String,Food>>> readAllFoodFromUser(String idUser) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();
        TaskCompletionSource<ObjectResult<Map<String,Food>>> taskCompletionSource = new TaskCompletionSource<>();

        if (saveData.getFoods().isLoaded()){
            Log.d(TAG, "📖 Comidas encontradas en cache");
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getFoods().getCollection()));
            return taskCompletionSource.getTask();
        };

        fst.collection("alimentos")
                .whereEqualTo("idUsr", idUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "📖 Comidas encontradas");
                        Map<String,Food> foods = new HashMap<>();
                        CacheCollection<Food> foodsCache = new CacheCollection<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Food food = document.toObject(Food.class);
                            Log.d(TAG, "📖 Comida encontrada: " + food.getIdUsr());
                            if (food != null) {
                                Log.d("Firebase", "📖 Comida encontrada: " + food.getName());
                                foods.put(food.getName(),food);
                                foodsCache.add(food.getName(), food);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Food");
                            }
                        }


                        if (!foods.isEmpty()) {
                            foodsCache.setLoaded(true);
                            saveData.setCollectionFood(foodsCache);

                            if ((saveData.getFoods()!= null && !saveData.getFoods().isEmpty()) && saveData.getFoods().isLoaded()){
                                Log.d(TAG, "📖 Comidas guardadas en cache");
                            }

                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", foods));
                        }else{
                            taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron comidas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna comida que pertenezca al usuario: " +idUser);
                        taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron comidas", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar la comida por nombre: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE( new ObjectResult<>(false, "Error al buscar la comida: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee las dietas de un cliente (Fill)
     * @param idCli cleinte al que pertenecen las dietas
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Map<String,Diet>>> readDietFromClient(String idCli) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();
        TaskCompletionSource<ObjectResult<Map<String,Diet>>> taskCompletionSource = new TaskCompletionSource<>();

        /*
         * Coimprobamos que este cargado y si tiene la clave del cliente
         */
        if (saveData.getDiets().isLoaded()&&saveData.getDiets().contains(idCli)){
            Log.d(TAG, "📖 Dietas encontradas en cache");
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getDietsOfClient(idCli)));
            return taskCompletionSource.getTask();
        };

        fst.collection("dietas")
                .whereEqualTo("idCli", idCli.toUpperCase())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        CacheCollection<Map<String,Diet>> dietsCache = new CacheCollection<>();
                        Map<String, Diet> diets = new HashMap<>();



                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Diet diet = document.toObject(Diet.class);
                            if (diet != null) {
                                Log.d("Firebase", "📖 Dieta encontrada: " + diet.getName());
                                diets.put(diet.getName(),diet);
                            }else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Dieta");
                            }
                        }


                        if (!diets.isEmpty()){
                            dietsCache.setLoaded(true);
                            dietsCache.add(idCli.toUpperCase(),diets);
                            saveData.setCollectionDiet(dietsCache);

                            if (saveData.getDiets().isLoaded()){
                                Log.d(TAG, "📖 Dietas guardadas en cache");
                            }

                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", diets));
                        }else{
                            taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron dietas", null));
                        }


                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningúna dieta para el cliente con id: " + idCli);
                        taskCompletionSource.setResult(new ObjectResult<>(false, "Dieta no encontrada", null));
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
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Diet>> readDietByName(String idCli,String name) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<Diet>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        if (saveData.getDiets().contains(idCli)){
            Log.d(TAG, "📖 Dieta encontrada en cache");
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getDiet(idCli,name)));
            return taskCompletionSource.getTask();
        };

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


                        if (diets.size()==1){
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", diets.get(0)));
                        }else{
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Dieta no encontrada", null)));
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
     * Metodo que lee un cliente a partir de su nombre y apellido
     * @param name nombre del cliente
     * @param ape apellido del cliente
     * @param idUsr id del usuario al que pertenece el cliente
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Client>> readClient(String name, String ape, String idUsr) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<Client>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        if (saveData.getClients().isLoaded()&&saveData.getClients().contains(name+" "+ape)){
            Log.d(TAG, "📖 Cliente encontrado en cache");
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getClients().get(name+" "+ape)));
            return taskCompletionSource.getTask();
        };

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
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", client));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Cliente");
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al convertir el documento a cliente", null)));
                        }
                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún cliente con nombre: " + name +" "+ ape+" y que pertenezca al usuario: "+idUsr);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Cliente no encontrado", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el cliente: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar el cliente: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee todos los clientes a partir de su id de usuario (Fill)
     * @param idUsr id del usuario

     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<ArrayList<Client>>> readClientFromUser(String idUsr) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();
        TaskCompletionSource<ObjectResult<ArrayList<Client>>> taskCompletionSource = new TaskCompletionSource<>();

        if (saveData.getClients().isLoaded()){
            Log.d("CurrentStudent", "📖 Clientes encontrados en cache: " + idUsr);

            ArrayList<Client> clientes = saveData.getClients().getAllAsArrayList();
            if (!clientes.isEmpty()) {
                Log.d(TAG, "✅ Resultado exitoso antes del return:");
                Log.d(TAG, "| → Estado (exit): true");
                Log.d(TAG, "| → Mensaje: Clientes cargados");
                Log.d(TAG, "| → Número total de clientes: " + clientes.size());

                for (int i = 0; i < clientes.size(); i++) {
                    Client c = clientes.get(i);
                    Log.d(TAG, "|    Cliente[" + i + "]: { id: " + c.getId() + ", nombre: " + c.getName() + ", idUsr: " + c.getIdUsr() + "minKcal: " + c.getMinKal() + ", maxKcal: " + c.getMaxKal()  +" }");
                }

                taskCompletionSource.setResult(new ObjectResult<>(true, "Clientes cargados", clientes));
                return taskCompletionSource.getTask();
            } else {
                Log.d(TAG, "🟡 Resultado vacío antes del return:");
                Log.d(TAG, "| → Estado (exit): false");
                Log.d(TAG, "| → Mensaje: No hay clientes disponibles");
                Log.d(TAG, "| → Número total de clientes: 0");
            }

            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getClients().getAllAsArrayList()));
        }else{
            fst.collection("clientes")
                    .whereEqualTo("idUsr", idUsr)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            CacheCollection<Client> clientsCache = new CacheCollection<>();
                            ArrayList<Client> clients = new ArrayList<>();


                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Client client = document.toObject(Client.class);
                                if (client != null) {
                                    Log.d("Firebase", "📖 Cliente encontrado: " + client.getName());
                                    clients.add(client);
                                    clientsCache.add(client.getId(), client);
                                }else{
                                    Log.d("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a Cliente");
                                }
                            }

                            if (!clients.isEmpty()){
                                clientsCache.setLoaded(true);
                                saveData.setCollectionClient(clientsCache);

                                if ((saveData.getClients()!= null && !saveData.getClients().isEmpty()) && saveData.getClients().isLoaded()){
                                    Log.d(TAG, "📖 Clientes guardados en cache");
                                }

                                taskCompletionSource.setResult(new ObjectResult<>(true, "success", clients));
                            }else{
                                taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron clientes", null));
                            }

                            if (!clients.isEmpty()) {
                                Log.d(TAG, "✅ Resultado exitoso antes del return:");
                                Log.d(TAG, "| → Estado (exit): true");
                                Log.d(TAG, "| → Mensaje: Clientes cargados");
                                Log.d(TAG, "| → Número total de clientes: " + clients.size());

                                for (int i = 0; i < clients.size(); i++) {
                                    Client c = clients.get(i);
                                    Log.d(TAG, "|    Cliente[" + i + "]: { id: " + c.getId() + ", nombre: " + c.getName() + ", idUsr: " + c.getIdUsr() + " }");
                                }

                            } else {
                                Log.d(TAG, "🟡 Resultado vacío antes del return:");
                                Log.d(TAG, "| → Estado (exit): false");
                                Log.d(TAG, "| → Mensaje: No hay clientes disponibles");
                                Log.d(TAG, "| → Número total de clientes: 0");
                            }

                        } else {
                            Log.d("Firebase", "⚠️ No se encontró ningún cliente que pertenezca al usuario: "+idUsr);
                            taskCompletionSource.setResult(new ObjectResult<>(false, "Cliente no encontrado", new ArrayList<>() ));
                            CacheCollection<Client> clientsCache = new CacheCollection<>();
                            clientsCache.setLoaded(true);
                            saveData.setCollectionClient(clientsCache);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "❌ Error al buscar el cliente: " + e.getMessage());
                        taskCompletionSource.setException(new ComplexFBCE( new ObjectResult<>(false, "Error al buscar el cliente: " + e.getMessage(), null)));
                    });
        }
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee una comidaDiet a partir de su id de dieta y de alimento
     * @param idDieta id de la dieta
     * @param idAlimento id del alimento
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<FoodDiet>> readFoodDiet(String idDieta, String idAlimento) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<FoodDiet>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        if (saveData.getFoodDiets().isLoaded() && saveData.getFoodDiets().contains(idDieta)) {
            Log.d(TAG, "📖 FoodDiet encontrada en cache");

            Collection<ArrayList<FoodDiet>> foodDiets = saveData.getFoodDietsOfDiet(idDieta).values();

            for (ArrayList<FoodDiet> lista : foodDiets) {
                for (FoodDiet foodDiet : lista) {
                    if (foodDiet.getIdAlimento().equals(idAlimento)) {
                        taskCompletionSource.setResult(new ObjectResult<>(true, "success", foodDiet));
                        Log.d("Firebase", "📖 FoodDiet encontrada: " + foodDiet.getIdAlimento());
                        return taskCompletionSource.getTask();
                    }
                }
            }

            // Si no se encuentra
            taskCompletionSource.setException(new ComplexFBCE(
                    new ObjectResult<>(false, "Alimento no encontrado en la dieta", null)
            ));
            return taskCompletionSource.getTask();
        }



        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .whereEqualTo("idAlimento", idAlimento)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // El primero que encuentre
                        FoodDiet foodDiet = document.toObject(FoodDiet.class);
                        if (foodDiet != null) {
                            Log.d("Firebase", "📖 FoodDiet encontrada: " + foodDiet.getIdAlimento());
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", foodDiet));
                        } else {
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al convertir el documento a FoodDiet", null)));
                        }

                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún foodDiet con idDieta: " + idDieta +" y con idAlimento: "+idAlimento);
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "FoodDiet no encontrado", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el FoodDiet: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar el FoodDiet: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }



    /**
     * Metodo que lee todas las comidasde una dieta a partir de su id de dieta (Fill)
     * @param idDieta id de la dieta
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Map<String, ArrayList<FoodDiet>>>> readFoodDietByDiet(String idDieta) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();
        TaskCompletionSource<ObjectResult<Map<String, ArrayList<FoodDiet>>>> taskCompletionSource = new TaskCompletionSource<>();

        // Si ya está en caché, devolvemos directamente
        if (saveData.getFoodDiets().isLoaded() && saveData.getFoodDiets().contains(idDieta)) {
            Log.d(TAG, "📖 FoodDiets encontradas en cache");
            taskCompletionSource.setResult(new ObjectResult<>(true, "success", saveData.getFoodDiets().get(idDieta)));
            return taskCompletionSource.getTask();
        }

        // Consultamos en Firestore
        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        fst.collection("recetaAlimento")
                                .whereEqualTo("idDieta", idDieta)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    if (!queryDocumentSnapshots1.isEmpty()) {
                                        Map<String, ArrayList<FoodDiet>> agrupado = new HashMap<>();

                                        for (DocumentSnapshot documentR : queryDocumentSnapshots1.getDocuments()) {
                                            RelacionRecetaAlimento  rra= documentR.toObject(RelacionRecetaAlimento.class);
                                            if (rra != null) {
                                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                                    FoodDiet food = document.toObject(FoodDiet.class);
                                                    if (food != null) {
                                                        food.setIdAlimento(rra.getIdFood());
                                                        food.setG(rra.getGr());
                                                        String nombre = food.getName(); // o .getComida(), según agrupación
                                                        agrupado.putIfAbsent(nombre, new ArrayList<>());
                                                        agrupado.get(nombre).add(food);
                                                        Log.d("Firebase", "📖 FoodDiet añadida: " + food.getId());
                                                    } else {
                                                        Log.e("Firebase", "⚠️ Documento no convertible a FoodDiet");
                                                    }
                                                }

                                                if (!agrupado.isEmpty()) {
                                                    // Guardamos en la caché
                                                    saveData.getFoodDiets().add(idDieta, agrupado);
                                                    saveData.getFoodDiets().setLoaded(true);

                                                    if ((saveData.getFoodDiets() != null && !saveData.getFoodDiets().isEmpty()) && saveData.getFoodDiets().isLoaded()) {
                                                        Log.d(TAG, "📖 FoodDiets guardadas en cache");
                                                    }

                                                    taskCompletionSource.setResult(new ObjectResult<>(true, "success", agrupado));
                                                } else {
                                                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "FoodDiet no encontrado", null)));
                                                }
                                            }
                                        }
                                    }else{
                                        taskCompletionSource.setResult(new ObjectResult<>(false, "FoodDiet sin hijos", null));
                                        return;
                                    }
                                }
                        );
                    } else {
                        Log.d("Firebase", "⚠️ No se encontraron documentos con idDieta: " + idDieta);
                        taskCompletionSource.setResult(new ObjectResult<>(false, "FoodDiet no encontrado", null));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar FoodDiet: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error: " + e.getMessage(), null)));
                });

        return taskCompletionSource.getTask();
    }



    /**
     * Metodo que lee todas las comidas de una dieta a partir de su id de dieta y de dia
     * @param idDieta id de la dieta
     * @param dia  dia de la dieta
     * @param callback parametro que convierte el metodo de asincronico en sincronico
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<ObjectResult<Map<String,ArrayList<FoodDiet>>>> readFoodDietByDay(String idDieta, String dia, OnResultCallBack<ObjectResult<ArrayList<FoodDiet>>> callback) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();
        TaskCompletionSource<ObjectResult<Map<String,ArrayList<FoodDiet>>>> taskCompletionSource = new TaskCompletionSource<>();

        if (saveData.getFoodDiets().isLoaded() && saveData.getFoodDiets().contains(idDieta)) {
            Log.d(TAG, "📖 FoodDiets encontradas en cache");

            Collection<ArrayList<FoodDiet>> listaDeListas = saveData.getFoodDietsOfDiet(idDieta).values();
            Map<String, ArrayList<FoodDiet>> resultados = new HashMap<>();

            for (ArrayList<FoodDiet> lista : listaDeListas) {
                for (FoodDiet foodDiet : lista) {
                    if (dia.equals(foodDiet.getDia())) {
                        String nombre = foodDiet.getName();
                        resultados.putIfAbsent(nombre, new ArrayList<>());
                        resultados.get(nombre).add(foodDiet);
                        Log.d(TAG, "📖 FoodDiet añadida: " + foodDiet.getId());
                    }
                }
            }

            if (!resultados.isEmpty()) {
                taskCompletionSource.setResult(new ObjectResult<>(true, "success", resultados));
            } else {
                taskCompletionSource.setException(new ComplexFBCE(
                        new ObjectResult<>(false, "No se encontraron alimentos para el día especificado", null)
                ));
            }

        } else {
            taskCompletionSource.setException(new ComplexFBCE(
                    new ObjectResult<>(false, "Colección no cargada o idDieta no encontrado", null)
            ));
        }


        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .whereEqualTo("dia", dia)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        Map<String,ArrayList<FoodDiet>> foods = new HashMap<>();


                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            FoodDiet food = document.toObject(FoodDiet.class);
                            if (food != null) {
                                Log.d("Firebase", "📖 FoodDiet encontrada: " + food.getIdAlimento());
                                foods.putIfAbsent(food.getName(), new ArrayList<>());
                                foods.get(food.getName()).add(food);
                            }
                            else{
                                Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                            }
                        }

                        if (!foods.isEmpty()){
                            taskCompletionSource.setResult(new ObjectResult<>(true, "success", foods));
                        }else{
                            taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "FoodDiet no encontrado", null)));
                        }

                    } else {
                        Log.d("Firebase", "⚠️ No se encontró ningún foodDiet con idDieta: " + idDieta );
                        taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "FoodDiet no encontrado", null)));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "❌ Error al buscar el FoodDiet: " + e.getMessage());
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar el FoodDiet: " + e.getMessage(), null)));
                });
        return taskCompletionSource.getTask();
    }


    /**
     * Metodo que lee un usuario a partir de su correo
     * @param email correo del usuario
     * @return objeto User
     * @throws FBCException excepcion propia que marca si la conexion no es buena
     */
    public static Task<User> readUserByEmail(String email) throws FBCException {


        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();

        if (saveData.getUser() != null&&saveData.getUser().getName().equals(email)){
            Log.d(TAG, "📖 Usuario encontrado en cache");
            TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
            taskCompletionSource.setResult(saveData.getUser());
            return taskCompletionSource.getTask();
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
                            saveData.setUser(usuario);

                            if (saveData.getUser() != null && saveData.getUser()!= new User("","","")){
                                Log.d(TAG, "📖 Usuario guardado en cache");
                            }

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
        SaveData saveData = SaveData.getInstance();

        final TaskCompletionSource<FoodDiet> taskCompletionSource = new TaskCompletionSource<>();

        if (saveData.getFoodDiets().isLoaded() && saveData.getFoodDiets().contains(idDieta)) {
            Log.d(TAG, "📖 FoodDiet encontrada en cache");
            Collection<ArrayList<FoodDiet>> listaDeListas = saveData.getFoodDietsOfDiet(idDieta).values();
            for (ArrayList<FoodDiet> lista : listaDeListas) {
                for (FoodDiet foodDiet : lista) {
                    if (foodDiet.getIdAlimento().equals(idAlimento)) {
                        if (foodDiet != null) {
                            Log.d("Firebase", "📖 FoodDiet encontrada: " + foodDiet.getIdAlimento());
                            taskCompletionSource.setResult(foodDiet);
                            return taskCompletionSource.getTask();
                        }else{
                            Log.e("Firebase", "⚠️ Documento encontrado pero no se pudo convertir a FoodDiet");
                            taskCompletionSource.setException(new Exception("Error al convertir documento a FoodDiet"));
                        }
                    }
                }
            }

        }


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

    /**
     * Método que obtiene los alimentos de una dieta para un día específico
     * @param idDieta ID de la dieta
     * @param dia Día específico (1-7)
     * @return Lista de FoodDiet con los alimentos del día
     * @throws FBCException excepción propia que marca si la conexión no es buena
     */
    public static Task<ObjectResult<List<FoodDiet>>> readFoodsForDay(String idDieta, int dia) throws FBCException {
        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        TaskCompletionSource<ObjectResult<List<FoodDiet>>> taskCompletionSource = new TaskCompletionSource<>();
        SaveData saveData = SaveData.getInstance();

        // Primero verificamos si ya tenemos los datos en caché
        if (saveData.getFoodDiets().isLoaded() && saveData.getFoodDiets().contains(idDieta)) {
            List<FoodDiet> foodsForDay = new ArrayList<>();
            Map<String, ArrayList<FoodDiet>> allFoodDiets = saveData.getFoodDietsOfDiet(idDieta);

            for (ArrayList<FoodDiet> foodList : allFoodDiets.values()) {
                for (FoodDiet foodDiet : foodList) {
                    if (foodDiet.getDia().equals(String.valueOf(dia))) {
                        foodsForDay.add(foodDiet);
                    }
                }
            }

            if (!foodsForDay.isEmpty()) {
                taskCompletionSource.setResult(new ObjectResult<>(true, "success", foodsForDay));
                return taskCompletionSource.getTask();
            }
        }

        // Si no está en caché, lo buscamos en Firestore
        fst.collection("comidaDietas")
                .whereEqualTo("idDieta", idDieta)
                .whereEqualTo("dia", String.valueOf(dia))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<FoodDiet> foodsForDay = new ArrayList<>();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            FoodDiet foodDiet = document.toObject(FoodDiet.class);
                            if (foodDiet != null) {
                                foodsForDay.add(foodDiet);
                            }
                        }

                        taskCompletionSource.setResult(new ObjectResult<>(true, "success", foodsForDay));
                    } else {
                        taskCompletionSource.setResult(new ObjectResult<>(false, "No se encontraron alimentos para este día", new ArrayList<>()));
                    }
                })
                .addOnFailureListener(e -> {
                    taskCompletionSource.setException(new ComplexFBCE(new ObjectResult<>(false, "Error al buscar alimentos: " + e.getMessage(), null)));
                });

        return taskCompletionSource.getTask();
    }
}
