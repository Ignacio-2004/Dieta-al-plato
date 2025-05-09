package com.tfg.dietaalplato;


import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;


public class InicioAdminActivity extends AppCompatActivity {


    private LinearLayout layoutUsuarios;
    private FireBaseConnector fbConnector; // tu conector personalizado


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_admin);


        layoutUsuarios = findViewById(R.id.layoutUsuarios);
        fbConnector = new FireBaseConnector(); // instanciar tu clase


        try {
            FireBaseReader.readAllFromCollection("usuarios", User.class)
                    .addOnSuccessListener(usuarios -> {
                        for (User usuario : usuarios) {
                            Button botonUsuario = new Button(this);
                            botonUsuario.setText(usuario.getName());


                            botonUsuario.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));


                            botonUsuario.setOnClickListener(v ->
                                    Toast.makeText(this, "Usuario: " + usuario.getName(), Toast.LENGTH_SHORT).show()
                            );


                            layoutUsuarios.addView(botonUsuario);
                        }
                    });
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
        }
    }
}
