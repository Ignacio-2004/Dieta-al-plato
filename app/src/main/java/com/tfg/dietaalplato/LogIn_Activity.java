package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.*;

import java.net.*;

public class LogIn_Activity extends AppCompatActivity {

    private static final String TAG = "FirebaseConnection";
    private DatabaseReference databaseReference;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 1. Obtener instancia con URL europea
        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://dieta-al-plato-20-default-rtdb.europe-west1.firebasedatabase.app"
        );

        // 2. Configurar referencia
        databaseReference = database.getReference();

        // 3. Prueba de conexión completa
        testFirebaseConnection();
    }

    private void testFirebaseConnection() {
        String testKey = "test_" + System.currentTimeMillis();

        // Escritura
        databaseReference.child(testKey).setValue("conexion_exitosa")
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "✅ Datos escritos en Firebase Europa");
                    Toast.makeText(this, "Conexión exitosa con región europea", Toast.LENGTH_SHORT).show();

                    // Lectura para verificar
                    databaseReference.child(testKey).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

        // Monitor de conexión permanente
        monitorConnectionStatus();
    }

    private void monitorConnectionStatus() {
        DatabaseReference connectedRef = databaseReference.getRoot().child(".info/connected");
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
    }
}