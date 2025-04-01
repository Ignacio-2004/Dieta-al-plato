package com.tfg.dietaalplato.utilities;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.firebase.database.*;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.exception.FBCException;

public class FireBaseConnector {

    private DatabaseReference ref;
    private FirebaseDatabase bd;

    public FireBaseConnector() {
        bd = FirebaseDatabase.getInstance();
    }

    public void setRef() throws FBCException {
        if (bd != null) {
            this.ref = ref;
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
        if (ref != null) {
            // Crear un objeto de usuario
            User usuario = new User(id, nombre, psw);

            // Guardar en Firebase usando el ID como clave
            ref.child(id).setValue(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "✅ Usuario guardado con éxito");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "❌ Error al guardar usuario: " + e.getMessage());
                    });
        }else throw new FBCException("La referencia no puede ser nula");
    }

    public void leerUsuario(String id) throws FBCException {
        if (ref != null){
            ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User usuario = snapshot.getValue(User.class);
                        Log.d("Firebase", "📖 Usuario leído: " + usuario.getUser() + ", Password: " + usuario.getPsw());
                    } else {
                        Log.d("Firebase", "⚠️ Usuario no encontrado");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e("Firebase", "❌ Error de lectura: " + error.getMessage());
                }
            });
        }else throw new FBCException("La referencia no puede ser nula");
    }



    public DatabaseReference getRef() {
        return ref;
    }

    public FirebaseDatabase getBd() {
        return bd;
    }
}
