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
import com.tfg.dietaalplato.utilities.FireBaseConnector;
import com.tfg.dietaalplato.utilities.exception.FBCException;

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

        FireBaseConnector database = new FireBaseConnector();

        try {
            database.setRef();
            database.testFirebaseConnection();
            database.monitorConnectionStatus();

            database.saveUser("1", "Ignacio", "1234");
            database.leerUsuario("1");

        } catch (FBCException e) {
            throw new RuntimeException(e);
        }

    }

}