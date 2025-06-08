package com.tfg.dietaalplato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.DietaTableGenerator;
import com.tfg.dietaalplato.utilities.HeaderColumns;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.dialogo.DialogAddJustification;
import com.tfg.dietaalplato.utilities.TableGenerator;
import com.tfg.dietaalplato.utilities.dialogo.DialogScrollView;

import java.util.ArrayList;

public class DietaIDia extends AppCompatActivity {

    SaveData saveData = SaveData.getInstance();
    private LinearLayout layoutSV;
    DietaTableGenerator generator;
    private TableGenerator tableGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dieta_dia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tableGenerator = TableGenerator.getInstance();
        layoutSV = findViewById(R.id.layoutTabla);


        try {
            tableGenerator.generarTabla(this, layoutSV, onClickAddHeader -> {
                ArrayList<HeaderColumns> columns = tableGenerator.getColumns();
                DialogScrollView dialog = DialogScrollView.getHeaderInstance(columns);
                dialog.show(getSupportFragmentManager(), "DialogScrollView");
            });
        } catch (FBCException e) {
            Toast.makeText(this, "No es posible generar la tabla", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickReturn(View view){
        Intent intent = new Intent(this, ComidasActivity.class);
        startActivity(intent);
    }

    public void just(View view){
        DialogAddJustification dialog = new DialogAddJustification();
        dialog.show(getSupportFragmentManager(), "DialogAddJustification");
    }
}