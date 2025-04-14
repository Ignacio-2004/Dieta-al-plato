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

        // 🔒 Crear una vista bloqueadora para desactivar interacciones
        View blocker = new View(actualActivity);
        blocker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        blocker.setBackgroundColor(0x00000000); // totalmente transparente
        blocker.setClickable(true); // intercepta clics
        blocker.setFocusable(true); // intercepta enfoque

        // Añadir la vista bloqueadora y la animación al layout raíz
        rootView.addView(blocker);
        rootView.addView(animView);

        // Obtener referencias a los elementos de la animación
        FrameLayout animContainer = animView.findViewById(R.id.basket_anim_container);
        ImageView foodImage = animView.findViewById(R.id.food_image);
        ImageView basketImage = animView.findViewById(R.id.basket_image);
        TextView saveMessage = animView.findViewById(R.id.save_message);

        // Escoger un alimento al azar
        int randomIndex = new Random().nextInt(ALIMENTOS.length);
        foodImage.setImageResource(ALIMENTOS[randomIndex]);

        // Mostrar el contenedor de la animación y el alimento
        animContainer.setVisibility(View.VISIBLE);
        foodImage.setVisibility(View.VISIBLE);

        // Reiniciar posiciones iniciales
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

                    // Mostrar el mensaje de guardado
                    saveMessage.setVisibility(View.VISIBLE);
                    saveMessage.setAlpha(0f);
                    saveMessage.animate().alpha(1f).setDuration(300).start();

                    // Ocultar todo tras un breve retardo
                    new Handler().postDelayed(() -> {
                        rootView.removeView(animView);   // Eliminar la animación de la vista
                        rootView.removeView(blocker);    // Quitar el bloqueo de interacciones
                    }, 1000);
                }).start();
    }


}
