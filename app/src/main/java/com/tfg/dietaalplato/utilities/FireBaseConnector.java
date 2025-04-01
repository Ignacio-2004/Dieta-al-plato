package com.tfg.dietaalplato.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.object.Client;
import com.tfg.dietaalplato.object.Diet;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.exception.FBCException;

import java.util.HashMap;

public class FireBaseConnector {

    private DatabaseReference ref;
    private FirebaseDatabase bd;
    private FirebaseFirestore fst;
    private static final String TAG = "Firebase";

    public FireBaseConnector() {
        bd = FirebaseDatabase.getInstance("https://dieta-al-plato-20-default-rtdb.europe-west1.firebasedatabase.app");
        fst = FirebaseFirestore.getInstance();
    }

    public void setRef() throws FBCException {
        if (bd != null) {
            this.ref = bd.getReference();
        }else{
            throw new FBCException("La BD no puede ser nula");
        }
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
        fst.collection("clientes").document(diet.getId())
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

    public DatabaseReference getRef() {
        return ref;
    }

    public FirebaseDatabase getBd() {
        return bd;
    }
}
