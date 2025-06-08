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
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.utilities.HeaderColumns;

import java.util.ArrayList;
import java.util.Formattable;
import java.util.HashMap;
import java.util.Map;

public class DialogScrollView extends DialogFragment {
    private View mainView;
    private static String whatIsIt;
    private static ArrayList<HeaderColumns> currentHeaderColumns;
    private static ArrayList<Food> currentFoods;
    private LinearLayout currentLayout;
    private LinearLayout toAddLayout;

    public static DialogScrollView getHeaderInstance(ArrayList<HeaderColumns> currentHeaderColumns) {
        whatIsIt = "Headers";
        DialogScrollView.currentHeaderColumns = currentHeaderColumns;
        return new DialogScrollView();
    }

    public static DialogScrollView getFoodInstance(ArrayList<Food> currentFoods) {
        whatIsIt = "Foods";
        DialogScrollView.currentFoods = currentFoods;
        return new DialogScrollView();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainView = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog_scroll_view, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mainView)
                .setCancelable(true);

        currentLayout = mainView.findViewById(R.id.actualLayout);
        toAddLayout = mainView.findViewById(R.id.addLayout);


        ScrollView csv = new ScrollView(mainView.getContext());
        csv.setBackgroundColor(Color.TRANSPARENT);
        csv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        );

        ScrollView sv = new ScrollView(mainView.getContext());
        sv.setBackgroundColor(Color.TRANSPARENT);
        sv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        );

        switch (whatIsIt){
            case "Headers":

                for (HeaderColumns header : HeaderColumns.values()) {
                    if (currentHeaderColumns.contains(header)){
                        csv.addView(generateList(header));
                        csv.addView(setMargin());
                    }else{
                        sv.addView(generateList(header));
                        sv.addView(setMargin());
                    }
                }

                break;
            case "Foods":
                break;
                default:
                break;
        }

        currentLayout.addView(csv);
        toAddLayout.addView(sv);

        return builder.create();
    }

    private LinearLayout setMargin(){
        LinearLayout margin = new LinearLayout(mainView.getContext());
        margin.setPadding(10,10,10,10);
        margin.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );
        return margin;
    }

    private LinearLayout generateList(HeaderColumns header){
        LinearLayout ln = new LinearLayout(mainView.getContext());
        ln.setBackgroundResource(R.drawable.bg_food_background);
        ln.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        TextView tv = new TextView(mainView.getContext());
        tv.setText(header.name());
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextSize(30);
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