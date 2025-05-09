package com.tfg.dietaalplato.firebase.utilities;

import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;

public class  ClassUtilities {

    public static ClassData collectionData(Class<?> classType){
       ClassData data = null;

        if (classType.equals(Diet.class)) data = new ClassData(String.valueOf(IdNames.DIE),String.valueOf(TablesNames.dietas));
        else if (classType.equals(Client.class)) data = new ClassData(String.valueOf(IdNames.CLI),String.valueOf(TablesNames.clientes));
        else if (classType.equals(User.class)) data = new ClassData(String.valueOf(IdNames.USU),String.valueOf(TablesNames.usuarios));
        else if (classType.equals(Food.class)) data = new ClassData(String.valueOf(IdNames.ALI),String.valueOf(TablesNames.alimentos));
        else if (classType.equals(FoodDiet.class)) data = new ClassData(String.valueOf(IdNames.FDI),String.valueOf(TablesNames.comidaDietas));
        else data = new ClassData("Error","Dato no soportado");

        return data;
    }

    public static String generateId(ClassData classData, int amount){

       String id = String.valueOf(amount+1);

        while (id.length()<4){
            id = "0" + id;
        }

        return classData.key+id;

    }


}
