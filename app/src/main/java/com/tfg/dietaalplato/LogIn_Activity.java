package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.*;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseWriter;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.BasketAnimation;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.SaveData;

public class LogIn_Activity extends AppCompatActivity {

    private static final String TAG = "FirebaseConnection";
    private DatabaseReference databaseReference;


    //variables para gestionar los gmails y sus password
    private EditText correo;
    private EditText passw;
    private TextView text_error;
    private TextView text_passw;
    private ImageButton see_passw;
    private ImageButton hide_passw;
    private FireBaseConnector DateBase;
    private SaveData saveData;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Prueba para guardar y leer un usuario (puedes quitarlo más adelante)
        //  DateBase.saveUser(new User("USE002", "Patatudo", "123456"));
        //  DateBase.readUsuario("USE002");
        try {
            // inicializamos la conexión con Firebase
            DateBase = FireBaseConnector.getInstance();
            DateBase.testFirebaseConnection();
            DateBase.monitorConnectionStatus();


        } catch (FBCException e) {
            throw new RuntimeException(e);
        }

        saveData = SaveData.getInstance();
        saveData.clear();

    }

    public void onClick(View view) {
       /* Intent intent = new Intent(this, InicioUsuarioActivity.class );
          startActivity(intent); */
        BasketAnimation.showAnimation(this);
    }




    // metodo para mostrar el dialogo cuando se hace clic sign up
    public void signUp(View view) {
        SignUp_Dialogo dialogo = new SignUp_Dialogo();
        dialogo.show(getSupportFragmentManager(), "dialogoNuevoUsuario");
    }

    public void onClickTest(View view) {
        ValidationResult result = new ValidationResult();
        result.exit = true;
        result.message = "Datos válidos";

// Añadir campos en orden
        result.data.put("idUsr", "USU0002");
        result.data.put("name", "Pollo");
        result.data.put("pc", "100");
        result.data.put("energia", "239");
        result.data.put("proteina", "27");
        result.data.put("grasa", "14");
        result.data.put("ags", "3.8");
        result.data.put("agmi", "5.3");
        result.data.put("agpi", "3.6");
        result.data.put("colesterol", "85");
        result.data.put("hc", "0");
        result.data.put("fibra", "0");
        result.data.put("vitC", "0");
        result.data.put("vitB6", "0.5");
        result.data.put("vitE", "0.3");
        result.data.put("hierro", "1");
        result.data.put("sodio", "70");
        result.data.put("calcio", "12");
        result.data.put("potasio", "223");

// Guardar el alimento
        FireBaseWriter.saveData(Food.class, result).addOnSuccessListener(
                objectResult -> Log.d("FireBaseWriter", "✅ Datos del alimento guardados correctamente")
        ).addOnFailureListener(
                e -> Log.e("FireBaseWriter", "❌ Error al guardar el alimento: " + e.getMessage())
        );

    }


    /* metodo para que el usuario se logue
        1 - se comprueba que el correo sea el adecuado
     */
    public void login(View view) throws FBCException {

        Blocker.createBlocker(this);

        text_error = findViewById(R.id.text_error);// guaardamos en la variable la referencia dele textView para los errores

        correo = findViewById(R.id.text_usuario); //almacenamos lo que ha introducio el usario
        String correo_introducido = correo.getText().toString().trim().toLowerCase(); // lo ponemos en tipo String
        String emailRegex = "^[a-zA-Z]+(\\.[a-zA-Z]+)?@educa\\.madrid\\.org$";

        passw = findViewById(R.id.Password); // almacenamos lo que ha introducido el usuario
        String passwd_introducida = passw.getText().toString().trim(); // lo ponemos en tipo String

        //Expresiones regulares para gmail y password
       /*
          Mínimo 8 caracteres
          Máximo 12 caracteres
          Al menos una letra mayúscula
          Al menos un número
          Al menos un símbolo especial de entre estos: !, -, _, #


          Ejemplo válido: Develop1!
        */
        String passwRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-_#])[A-Za-z\\d!\\-_#]{8,12}$";


        // COMPROBAMOS CORREo
        if (!correo_introducido.isEmpty()) // ha puesto el usuario
        {
            //COMPROBAMOS QUE SEA UN CORREO CORRECTO
            //nombre.apellidos@educa.madrid.org

            if (correo_introducido.matches(emailRegex)) // es correcto
            {
                // VERIFICAMOS SI EL USUARIO EXISTE EN FIREBASE
                DateBase.verifyUser(correo_introducido)
                        .addOnSuccessListener(isUserExists -> {
                            if (isUserExists) {// El usuario existe ✅

                                // COMPROBAMOS PASSWORD
                                if (!passwd_introducida.isEmpty()) {
                                    try {
                                        FireBaseReader.readUserByEmail(correo_introducido)
                                                .addOnSuccessListener(usuario -> {
                                                    String passwDB = usuario.getPsw();  // Accedemos a la contraseña de la BD

                                                    if(passwDB.matches(passwRegex)){ // Contraseña correcta

                                                        saveData.setUser(usuario);//Guardamos el usuario que se logea

                                                        if (passwd_introducida.equals(passwDB)) {
                                                            if (correo_introducido.equals("admin.admin@educa.madrid.org")){
                                                                Intent i = new Intent(this, InicioAdminActivity.class);
                                                                Blocker.removeBlocker(this);
                                                                startActivity(i);
                                                                finish(); // Cerramos Login para que no se pueda volver atrás
                                                            }
                                                            else {
                                                                Intent i = new Intent(this, InicioUsuarioActivity.class);
                                                                Blocker.removeBlocker(this);
                                                                startActivity(i);
                                                                finish(); // Cerramos Login para que no se pueda volver atrás
                                                            }
                                                        } else {
                                                            // Contraseña incorrecta
                                                            text_error.setText("Contraseña incorrecta.");
                                                            text_error.setVisibility(View.VISIBLE);
                                                            mostrarTextError();
                                                        }

                                                    }
                                                    else {
                                                        text_error.setText(" Error: La contraseña debe tener entre 8 y 12 caracteres, con al menos una mayúscula, un número y un símbolo especial (- _ # !).");
                                                        text_error.setVisibility(View.VISIBLE);
                                                        mostrarTextError();
                                                    }


                                                })
                                                .addOnFailureListener(e -> {
                                                    // Usuario no encontrado o error en la base de datos
                                                    text_error.setText("Usuario no encontrado.");
                                                    text_error.setVisibility(View.VISIBLE);
                                                    mostrarTextError();
                                                });
                                    } catch (FBCException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else {
                                    text_error.setText("  Error: contraseña de usuario vacía.  ");
                                    text_error.setVisibility(View.VISIBLE); // el textView se puede ver
                                    mostrarTextError();// se oculta el mensaje
                                }

                            } else { // No existe el usuario ❌
                                text_error.setText("  Error: correo no resgistrado ");
                                text_error.setVisibility(View.VISIBLE); // el textView se puede ver
                                mostrarTextError();// se oculta el mensaje
                            }
                        }).addOnFailureListener(e -> {
                            // Manejo de error
                            e.printStackTrace();
                            text_error.setText("Error al verificar el usuario.");
                            text_error.setVisibility(View.VISIBLE);
                            mostrarTextError();
                        });

            } else {
                text_error.setText("  Error: correo de usuario debe tener la estructura [nombre.apellidos@educa.madrid.org].  ");
                text_error.setVisibility(View.VISIBLE); // el textView se puede ver
                mostrarTextError();// se oculta el mensaje
            }

        } else // no ha puesto el gmail
        {
            text_error.setText("  Error: correo de usuario vacío.  ");
            text_error.setVisibility(View.VISIBLE); // el textView se puede ver
            mostrarTextError();// se oculta el mensaje
        }
    }

    public void mostrarTextError() {
        // ocultamos el mensaje después de 2 segundos
        text_error.postDelayed(new Runnable() {
            @Override
            public void run() {
                text_error.setVisibility(View.GONE); // text view desaparece
            }
        }, 2000);
        Blocker.removeBlocker(this);
    }

    public void mostrarPassword(View view) {
        text_passw = findViewById(R.id.Password);
        text_passw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        see_passw = findViewById(R.id.buttonSeePasswd);
        hide_passw = findViewById(R.id.buttonHidePasswd);
        see_passw.setVisibility(View.GONE);
        hide_passw.setVisibility(View.VISIBLE);
    }

    public void ocultarPassword(View view) {
        text_passw = findViewById(R.id.Password);
        text_passw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ImageButton see = findViewById(R.id.buttonSeePasswd);
        ImageButton hide = findViewById(R.id.buttonHidePasswd);
        see.setVisibility(View.VISIBLE);
        hide.setVisibility(View.GONE);
    }

}
