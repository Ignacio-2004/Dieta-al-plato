package com.tfg.dietaalplato;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.exceptions.ComplexFBCE;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.ClientInfo_Dialog;

import java.util.ArrayList;
import java.util.Map;

public class DietasActivity extends AppCompatActivity {

    private SaveData saveData;
    ImageView imagenCliente;
    Button boton1dias, boton3dias, boton7dias;
    ImageButton botonimage1dias, botonimage3dias, botonimage7dias;
    TextView nombreClienteText;

    private boolean esAdmin;
    private String idUser, clienteSeleccionado;
    private static final String msgErrorRepeatDiet = "Ya existe una dieta con las mismas credenciales, para modificarla entre en la ficha de la dieta.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas);

        // Obtener datos del Intent
        esAdmin = getIntent().getBooleanExtra("esAdmin", false);

        if(esAdmin) {
            idUser = getIntent().getStringExtra("usuarioSeleccionado");
        }

        clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");

        initViews();
        setupButtons();
    }

    private void initViews() {
        imagenCliente = findViewById(R.id.imgCliente);
        nombreClienteText = findViewById(R.id.nombrePaciente_textview);
        nombreClienteText.setText((clienteSeleccionado.substring(0, 1).toUpperCase() + clienteSeleccionado.substring(1)));


        // Referenciar los botones
        boton1dias = findViewById(R.id.boton1dias_button);
        boton3dias = findViewById(R.id.boton3dias_button);
        boton7dias = findViewById(R.id.boton7dias_button);


        botonimage1dias = findViewById(R.id.boton1dias_imagebutton);
        botonimage3dias = findViewById(R.id.boton3dias_imagebutton);
        botonimage7dias = findViewById(R.id.boton7dias_imagebutton);
    }

    private void setupButtons() {
        // Configurar listeners para los botones
        View.OnClickListener listener1Dia = v -> abrirComidasActivity(1);
        View.OnClickListener listener3Dias = v -> abrirDiasActivity(3);
        View.OnClickListener listener7Dias = v -> abrirDiasActivity(7);

        boton1dias.setOnClickListener(listener1Dia);
        boton3dias.setOnClickListener(listener3Dias);
        boton7dias.setOnClickListener(listener7Dias);

        botonimage1dias.setOnClickListener(listener1Dia);
        botonimage3dias.setOnClickListener(listener3Dias);
        botonimage7dias.setOnClickListener(listener7Dias);

        // Listeners para la información del cliente
        View.OnClickListener infoListener = v -> onClickOpenInfo(v, callback -> {
            super.onRestart();
        });
        imagenCliente.setOnClickListener(infoListener);
        nombreClienteText.setOnClickListener(infoListener);
    }

    private void abrirDiasActivity( int dietaSeleccionada) {
        Blocker.createBlocker(this.findViewById(android.R.id.content), this);
        ArrayList<String> data = new ArrayList<>();
        data.add(String.valueOf(dietaSeleccionada));
        data.add("null");
        ValidationResult result = Diet.toMapData(data, saveData.getCurrentClient().getId());
        Log.d("Cliente", "Cliente: "+saveData.getCurrentClient().toString());
        Log.d("Cliente", result.toString());
        FireBaseWriter.saveData(Diet.class, result).addOnSuccessListener(
                validationResult -> {
                    Log.d("Dieta", validationResult.toString());
                    Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    Intent intent = new Intent(this, DiasActivity.class);
                    try{
                        saveData.setCurrentDiet((Diet) validationResult.result);
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                e -> {
                    if (!((ComplexFBCE) e).reason.message.equals(msgErrorRepeatDiet)) {
                        Toast.makeText(this, "Error al guardar la dieta", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }else{
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                        Intent intent = new Intent(this, DiasActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }


    private void abrirComidasActivity(int dietaSeleccionada) { // en caso de que se seleccione la dieta de 1 día, pasará directamente a la ventana de comidas
        Blocker.createBlocker(this.findViewById(android.R.id.content), this);
        ArrayList<String> data = new ArrayList<>();
        data.add(String.valueOf(dietaSeleccionada));
        data.add("null");
        ValidationResult result = Diet.toMapData(data, saveData.getCurrentClient().getId());
        Log.d("Cliente", "Cliente: "+saveData.getCurrentClient().toString());
        Log.d("Cliente", result.toString());
        FireBaseWriter.saveData(Diet.class, result).addOnSuccessListener(
                validationResult -> {
                    Log.d("Dieta", validationResult.toString());
                    Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    Intent intent = new Intent(this, DietaIDia.class);
                    try{
                        saveData.setCurrentDiet((Diet) validationResult.result);
                        startActivity(intent);
                    }catch (Exception e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                e -> {
                    if (!((ComplexFBCE) e).reason.message.equals(msgErrorRepeatDiet)) {
                        Toast.makeText(this, "Error al guardar la dieta", Toast.LENGTH_SHORT).show();
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                    }else{
                        Blocker.removeBlocker(this.findViewById(android.R.id.content));
                        Intent intent = new Intent(this, DietaIDia.class);
                        try{
                            Map<String,String> datas = (Map<String, String>) ((ComplexFBCE) e).reason.result;
                            Diet diet = new Diet(datas.get("id"), datas.get("name"), datas.get("tip"), datas.get("idCli"), datas.get("just"));
                            saveData.setCurrentDiet(diet);
                            startActivity(intent);
                        }catch (Exception e2) {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    public void onClickReturn(View view){ //si volvemos para atrás, solo queremos saber si es admin y el cliente que había seleccionado
        Intent intent = new Intent(this, PacientesActivity.class );


        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false); //comprobamos si es admin
        intent.putExtra("esAdmin", esAdmin);


        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        startActivity(intent);
    }


    public void onClickOpenInfo(View view, OnResultCallBack<Boolean> callback){
        ClientInfo_Dialog dialogo = ClientInfo_Dialog.getInstance(true);
        dialogo.show(getSupportFragmentManager(), "dialogoInfoCliente");
    }
}
