package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.utilities.SaveData;

public class InicioUsuarioActivity extends AppCompatActivity {

    Button botonLogout;
    ImageButton botonRetorno;
    private SaveData saveData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_usuario);

        botonRetorno = findViewById(R.id.return_button);
        botonLogout = findViewById(R.id.logout_button);

        // Configurar visibilidad de botones
        botonRetorno.setVisibility(saveData.isAdmin() ? View.VISIBLE : View.INVISIBLE);
        botonLogout.setVisibility(saveData.isAdmin() ? View.INVISIBLE : View.VISIBLE);
    }

    public void onClickBackNavigation(View view){ //este botón es solo visible si es admin
        Intent intent = new Intent(this, InicioAdminActivity.class);
        startActivity(intent);
    }

    public void onClickPatient(View view) {
        Intent intent = new Intent(this, PacientesActivity.class);
        startActivity(intent);
    }

    public void onClickAlimentos(View view) {
        Intent intent = new Intent(this, BancoAlimentosActivity.class);
        startActivity(intent);
    }

    public void onClickCloseSesion(View view){ //seleccionamos cerrar sesión, este botón no aparece si somos admin
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }
}