package com.tfg.dietaalplato.utilities;

import android.util.Log;

import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public void saveUser(String id, String nombre, String psw) throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        if (ref != null) {
            // Crear un objeto Map con los datos del usuario
            HashMap<Object, Object> usuario = new HashMap<>();
            usuario.put("id", id);
            usuario.put("user", nombre);
            usuario.put("psw", psw);

            // Guardar en Firestore en la colección "usuarios"
            fst.collection("usuarios").document(id)
                    .set(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "✅ Usuario guardado con éxito en Firestore");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "❌ Error al guardar usuario: " + e.getMessage());
                    });
        } else {
            throw new FBCException("La referencia no puede ser nula");
        }
    }

    public void leerUsuario(String id) throws FBCException {

        if (fst == null){
            throw new FBCException("La instancia de Firestore no puede ser nula");
        }

        if (ref != null) {
            // Referencia al documento del usuario
            fst.collection("usuarios").document(id).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Convertir el documento a un objeto User
                            User usuario = documentSnapshot.toObject(User.class);
                            if (usuario != null) {
                                Log.d("Firebase", "📖 Usuario leído: " + usuario.getUser() + ", Password: " + usuario.getPsw());
                            }
                        } else {
                            Log.d("Firebase", "⚠️ Usuario no encontrado en Firestore");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "❌ Error al leer usuario: " + e.getMessage());
                    });
        } else {
            throw new FBCException("La referencia no puede ser nula");
        }
    }



    public DatabaseReference getRef() {
        return ref;
    }

    public FirebaseDatabase getBd() {
        return bd;
    }
}
