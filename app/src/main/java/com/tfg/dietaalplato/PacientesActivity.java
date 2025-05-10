package com.tfg.dietaalplato;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;


public class PacientesActivity extends AppCompatActivity {


    private LinearLayout layoutClientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);


        layoutClientes = findViewById(R.id.layoutClientes);


        try {
            FireBaseReader.readClientFromUser("USU0002").addOnSuccessListener(
                  clientes ->{
                      for (Client cliente : clientes.result) {
                          Button boton = new Button(this);
                          boton.setText(cliente.getName()); // o el campo que tengas
                          boton.setLayoutParams(new LinearLayout.LayoutParams(
                                  LinearLayout.LayoutParams.MATCH_PARENT,
                                  LinearLayout.LayoutParams.WRAP_CONTENT));


                          boton.setOnClickListener(v ->{
                                      Log.d("Cliente", "Nombre del cliente: " + cliente.getName());

                                      Intent intent = new Intent(this, DietasActivity.class);
                                      startActivity(intent);

                                  }
                          );
                          layoutClientes.addView(boton);
                      }
                  }
            );
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickReturn(View view){
        Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);
    }
}
