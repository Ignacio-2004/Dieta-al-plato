package com.tfg.dietaalplato.utilities.dialogo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.tables.User;

public class infoUser_Dialog extends Dialog {

    private final User user;

    public infoUser_Dialog(Context context, User user) {
        super(context);
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Elimina el título de la ventana
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_password_users);

        // referenciamos los objetos
        EditText correo = findViewById(R.id.correo);
        EditText password = findViewById(R.id.password_input);
        Button cerrarBtn = findViewById(R.id.cerrar_button);
        TextView errorText = findViewById(R.id.textError);

        // mostramos los datos
        if (user != null) {
            correo.setText(user.getName());

            if (user.getPsw() != null && !user.getPsw().isEmpty()) {
                password.setText(user.getPsw());
            } else {
                password.setText("Contraseña no disponible");
            }

        } else {
            errorText.setText("Error: usuario no válido.");
            errorText.setVisibility(TextView.VISIBLE);
        }

        cerrarBtn.setOnClickListener(v -> dismiss());
    }
}
