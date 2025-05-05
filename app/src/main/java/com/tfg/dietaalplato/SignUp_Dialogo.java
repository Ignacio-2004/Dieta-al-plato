package com.tfg.dietaalplato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseError;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.FireBaseConnector;
import com.tfg.dietaalplato.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.exception.FBCException;

import java.util.HashMap;

public class SignUp_Dialogo extends DialogFragment {
    private TextView textError;
    private FireBaseConnector DateBase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // mostramos el layout (Inflamos)
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_nuevo_usuario, null);

        // se inicia los campos
        final EditText correo = view.findViewById(R.id.editTextCorreo);
        final EditText password = view.findViewById(R.id.editTextPassword);
        final EditText confirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        textError = view.findViewById(R.id.textError);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setCancelable(true);

        //comprobaciones de correo y paswd
        String passwRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-_#])[A-Za-z\\d!\\-_#]{8,12}$";
        String emailRegex = "^[a-zA-Z]+(\\.[a-zA-Z]+)?@educa\\.madrid\\.org$";

        // boton de confirmar
        Button btnConfirmar = view.findViewById(R.id.buttonConfirmar);
        btnConfirmar.setOnClickListener(v -> {


            // guardamos los valores introducidos
            String correo_introducido = correo.getText().toString().trim().toLowerCase();
            String passw_introducida = password.getText().toString().trim();
            String confirmPasswordStr = confirmPassword.getText().toString();

           if(!correo_introducido.isEmpty())
           {
               if(!passw_introducida.isEmpty())
               {
                   if (correo_introducido.matches(emailRegex)){
                       if (passw_introducida.matches(passwRegex)) {
                           if (passw_introducida.equals(confirmPasswordStr)) {

                               // GUARDAMOS AL USUARIO

                               // CREAMOS HASHMAP
                               HashMap<String, String> userData = new HashMap<>();
                               userData.put("name", correo_introducido);
                               userData.put("psw", passw_introducida);

                               try {
                                   // inicializamos la conexión con Firebase
                                   DateBase = FireBaseConnector.getInstance();
                                   DateBase.testFirebaseConnection();
                                   DateBase.monitorConnectionStatus();

                                   // llamamos al metodo saveData pero sin forzar a sobreescribir ya que ya comprobamos que no haya un usuario con ese correo
                                   ValidationResult result = new ValidationResult();
                                   result.exit = true; // Datos validados
                                   result.message = correo_introducido;
                                   result.data = userData;

                                   // guardamos los datos en la base de datos
                                   ValidationResult saveResult = DateBase.saveData(User.class, result, false);

                                   // comprobamos que se haya guardado correctamente
                                   if (saveResult.exit) {
                                       textError.setText("Usuario guardado correctamente.");
                                       textError.setVisibility(View.VISIBLE);
                                       mostrarTextError();

                                       dismiss();
                                   } else {
                                       textError.setText("Error al guardar los datos: " + saveResult.message);
                                       textError.setVisibility(View.VISIBLE);
                                       mostrarTextError();
                                   }

                               } catch (FBCException e) {
                                   textError.setText("Error de conexión con Firebase: " + e.getMessage());
                                   textError.setVisibility(View.VISIBLE);
                                   mostrarTextError();
                               }

                               dismiss(); // Cerrar el diálogo
                           }
                           else {
                               textError.setText("Las contraseñas no coinciden");
                               textError.setVisibility(View.VISIBLE);
                               mostrarTextError();
                           }

                       }
                       else{
                           textError.setText(" Error: La contraseña debe tener entre 8 y 12 caracteres, con al menos una mayúscula, un número y un símbolo especial (- _ # !).");
                           textError.setVisibility(View.VISIBLE);
                           mostrarTextError();
                       }

                   }
                   else {
                       textError.setText("  Error: correo de usuario debe tener la estructura [nombre.apellidos@educa.madrid.org].  ");
                       textError.setVisibility(View.VISIBLE); // el textView se puede ver
                       mostrarTextError();// se oculta el mensaje
                   }

               }
               else{
                   textError.setText("  Error: contraseña de usuario vacía.  ");
                   textError.setVisibility(View.VISIBLE); // el textView se puede ver
                   mostrarTextError();// se oculta el mensaje
               }

           }
           else{
               textError.setText("  Error: correo de usuario vacío.  ");
                textError.setVisibility(View.VISIBLE); // el textView se puede ver
                mostrarTextError();// se oculta el mensaje
        }
        });

        // boton de cancelar
        Button btnCancelar = view.findViewById(R.id.buttonCancelar);
        btnCancelar.setOnClickListener(v -> dismiss());

        return builder.create();
    }
    public void mostrarTextError() {
        textError.postDelayed(() -> textError.setVisibility(View.GONE), 2000);
    }
}
