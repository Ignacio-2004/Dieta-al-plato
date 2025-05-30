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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dietas);

        String clienteSeleccionado = getIntent().getStringExtra("clienteSeleccionado");
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




        boton1dias.setOnClickListener(v -> abrirComidasActivity( 1));
        boton3dias.setOnClickListener(v -> abrirDiasActivity( 3));
        boton7dias.setOnClickListener(v -> abrirDiasActivity(7));


        botonimage1dias.setOnClickListener(v -> abrirComidasActivity(1));
        botonimage3dias.setOnClickListener(v -> abrirDiasActivity(3));
        botonimage7dias.setOnClickListener(v -> abrirDiasActivity(7));


        imagenCliente.setOnClickListener(v -> onClickOpenInfo(v, callback -> {
            super.onRestart();
        }));
        nombreClienteText.setOnClickListener(v -> onClickOpenInfo(v, callback -> {
            super.onRestart();
        }));
    }


    private void abrirDiasActivity(int dietaSeleccionada) { //si escogemos deiats de tipo 3 o 7, pasaremos sabiendo si es admin y el tipo de dieta
        Intent intent = new Intent(this, DiasActivity.class);


        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false); //comprobamos si es admin
        intent.putExtra("esAdmin", esAdmin);


        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
    }


    private void abrirComidasActivity(int dietaSeleccionada) { // en caso de que se seleccione la dieta de 1 día, pasará directamente a la ventana de comidas sabiendo si es admin y con dieta de tipo 1
        Intent intent = new Intent(this, ComidasActivity.class);


        boolean esAdmin = getIntent().getBooleanExtra("esAdmin", false); //comprobamos si es admin
        intent.putExtra("esAdmin", esAdmin);


        intent.putExtra("dietaSeleccionada", dietaSeleccionada);
        startActivity(intent);
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
