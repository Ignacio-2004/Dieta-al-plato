package com.tfg.dietaalplato.utilities.dialogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.firebase.exceptions.FBCException;
import com.tfg.dietaalplato.utilities.Blocker;
import com.tfg.dietaalplato.utilities.HeaderColumns;
import com.tfg.dietaalplato.utilities.TableGenerator;

import java.util.ArrayList;

public class DialogHeaders extends DialogFragment {
    private View mainView;
    private ArrayList<HeaderColumns> currentHeaderColumns;
    private ArrayList<HeaderColumns> foreceHeaders;
    private LinearLayout currentLayout;
    private LinearLayout toAddLayout;
    private TableGenerator tableGenerator;
    private ImageButton bttReturn;


    public static DialogHeaders getInstance() {
        return new DialogHeaders();
    }

    private DialogHeaders(){
        tableGenerator = TableGenerator.getInstance();
        currentHeaderColumns = tableGenerator.getColumns();
        foreceHeaders = new ArrayList<>();
        foreceHeaders.add(HeaderColumns.Nombre);
        foreceHeaders.add(HeaderColumns.Alimentos);
        foreceHeaders.add(HeaderColumns.Kcal);
        foreceHeaders.add(HeaderColumns.Proteina);
        foreceHeaders.add(HeaderColumns.HC);
        foreceHeaders.add(HeaderColumns.Grasa);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog_headers, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        currentLayout = mainView.findViewById(R.id.actualLayout);
        toAddLayout = mainView.findViewById(R.id.addLayout);
        bttReturn = mainView.findViewById(R.id.bttReturn);
        bttReturn.setOnClickListener(this::onReturn);

        ScrollView csv = new ScrollView(mainView.getContext());
        csv.setBackgroundColor(Color.TRANSPARENT);
        csv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        ScrollView sv = new ScrollView(mainView.getContext());
        sv.setBackgroundColor(Color.TRANSPARENT);
        sv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        LinearLayout ln = new LinearLayout(mainView.getContext());
        ln.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        ln.setBackgroundColor(Color.TRANSPARENT);
        ln.setOrientation(LinearLayout.VERTICAL);

        LinearLayout cln = new LinearLayout(mainView.getContext());
        cln.setLayoutParams( new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        cln.setBackgroundColor(Color.TRANSPARENT);
        cln.setOrientation(LinearLayout.VERTICAL);


        ArrayList<HeaderColumns> columns = new ArrayList<>(currentHeaderColumns);
        for (HeaderColumns headerColumns: HeaderColumns.values()) {
            if (!columns.contains(headerColumns)){
                columns.add(headerColumns);
            }
        }

        for (HeaderColumns header : columns) {
            if (currentHeaderColumns.contains(header)){
                if (!foreceHeaders.contains(header)){
                    cln.addView(addRermoveBotton(generateList(header, v -> {

                    }),v -> {
                        currentHeaderColumns.remove(header);
                        tableGenerator.setColumns(currentHeaderColumns);
                        ViewGroup parent = (ViewGroup) mainView.getRootView();
                         try {
                            Blocker.createBlocker(parent,requireActivity());
                            tableGenerator.resetAndGenerateTable();
                            Blocker.removeBlocker(parent);
                        } catch (FBCException e) {
                            Toast.makeText(mainView.getContext(), "Error al generar la tabla", Toast.LENGTH_SHORT).show();
                            currentHeaderColumns.add(header);
                            tableGenerator.setColumns(currentHeaderColumns);
                            Blocker.removeBlocker(parent);
                        }
                        dismiss();
                    }));
                }else{
                    cln.addView(generateList(header, v -> {

                    }));
                }
            }else{
               ln.addView(generateList(header, v -> {
                   currentHeaderColumns.add(header);
                   tableGenerator.setColumns(currentHeaderColumns);
                   ViewGroup parent = (ViewGroup) mainView.getRootView();
                   try {
                       Blocker.createBlocker(parent,requireActivity());
                       tableGenerator.resetAndGenerateTable();
                       Blocker.removeBlocker(parent);
                   } catch (FBCException e) {
                       Toast.makeText(mainView.getContext(), "Error al generar la tabla", Toast.LENGTH_SHORT).show();
                       currentHeaderColumns.remove(header);
                       tableGenerator.setColumns(currentHeaderColumns);
                       Blocker.removeBlocker(parent);
                   }
                   dismiss();
               }));
            }
        }

        csv.addView(cln);
        sv.addView(ln);
        currentLayout.addView(csv);
        toAddLayout.addView(sv);

        return builder.create();
    }

    private LinearLayout generateList(HeaderColumns header,View.OnClickListener listener){
        LinearLayout ln = new LinearLayout(mainView.getContext());
        ln.setBackgroundResource(R.drawable.bg_food_background);
        ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setPadding(10, 10, 10, 10);
        ln.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 5, 20, 5);
        ln.setLayoutParams(params);


        TextView tv = new TextView(mainView.getContext());
        tv.setText(header.name());
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextSize(20);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setOnClickListener(listener);
        Typeface customFont = ResourcesCompat.getFont(mainView.getContext(), R.font.lily_script_one);
        if (customFont != null){
            tv.setTypeface(customFont);
        }
        ln.addView(tv);
        return ln;
    }

    private LinearLayout addRermoveBotton(LinearLayout ln,View.OnClickListener listener){

        ImageButton remove = new ImageButton(mainView.getContext());

        remove.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        remove.setBackgroundColor(Color.TRANSPARENT);
        remove.setScaleX(0.8f);
        remove.setScaleY(0.8f);
        remove.setOnClickListener(listener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,0);
        remove.setLayoutParams(params);

        ln.addView(remove);

        return ln;
    }

    public void onReturn(View view){
        dismiss();
    }
}