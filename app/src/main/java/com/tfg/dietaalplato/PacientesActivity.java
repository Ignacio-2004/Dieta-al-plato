package com.tfg.dietaalplato;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.dialogo.ClientCreator_Dialogo;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.ClientInfo_Dialog;


public class PacientesActivity extends AppCompatActivity {


    private LinearLayout layoutClientes;
    private SaveData saveData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");

        layoutClientes = findViewById(R.id.layoutClientes);

        saveData = SaveData.getInstance();


        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false); //comprobamos si es admin


        if(esAdmin) { //si es admin, deberíamos mostrar los clientes del usuario que el admin haya seleccionado


        }
        else { //si no es admin, simplemente mostramos los clientes asociados al usuario con el que se ha iniciado sesión
            try {
                Blocker.createBlocker(this.findViewById(android.R.id.content),this);
                FireBaseReader.readClientFromUser(saveData.getUser().getId()).addOnSuccessListener(
                        clientes -> {
                            for (Client cliente : clientes.result) {
                                LinearLayout item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.HORIZONTAL);
                                item.setPadding(12, 5, 5, 12);
                                item.setElevation(8f);
                                layoutClientes.addView(item);




                                item = new LinearLayout(this);
                                item.setOrientation(LinearLayout.HORIZONTAL);
                                item.setBackgroundResource(R.drawable.bg_food_background);
                                item.setPadding(12, 12, 12, 12);
                                item.setElevation(8f);


                                TextView nombre = new TextView(this);
                                try{
                                    nombre.setText(cliente.getName().substring(0, 1).toUpperCase() + cliente.getName().substring(1).toLowerCase() +" "+
                                            cliente.getApe().substring(0, 1).toUpperCase() + cliente.getApe().substring(1).toLowerCase());
                                } catch (Exception e) {
                                    nombre.setText("Cliente mal registrado");
                                }
                                nombre.setTextColor(Color.WHITE);
                                nombre.setTextSize(30);
                                nombre.setGravity(Gravity.CENTER);
                                nombre.setTypeface(null, Typeface.BOLD);
                                nombre.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));




                                item.setOnClickListener(v ->{
                                            Log.d("Cliente", "Nombre del cliente: " + cliente.getName());
                                            saveData.setCurrentClient(cliente);


                                            Intent intent = new Intent(this, DietasActivity.class);
                                            intent.putExtra("clienteSeleccionado", cliente.getName()); //al seleccionar un cliente, se manda el que se haya seleccionado
                                            startActivity(intent);


                                        }
                                );
                                item.addView(nombre);
                                layoutClientes.addView(item);
                            }
                            Blocker.removeBlocker(this.findViewById(android.R.id.content));
                        }
                ).addOnFailureListener(
                        e -> {
                            Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
                            Blocker.removeBlocker(this.findViewById(android.R.id.content));
                        }
                );
            } catch (FBCException e) {
                Toast.makeText(this, "Error al cargar clientes", Toast.LENGTH_SHORT).show();
                Blocker.removeBlocker(this.findViewById(android.R.id.content));
            }
        }

    }


    public void onClickReturn(View view){ //si volvemos para atrás, solo queremos saber si es admin
        Intent intent = new Intent(this, InicioUsuarioActivity.class );


        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false); //comprobamos si es admin
        intent.putExtra("esAdmin", esAdmin);


        startActivity(intent);
    }


    public void onClickAddClient(View view){
        ClientInfo_Dialog dialogo = ClientInfo_Dialog.getInstance(false);
        dialogo.show(getSupportFragmentManager(), "dialogoNuevoCliente");
    }


    public void onRestartClick(View view) {
        super.onRestart();
        recreate();
    }
}
