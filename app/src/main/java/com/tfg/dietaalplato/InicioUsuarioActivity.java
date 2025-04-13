package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioUsuarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onClickPatient(View view){
        Intent intent = new Intent(this, PacientesActivity.class );
        startActivity(intent);
    }

    public void onClickAlimentos(View view){
        Intent intent = new Intent(this, ComidasActivity.class );
        startActivity(intent);
    }

    public void onClickCloseSesion(View view){
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }
}