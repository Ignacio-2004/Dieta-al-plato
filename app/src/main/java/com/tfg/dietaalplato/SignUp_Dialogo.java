package com.tfg.dietaalplato;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.FirebaseApp;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.utilities.ValidationResult;

import java.util.ArrayList;

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
                               //++IP -05/05/2025-

                               ArrayList<View> views = new ArrayList<>();

                               views.add(correo);
                               views.add(password);

                               ValidationResult userData = User.toMapData(views);

                               //--IP -05/05/2025-

                               /* Alex 05/05/2025
                               HashMap<String, String> userData = new HashMap<>();
                               userData.put("name", correo_introducido);
                               userData.put("psw", passw_introducida);
                                */

                               //try {
                                   // inicializamos la conexión con Firebase
                                   DateBase = FireBaseConnector.getInstance();

/*                                 Esto lo comento (Ignacio) para no consuimir datos de la tablet y que valla mas fluido
                                   DateBase.testFirebaseConnection();
                                   DateBase.monitorConnectionStatus();*/

                                   /* Alex 05/05/2025
                                   // llamamos al metodo saveData pero sin forzar a sobreescribir ya que ya comprobamos que no haya un usuario con ese correo
                                   ValidationResult result = new ValidationResult();
                                   result.exit = true; // Datos validados
                                   result.message = correo_introducido;
                                   result.data = userData;
                                    */


                                   // guardamos los datos en la base de datos
                                   //++IP -07/05/2025-

                                   if (FirebaseApp.getApps(this.getActivity()).isEmpty()) {
                                       Log.e("FireBase", "Firebase no se ha inicializado");
                                   } else {
                                       Log.d("FireBase", "✅ Firebase está inicializado");
                                   }

                                   DateBase.saveData(User.class,userData).addOnSuccessListener(
                                           aVoid -> {
                                               textError.setText("Usuario guardado correctamente.");
                                               textError.setVisibility(View.VISIBLE);
                                               mostrarTextError();
                                           }
                                   ).addOnFailureListener(
                                           e -> {
                                               textError.setText("Error al guardar los datos: " + e.getMessage());
                                               textError.setVisibility(View.VISIBLE);
                                               mostrarTextError();
                                           }
                                   );
                                   //--IP -07/05/2025
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
