package com.tfg.dietaalplato.utilities;

import android.animation.*;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.tfg.dietaalplato.R;

import java.util.Random;

public class BasketAnimation {


    private static final int[] ALIMENTOS = {
            R.drawable.carrot,
            R.drawable.milk,
            R.drawable.apple,
            R.drawable.bread,
    };


    public static void showAnimation(Activity actualActivity) {

    // Inflar el layout de la animación
        LayoutInflater inflater = LayoutInflater.from(actualActivity);
        View animView = inflater.inflate(R.layout.animated_basket_view, null);

    /*
     * Añadirlo al layout raíz de la actividad
     * Creamos un grupo de layouts inflados
     */
        ViewGroup rootView = actualActivity.findViewById(android.R.id.content);
        rootView.addView(animView);

    // Obtener referencias
        FrameLayout animContainer = animView.findViewById(R.id.basket_anim_container);
        ImageView foodImage = animView.findViewById(R.id.food_image);
        ImageView basketImage = animView.findViewById(R.id.basket_image);
        TextView saveMessage = animView.findViewById(R.id.save_message);

    //Alimento random
        int randomIndex = new Random().nextInt(ALIMENTOS.length);
        foodImage.setImageResource(ALIMENTOS[randomIndex]);

    // Mostrar
        animContainer.setVisibility(View.VISIBLE);
        foodImage.setVisibility(View.VISIBLE);

    // Reiniciar posiciones
        foodImage.setTranslationY(0);
        basketImage.setRotation(0f);

    // Animación de caída del alimento
        foodImage.animate()
                .translationYBy(800f)
                .setDuration(800)
                .withEndAction(() -> {
                // Animación de sacudida de la cesta
                    ObjectAnimator shake = ObjectAnimator.ofFloat(basketImage, "rotation", 0f, -10f, 10f, -10f, 10f, 0f);
                    shake.setDuration(600);
                    shake.start();

                // Mostrar mensaje
                    saveMessage.setVisibility(View.VISIBLE);
                    saveMessage.setAlpha(0f);
                    saveMessage.animate().alpha(1f).setDuration(300).start();

                // Ocultar tdo tras un tiempo
                    new Handler().postDelayed(() -> {
                        rootView.removeView(animView); // eliminar de la vista
                    }, 1000);
                })
                .start();
    }
}
