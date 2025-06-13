package com.tfg.dietaalplato.firebase.utilities;

import android.util.Log;

import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.RelacionRecetaAlimento;
import com.tfg.dietaalplato.firebase.tables.User;

public class  ClassUtilities {

    public static ClassData collectionData(Class<?> classType){
       ClassData data = null;

        if (classType.equals(Diet.class)) data = new ClassData(String.valueOf(IdNames.DIE),String.valueOf(TablesNames.dietas));
        else if (classType.equals(Client.class)) data = new ClassData(String.valueOf(IdNames.CLI),String.valueOf(TablesNames.clientes));
        else if (classType.equals(User.class)) data = new ClassData(String.valueOf(IdNames.USU),String.valueOf(TablesNames.usuarios));
        else if (classType.equals(Food.class)) data = new ClassData(String.valueOf(IdNames.ALI),String.valueOf(TablesNames.alimentos));
        else if (classType.equals(FoodDiet.class)) data = new ClassData(String.valueOf(IdNames.FDI),String.valueOf(TablesNames.comidaDietas));
        else if (classType.equals(RelacionRecetaAlimento.class)) data = new ClassData(String.valueOf(IdNames.RRA),String.valueOf(TablesNames.recetaAlimento));
        else data = new ClassData("Error","Dato no soportado");

        return data;
    }

    public static String generateId(ClassData classData, int amount, String idNumExt){
        Log.d("FireBase", "generateId: "+amount);
        String id = String.valueOf(amount+1);
        String date = String.valueOf(System.currentTimeMillis());

        while (id.length()<4){
            id = "0" + id;
        }

        if (idNumExt.split("0").length>1){
            Log.d("FireBase", "generateId: "+date+classData.key+idNumExt.substring(idNumExt.length()-4)+id);
            return classData.key+date+idNumExt.substring(idNumExt.length()-4)+id;
        }

        return classData.key+date+id;

    }

}
