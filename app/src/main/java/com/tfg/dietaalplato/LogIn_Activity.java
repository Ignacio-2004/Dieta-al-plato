package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.*;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.FireBaseConnector;
import com.tfg.dietaalplato.utilities.exception.FBCException;
import com.tfg.dietaalplato.utilities.BasketAnimation;

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



        try {
            FireBaseConnector database = FireBaseConnector.getInstance();
            database.testFirebaseConnection();
            database.monitorConnectionStatus();

            database.saveUser(new User("USE002","Patatudo","123456"));
            database.readUsuario("USE002");

        } catch (FBCException e) {
            throw new RuntimeException(e);
        }

    }

    public void onClick(View view) {
        View animView = getLayoutInflater().inflate(R.layout.animated_basket_view, null);
        BasketAnimation.showAnimation(this);
    }

}