package com.tfg.dietaalplato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import androidx.fragment.app.DialogFragment;

public class SignUp_Dialogo extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflamos el layout para el diálogo
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_nuevo_usuario, null);

        // Inicializamos los campos
        final EditText correo = view.findViewById(R.id.editTextCorreo);
        final EditText password = view.findViewById(R.id.editTextPassword);
        final EditText confirmPassword = view.findViewById(R.id.editTextConfirmPassword);

        // Creamos el builder del diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setCancelable(true);

        // Botón de confirmar
        Button btnConfirmar = view.findViewById(R.id.buttonConfirmar);
        btnConfirmar.setOnClickListener(v -> {
            // Aquí puedes obtener los valores y validarlos
            String correoStr = correo.getText().toString();
            String passwordStr = password.getText().toString();
            String confirmPasswordStr = confirmPassword.getText().toString();

            if (passwordStr.equals(confirmPasswordStr)) {
                // Aquí puedes añadir la lógica para crear la cuenta del nuevo usuario
                // Ejemplo: Firestore.createUser(correoStr, passwordStr);
                dismiss(); // Cerrar el diálogo
            } else {
                // Mostrar un mensaje de error si las contraseñas no coinciden
                confirmPassword.setError("Las contraseñas no coinciden");
            }
        });

        // Botón de cancelar
        Button btnCancelar = view.findViewById(R.id.buttonCancelar);
        btnCancelar.setOnClickListener(v -> dismiss()); // Cerrar el diálogo sin hacer nada

        return builder.create(); // Retornamos el diálogo
    }
}
