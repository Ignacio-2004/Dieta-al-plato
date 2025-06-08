package com.tfg.dietaalplato;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tfg.dietaalplato.firebase.conectors.tools.FireBaseReader;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.SaveData;

public class InicioAdminActivity extends AppCompatActivity {

    private LinearLayout layoutUsuarios;
    private FireBaseConnector fbConnector; // tu conector personalizado
    private SaveData saveData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_admin);

        layoutUsuarios = findViewById(R.id.layoutUsuarios);

        saveData = SaveData.getInstance();
        saveData.clearAdmin();

        try {
            FireBaseReader.readAllFromCollection("usuarios", User.class)
                    .addOnSuccessListener(usuarios -> {
                        for (User usuario : usuarios) {
                            LinearLayout item = new LinearLayout(this);
                            item.setOrientation(LinearLayout.HORIZONTAL);
                            item.setPadding(12, 5, 5, 12);
                            item.setElevation(8f);
                            layoutUsuarios.addView(item);

                            item = new LinearLayout(this);
                            item.setOrientation(LinearLayout.HORIZONTAL);
                            item.setBackgroundResource(R.drawable.bg_food_background);
                            item.setPadding(12, 12, 12, 12);
                            item.setElevation(8f);

                            TextView nombre = new TextView(this);
                            nombre.setText(usuario.getName().toUpperCase());
                            nombre.setTextColor(Color.WHITE);
                            nombre.setTextSize(30);
                            nombre.setGravity(Gravity.CENTER);
                            nombre.setTypeface(null, Typeface.BOLD);
                            nombre.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            ));

                            item.setOnClickListener(v ->{
                                        Log.d("Usuario", "Nombre del usuario: " + usuario.getName());
                                        saveData.setCurrentStudent(usuario);

                                        Intent intent = new Intent(this, InicioUsuarioActivity.class);
                                        startActivity(intent);
                                    }
                            );
                            item.addView(nombre);
                            layoutUsuarios.addView(item);
                        }
                    });
        } catch (FBCException e) {
            Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickBackNavigation(View view){
        Intent intent = new Intent(this, LogIn_Activity.class );
        startActivity(intent);
    }
}