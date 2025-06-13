package com.tfg.dietaalplato.utilities.dialogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.FirebaseApp;
import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.Blocker;

import java.util.ArrayList;

public class SignUp_Dialogo extends DialogFragment {
    private TextView textError;
    private FireBaseConnector DateBase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // contenedor para permitir agregar el blocker dentro del diálogo
        FrameLayout container = new FrameLayout(requireContext());

        // mostramos el layout (Inflamos)
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_nuevo_usuario, container, false);
        container.addView(view);

        // se inicia los campos
        final EditText nam_user = view.findViewById(R.id.editTextCorreo);
        final EditText password = view.findViewById(R.id.editTextContraseña);
        final EditText confirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        textError = view.findViewById(R.id.textError);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(container)
                .setCancelable(true);

        //comprobaciones de correo y paswd
        String passwRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-_#])[A-Za-z\\d!\\-_#]{8,12}$";
        String emailRegex = "^[a-zA-Z]+\\.[a-zA-Z]+@dietetica\\.davinci$";

        // boton de confirmar
        Button btnConfirmar = view.findViewById(R.id.buttonConfirmar);
        btnConfirmar.setOnClickListener(v -> {

            // guardamos los valores introducidos
            String nomusuario_introducido = nam_user.getText().toString().trim().toLowerCase();
            String passw_introducida = password.getText().toString().trim();
            String confirmPasswordStr = confirmPassword.getText().toString();

            if (!nomusuario_introducido.isEmpty()) {
                if (!passw_introducida.isEmpty()) {
                    if (nomusuario_introducido.matches(emailRegex)) {
                        if (passw_introducida.matches(passwRegex)) {
                            if (passw_introducida.equals(confirmPasswordStr)) {

                                // bloqueamos pantalla
                                Blocker.createBlocker(container, getContext());

                                // GUARDAMOS AL USUARIO
                                ArrayList<View> views = new ArrayList<>();
                                views.add(nam_user);
                                views.add(password);

                                ValidationResult userData = User.toMapData(views);
                                DateBase = FireBaseConnector.getInstance();

                                if (FirebaseApp.getApps(this.getActivity()).isEmpty()) {
                                    Log.e("FireBase", "Firebase no se ha inicializado");
                                } else {
                                    Log.d("FireBase", "✅ Firebase está inicializado");
                                }

                                FireBaseWriter.saveData(User.class, userData)
                                        .addOnSuccessListener(aVoid -> {
                                            textError.setText("Usuario guardado correctamente.");
                                            textError.setTextColor(Color.parseColor("#027C68"));
                                            textError.setVisibility(View.VISIBLE);
                                            mostrarTextError();
                                            Blocker.removeBlocker(container);
                                            dismiss(); // cerrar el diálogo si va bien
                                        })
                                        .addOnFailureListener(e -> {
                                            textError.setText("❌  Error al guardar los datos: " + e.getMessage());
                                            textError.setVisibility(View.VISIBLE);
                                            mostrarTextError();
                                            Blocker.removeBlocker(container);
                                        });

                            } else {
                                textError.setText("❌ Las contraseñas no coinciden");
                                nam_user.setError("Error en contraseña");
                                textError.setVisibility(View.VISIBLE);
                                mostrarTextError();
                            }
                        } else {
                            textError.setText("❌ Error: La contraseña debe tener entre 8 y 12 caracteres, con al menos una mayúscula, un número y un símbolo especial (- _ # !).");
                            password.setError("Error en contraseña");
                            textError.setVisibility(View.VISIBLE);
                            mostrarTextError();
                        }
                    } else {
                        textError.setText("❌ Error: nombre de usuario debe tener la estructura [nombre.apellidos@dietetica.davinci].");
                        nam_user.setError("Error en usuario");
                        textError.setVisibility(View.VISIBLE);
                        mostrarTextError();
                    }
                } else {
                    textError.setText("❌ Error: contraseña de usuario vacía.");
                    password.setError("Error en contraseña");
                    textError.setVisibility(View.VISIBLE);
                    mostrarTextError();
                }
            } else {
                textError.setText("❌ Error: nombre de usuario vacío.");
                nam_user.setError("Error en usuario");
                textError.setVisibility(View.VISIBLE);
                mostrarTextError();
            }
        });

        // boton de cancelar
        Button btnCancelar = view.findViewById(R.id.buttonCancelar);
        btnCancelar.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    public void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 8000);
    }
}
