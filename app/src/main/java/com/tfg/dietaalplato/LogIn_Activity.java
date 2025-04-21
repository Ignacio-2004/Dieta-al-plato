package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.*;
import com.tfg.dietaalplato.object.User;
import com.tfg.dietaalplato.utilities.BasketAnimation;
import com.tfg.dietaalplato.utilities.FireBaseConnector;
import com.tfg.dietaalplato.utilities.exception.FBCException;

public class LogIn_Activity extends AppCompatActivity {

    private static final String TAG = "FirebaseConnection";
    private DatabaseReference databaseReference;


    //variables para gestionar los gmails y sus password
    private EditText gmail;
    private EditText passw;
    private TextView text_error;
    private FireBaseConnector DateBase;

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



        try {
            FireBaseConnector database = FireBaseConnector.getInstance();
            database.testFirebaseConnection();
            database.monitorConnectionStatus();

            database.saveUser(new User("USE002","Patatudo","123456"));
            database.readUsuario("USE002");

        } catch (FBCException e) {
            throw new RuntimeException(e);
        }

    }

    public void onClick(View view) {
       /* Intent intent = new Intent(this, InicioUsuarioActivity.class );
        startActivity(intent);*/

        BasketAnimation.showAnimation(this);
    }
    /* metodo para que el usuario se logue
        1 - se comprueba que el correo sea el adecuado
     */
    public void login (View view) throws FBCException {
        text_error = findViewById(R.id.text_error);// guaardamos en la variable la referencia dele textView para los errores

        gmail = findViewById(R.id.text_usuario); //almacenamos lo que ha introducio el usario
        String gmail_introducido = gmail.getText().toString(); // lo ponemos en tipo String

        passw = findViewById(R.id.Password); // almacenamos lo que ha introducido el usuario
        String passwd_introducida = passw.getText().toString(); // lo ponemos en tipo String

        //Expresiones regulares para gmail y password
        /*
           Mínimo 8 caracteres
           Máximo 12 caracteres
           Al menos una letra mayúscula
           Al menos un número
           Al menos un símbolo especial de entre estos: !, -, _, #

           Develop1!
         */
        String passwRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-_#])[A-Za-z\\d!\\-_#]{8,12}$";
        String emailRegex = "^[a-zA-Z]+(\\.[a-zA-Z]+)?@educa\\.madrid\\.org$";

        // COMPROBAMOS CORREo
        if (!gmail_introducido.isEmpty()) // ha puesto el usuario
        {
            //COMPROBAMOS QUE SEA UN CORREO CORRECTO
            //nombre.apellidos@educa.madrid.org

            if(gmail_introducido.matches(emailRegex)) // es correcto
            {
                if(DateBase.verifyUser(gmail_introducido)) // esta registrado
                {
                    // COMPROBAMOS PASSWORD
                    if(!passwd_introducida.isEmpty())
                    {
                        DateBase.readUsuario(gmail_introducido)
                                .addOnSuccessListener(usuario -> {
                                    String passwDB = usuario.getPsw();  // Accedemos a la contraseña de la BD

                                    if (passwd_introducida.equals(passwDB)) {
                                        // Contraseña correcta
                                        Intent i = new Intent(this, InicioUsuarioActivity.class);
                                        startActivity(i);
                                        finish(); // Cerramos Login para que no se pueda volver atrás
                                    } else {
                                        // Contraseña incorrecta
                                        text_error.setText("Contraseña incorrecta.");
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

                    }else{
                        text_error.setText("  Error: contraseña de usuario vacía.  ");
                        text_error.setVisibility(View.VISIBLE); // el textView se puede ver
                        mostrarTextError();// se oculta el mensaje
                    }
                }
                else{// no esta registrado en la BD

                }
            }
            else{
                text_error.setText("  Error: gmail de usuario debe tener la estructura [nombre.apellidos@educa.madrid.org].  ");
                text_error.setVisibility(View.VISIBLE); // el textView se puede ver
                mostrarTextError();// se oculta el mensaje
            }

        }
        else // no ha puesto el gmail
        {
            text_error.setText("  Error: gmail de usuario vacío.  ");
            text_error.setVisibility(View.VISIBLE); // el textView se puede ver
            mostrarTextError();// se oculta el mensaje

        }
    }
    public void mostrarTextError()
    {
        // ocultamos el mensaje después de 2 segundos
        text_error.postDelayed(new Runnable() {
            @Override
            public void run() {
                text_error.setVisibility(View.GONE); // text view desaparece
            }
        }, 2000);
    }
}