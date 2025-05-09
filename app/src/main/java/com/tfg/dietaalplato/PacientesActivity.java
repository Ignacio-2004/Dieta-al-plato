package com.tfg.dietaalplato;


import android.content.Intent;
import android.os.Bundle;
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
    private FireBaseConnector fbConnector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);


        layoutClientes = findViewById(R.id.layoutClientes);
        fbConnector = new FireBaseConnector(); // tu clase personalizada para Firestore


        try {
            FireBaseReader.readAllFromCollection("clientes", Client.class)
                    .addOnSuccessListener(clientes -> {
                        for (Client cliente : clientes) {
                            Button boton = new Button(this);
                            boton.setText(cliente.getName()); // o el campo que tengas
                            boton.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));


                            boton.setOnClickListener(v ->
                                    Toast.makeText(this, "Cliente: " + cliente.getName(), Toast.LENGTH_SHORT).show()
                            );


                            layoutClientes.addView(boton);
                        }
                    });
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickReturn(View view){
        Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);
    }
}
