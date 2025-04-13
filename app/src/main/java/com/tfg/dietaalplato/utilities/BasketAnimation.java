package com.tfg.dietaalplato.utilities;

import android.animation.*;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;


import androidx.core.content.ContextCompat;

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

    // Añadirlo al layout raíz de la actividad
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

                // Ocultar todo tras un tiempo
                    new Handler().postDelayed(() -> {
                        rootView.removeView(animView); // eliminar de la vista
                    }, 2000);
                })
                .start();
    }

    /*public static void showAnimation(View animView, Context context, String message) {
        if (!animView.isAttachedToWindow()) {
            animView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    animView.removeOnAttachStateChangeListener(this);
                    startAnimation(animView, context, message);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {}
            });
        } else {
            startAnimation(animView, context, message);
        }
    }

    private static void startAnimation(View animView, Context context, String message) {

        animView.post(() -> {

            Log.d("Animation", "View attached: " + animView.isAttachedToWindow());

            if (animView == null || context == null) return;

            animView.setVisibility(View.VISIBLE);

            ImageView foodImage = animView.findViewById(R.id.food_image);
            ImageView basketImage = animView.findViewById(R.id.basket_image);
            TextView saveMessage = animView.findViewById(R.id.save_message);

            basketImage.setVisibility(View.VISIBLE);
            basketImage.setAlpha(1f);
            saveMessage.setVisibility(View.VISIBLE);
            saveMessage.setAlpha(1f);

            animView.post(() -> {
                String selected = FOOD_IMAGE_NAMES[new Random().nextInt(FOOD_IMAGE_NAMES.length)];
                int imageResId = context.getResources().getIdentifier(selected, "drawable", context.getPackageName());

                Log.d("Animation", "Selected image name: " + selected + ", Resource ID: " + imageResId);

                Drawable drawable = ContextCompat.getDrawable(context, imageResId);
                foodImage.setImageDrawable(drawable);
                foodImage.setVisibility(View.VISIBLE);
                foodImage.setTranslationY(0);
                foodImage.setAlpha(1f);

                Log.d("Animation", "AFTER post - foodImage size: " + foodImage.getWidth() + ", height: " + foodImage.getHeight());
                Log.d("Animation", "AFTER post - basketImage size: " + basketImage.getWidth() + ", height: " + basketImage.getHeight());

                ObjectAnimator drop = ObjectAnimator.ofFloat(foodImage, "translationY", 0f, 600f);
                drop.setDuration(800);
                drop.setInterpolator(new AccelerateInterpolator());

                ObjectAnimator shake = ObjectAnimator.ofFloat(basketImage, "rotation", 0f, -10f, 10f, -8f, 8f, 0f);
                shake.setDuration(400);
                shake.setInterpolator(new CycleInterpolator(2));

                saveMessage.setText(message);
                saveMessage.setVisibility(View.VISIBLE);
                saveMessage.setAlpha(0f);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(saveMessage, "alpha", 0f, 1f);
                fadeIn.setDuration(300);
                fadeIn.setStartDelay(400);

                AnimatorSet hideAll = new AnimatorSet();
                hideAll.setStartDelay(2000);
                hideAll.playTogether(
                        ObjectAnimator.ofFloat(foodImage, "alpha", 1f, 0f),
                        ObjectAnimator.ofFloat(saveMessage, "alpha", 1f, 0f)
                );
                hideAll.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        foodImage.setVisibility(View.GONE);
                        saveMessage.setVisibility(View.GONE);
                        animView.setVisibility(View.GONE);
                        basketImage.setVisibility(View.GONE);
                    }
                });

                AnimatorSet fullAnim = new AnimatorSet();
                fullAnim.playSequentially(drop, shake, fadeIn, hideAll);
                fullAnim.start();
            });
        });
    }*/

}
