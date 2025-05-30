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

        //vision de botones si no somos admin
        botonRetorno.setVisibility(View.INVISIBLE);
        botonLogout.setVisibility(View.VISIBLE);

        if(esAdmin) { //vision de botones si somos admin
            botonRetorno.setVisibility(View.VISIBLE);
            botonLogout.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickBackNavigation(View view){ //este botón es solo visible si es admin
        Intent intent = new Intent(this, InicioAdminActivity.class );
        startActivity(intent);
    }

    public void onClickPatient(View view){ //seleccionamos el botón de pacientes y tenemos que mandar si es admin o no en caso de futuro retorno
        Intent intent = new Intent(this, PacientesActivity.class );

        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false);
        intent.putExtra("esAdmin", esAdmin);

        startActivity(intent);
    }

    public void onClickAlimentos(View view){ //seleccionamos el botón de alimentos y tenemos que mandar si es admin o no en caso de futuro retorno
        Intent intent = new Intent(this, BancoAlimentosActivity.class );

        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false);
        intent.putExtra("esAdmin", esAdmin);

        startActivity(intent);
    }

    public void onClickCloseSesion(View view){ //seleccionamos cerrar sesión, este botón no aparece si somos admin
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }
}
