package com.tfg.dietaalplato.utilities.dialogo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.tfg.dietaalplato.R;
import com.tfg.dietaalplato.utilities.HeaderColumns;
import com.tfg.dietaalplato.utilities.TableGenerator;

import java.util.ArrayList;

public class DialogHeaders extends DialogFragment {
    private View mainView;
    private ArrayList<HeaderColumns> currentHeaderColumns;
    private LinearLayout currentLayout;
    private LinearLayout toAddLayout;
    private TableGenerator tableGenerator;

    public static DialogHeaders getInstance() {
        return new DialogHeaders();
    }

    private DialogHeaders(){
        tableGenerator = TableGenerator.getInstance();
        currentHeaderColumns = tableGenerator.getColumns();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog_headers, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        currentLayout = mainView.findViewById(R.id.actualLayout);
        toAddLayout = mainView.findViewById(R.id.addLayout);


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
                cln.addView(generateList(header));
            }else{
               ln.addView(generateList(header));
            }
        }

        csv.addView(cln);
        sv.addView(ln);
        currentLayout.addView(csv);
        toAddLayout.addView(sv);

        return builder.create();
    }

    private LinearLayout generateList(HeaderColumns header){
        LinearLayout ln = new LinearLayout(mainView.getContext());
        ln.setBackgroundResource(R.drawable.bg_food_background);
        ln.setOrientation(LinearLayout.HORIZONTAL);
        ln.setPadding(10, 10, 10, 10);
        ln.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 5, 20, 5); // izquierda, arriba, derecha, abajo
        ln.setLayoutParams(params);


        TextView tv = new TextView(mainView.getContext());
        tv.setText(header.name());
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        Typeface customFont = ResourcesCompat.getFont(mainView.getContext(), R.font.lily_script_one);
        if (customFont != null){
            tv.setTypeface(customFont);
        }
        ln.addView(tv);
        return ln;
    }
}