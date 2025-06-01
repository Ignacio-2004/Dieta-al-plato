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
    private boolean esAdmin;
    private String idUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_usuario);

        botonRetorno = findViewById(R.id.return_button);
        botonLogout = findViewById(R.id.logout_button);

        esAdmin = getIntent().getBooleanExtra("esAdmin", false);
        idUser = esAdmin ? getIntent().getStringExtra("usuarioSeleccionado") : SaveData.getInstance().getUser().getId();

        // Configurar visibilidad de botones
        botonRetorno.setVisibility(esAdmin ? View.VISIBLE : View.INVISIBLE);
        botonLogout.setVisibility(esAdmin ? View.INVISIBLE : View.VISIBLE);
    }

    public void onClickBackNavigation(View view){ //este botón es solo visible si es admin
        Intent intent = new Intent(this, InicioAdminActivity.class);
        startActivity(intent);
    }

    public void onClickPatient(View view) {
        startNewActivity(PacientesActivity.class);
    }

    public void onClickAlimentos(View view) {
        startNewActivity(BancoAlimentosActivity.class);
    }

    public void onClickCloseSesion(View view){ //seleccionamos cerrar sesión, este botón no aparece si somos admin
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }

    //helper para evitar código repetido
    private void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        intent.putExtra("esAdmin", esAdmin);
        idUser = esAdmin ? getIntent().getStringExtra("usuarioSeleccionado") : SaveData.getInstance().getUser().getId();

        startActivity(intent);
    }
}