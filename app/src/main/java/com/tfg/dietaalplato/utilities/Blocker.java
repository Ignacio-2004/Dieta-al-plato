package com.tfg.dietaalplato.utilities;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class Blocker {

    private static View blocker;

    public static void createBlocker(Activity activity) {
        if (blocker != null) return;

        ViewGroup rootView = activity.findViewById(android.R.id.content);

        // Vista contenedora que cubrirá toda la pantalla
        FrameLayout overlay = new FrameLayout(activity);
        overlay.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        overlay.setBackgroundColor(Color.parseColor("#88000000")); // gris oscuro semitransparente
        overlay.setClickable(true);
        overlay.setFocusable(true);

        // Añadir ProgressBar centrado
        ProgressBar progressBar = new ProgressBar(activity);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        progressParams.gravity = Gravity.CENTER;
        overlay.addView(progressBar, progressParams);

        // Añadir al root
        rootView.addView(overlay);
        blocker = overlay;
    }

    public static void removeBlocker(Activity activity) {
        if (blocker != null) {
            ViewGroup rootView = activity.findViewById(android.R.id.content);
            rootView.removeView(blocker);
            blocker = null;
        }
    }
}
