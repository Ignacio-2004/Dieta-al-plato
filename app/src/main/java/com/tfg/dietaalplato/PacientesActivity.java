package com.tfg.dietaalplato;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.tfg.dietaalplato.utilities.dialogo.ClientCreator_Dialogo;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.SaveData;


public class PacientesActivity extends AppCompatActivity {


    private LinearLayout layoutClientes;
    private SaveData saveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);


        layoutClientes = findViewById(R.id.layoutClientes);

        saveData = SaveData.getInstance();
        try {
            FireBaseReader.readClientFromUser(saveData.getUser().getId()).addOnSuccessListener(
                  clientes ->{
                      for (Client cliente : clientes.result) {
                          Button boton = new Button(this);
                          boton.setText(cliente.getName()); // o el campo que tengas
                          boton.setLayoutParams(new LinearLayout.LayoutParams(
                                  LinearLayout.LayoutParams.MATCH_PARENT,
                                  LinearLayout.LayoutParams.WRAP_CONTENT));


                          boton.setOnClickListener(v ->{
                                      Log.d("Cliente", "Nombre del cliente: " + cliente.getName());
                                      saveData.setIdActualClient(cliente.getId());

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

    public void onClickAddClient(View view){
        ClientCreator_Dialogo dialogo = new ClientCreator_Dialogo();
        dialogo.show(getSupportFragmentManager(), "dialogoNuevoCliente");
    }

    public void onRestartClick(View view) {
        super.onRestart();
        recreate();
    }
}
