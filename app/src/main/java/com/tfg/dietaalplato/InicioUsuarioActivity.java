package com.tfg.dietaalplato;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class InicioUsuarioActivity extends AppCompatActivity {

    Button botonLogout;
    ImageButton botonRetorno;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_usuario);

        botonRetorno = findViewById(R.id.return_button);
        botonLogout = findViewById(R.id.logout_button);

        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false);

        botonRetorno.setVisibility(View.INVISIBLE);
        botonLogout.setVisibility(View.VISIBLE);

        if(esAdmin) {
            botonRetorno.setVisibility(View.VISIBLE);
            botonLogout.setVisibility(View.INVISIBLE);
        }

    }


    public void onClickBackNavigation(View view){
        Intent intent = new Intent(this, InicioAdminActivity.class );
        startActivity(intent);
    }


    public void onClickPatient(View view){
        Intent intent = new Intent(this, PacientesActivity.class );
        startActivity(intent);
    }


    public void onClickAlimentos(View view){
        Intent intent = new Intent(this, BancoAlimentosActivity.class );
        startActivity(intent);
    }


    public void onClickCloseSesion(View view){
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }
}
