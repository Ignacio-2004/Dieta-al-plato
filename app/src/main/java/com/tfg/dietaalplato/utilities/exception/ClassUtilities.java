package com.tfg.dietaalplato.utilities.exception;

import com.tfg.dietaalplato.object.*;
import com.tfg.dietaalplato.utilities.ClassData;

public class  ClassUtilities {

    public static ClassData collectionData(Class<?> classType){
       ClassData data = null;

        if (classType.equals(Diet.class)) data = new ClassData("DIE","dietas");
        else if (classType.equals(Client.class)) data = new ClassData("CLI","clientes");
        else if (classType.equals(User.class)) data = new ClassData("USU","usuarios");
        else if (classType.equals(Food.class)) data = new ClassData("ALI","comidas");
        else if (classType.equals(FoodDiet.class)) data = new ClassData("FDI","dietaAlimentos");
        else data = new ClassData("Error","Dato no soportado");

        return data;
    }

    public static String generateId(ClassData classData, String amount){

        while (amount.length()<4){
            amount = "0" + amount;
        }

        return classData.key+amount;

    }


}
