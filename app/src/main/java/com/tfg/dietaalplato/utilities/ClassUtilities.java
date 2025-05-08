package com.tfg.dietaalplato.utilities;

import com.tfg.dietaalplato.object.*;

public class  ClassUtilities {

    public static ClassData collectionData(Class<?> classType){
       ClassData data = null;

        if (classType.equals(Diet.class)) data = new ClassData("DIE","dietas");
        else if (classType.equals(Client.class)) data = new ClassData("CLI","clientes");
        else if (classType.equals(User.class)) data = new ClassData("USU","usuarios");
        else if (classType.equals(Food.class)) data = new ClassData("ALI","alimentos");
        else if (classType.equals(FoodDiet.class)) data = new ClassData("FDI","comidaDietas");
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
