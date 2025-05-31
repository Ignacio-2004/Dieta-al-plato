package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.tfg.dietaalplato.firebase.utilities.OnResultCallBack;
import com.tfg.dietaalplato.utilities.dialogo.ClientInfo_Dialog;

public class DietasActivity extends AppCompatActivity {

    ImageView imagenCliente;
    Button boton1dias, boton3dias, boton7dias;
    ImageButton botonimage1dias, botonimage3dias, botonimage7dias;
    TextView nombreClienteText;

    private boolean esAdmin;
    private String idUser, clienteSeleccionado;

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

        // Mostrar nombre del cliente con formato adecuado
        if (clienteSeleccionado != null && !clienteSeleccionado.isEmpty()) {
            String nombreFormateado = clienteSeleccionado.substring(0, 1).toUpperCase() +
                    clienteSeleccionado.substring(1).toLowerCase();
            nombreClienteText.setText(nombreFormateado);
        }

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

    private void abrirDiasActivity(int dietaSeleccionada) { //si escogemos deiats de tipo 3 o 7, pasaremos sabiendo si es admin y el tipo de dieta
        Intent intent = createBaseIntent(DiasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }


    private void abrirComidasActivity(int dietaSeleccionada) { // en caso de que se seleccione la dieta de 1 día, pasará directamente a la ventana de comidas sabiendo si es admin y con dieta de tipo 1
        Intent intent = createBaseIntent(ComidasActivity.class);
        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }

    private Intent createBaseIntent(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.putExtra("esAdmin", esAdmin);
        intent.putExtra("usuarioSeleccionado", idUser);
        intent.putExtra("clienteSeleccionado", clienteSeleccionado);
        return intent;
    }

    public void onClickBackNavigation(View view){ //si volvemos para atrás, solo queremos saber si es admin y el cliente que había seleccionado
        Intent intent = createBaseIntent(PacientesActivity.class);
        startActivity(intent);
    }


    public void onClickOpenInfo(View view, OnResultCallBack<Boolean> callback){
        ClientInfo_Dialog dialogo = ClientInfo_Dialog.getInstance(true);
        dialogo.show(getSupportFragmentManager(), "dialogoInfoCliente");
    }
}
