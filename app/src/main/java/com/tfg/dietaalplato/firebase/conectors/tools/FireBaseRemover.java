package com.tfg.dietaalplato.firebase.conectors.tools;

import android.telephony.TelephonyCallback;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;
import com.tfg.dietaalplato.utilities.SaveData;
import com.tfg.dietaalplato.utilities.tipe_collection.CacheCollection;

import java.io.DataInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class FireBaseRemover {
    private static final String TAG = "FireBase/Remover";

    public static Task<ValidationResult> remove(String id) {
        Log.d(TAG, "🗑️ Eliminando elemento con id: " + id);
        TaskCompletionSource<ValidationResult> taskCompletionSource = new TaskCompletionSource<>();

        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();
        SaveData saveData = SaveData.getInstance();

        String prefix = id.substring(0, 3);
        String collection = null;
        String successMsg = "Elemento eliminado correctamente";
        String errorMsg = "Error al eliminar el elemento";

        switch (prefix) {
            case "USU":
                collection = "usuarios";
                successMsg = "Usuario eliminado correctamente";
                errorMsg = "Error al eliminar el usuario";
                break;
            case "CLI":
                collection = "clientes";
                successMsg = "Cliente eliminado correctamente";
                errorMsg = "Error al eliminar el cliente";
                break;
            case "DIE":
                collection = "dietas";
                successMsg = "Dieta eliminada correctamente";
                errorMsg = "Error al eliminar la dieta";
                break;
            case "FDI":
                collection = "dietaAlimentos";
                successMsg = "Alimento eliminado correctamente";
                errorMsg = "Error al eliminar el alimento";
                break;
            case "ALI":
                collection = "comidas";
                successMsg = "Alimento eliminado correctamente";
                errorMsg = "Error al eliminar el alimento";
                break;
            default:
                taskCompletionSource.setResult(new ValidationResult(false, "Prefijo no soportado", null));
                return taskCompletionSource.getTask();
        }

        String finalSuccessMsg = successMsg;
        String finalErrorMsg = errorMsg;
        String finalCollection = collection;
        fst.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid ->
                        {
                            taskCompletionSource.setResult(new ValidationResult(true, finalSuccessMsg, null));
                            switch (finalCollection) {
                                case "clientes":
                                    for(Client client: saveData.getClients().getAllAsArrayList()){
                                        if(client.getId().equals(id)){
                                            saveData.removeClient(client.getName());
                                        }
                                    }
                                    break;
                                case "dietas":
                                    for (Map<String, Diet> diet : saveData.getDiets().getAllAsArrayList()){
                                        for (Diet diet1: diet.values()){
                                            if(diet1.getId().equals(id)){
                                                saveData.removeDiet(diet1.getName());
                                            }
                                        }
                                    }
                                    break;
                                case "dietaAlimentos":

                                    CacheCollection<Map<String, Diet>> foodDiet = saveData.getDiets();
                                    for (Map<String, Diet> dietMap: foodDiet.getAllAsArrayList()){
                                        for (Diet diet: dietMap.values()){
                                            if(diet.getId().equals(id)){
                                                saveData.removeDiet(diet.getName());
                                            }
                                        }
                                    }
                                    break;
                                case "comidas":
                                    for (Food food: saveData.getFoods().getAllAsArrayList()){
                                        if(food.getId().equals(id)){
                                            saveData.removeFood(food.getName());
                                        }
                                    }
                                    break;
                            }
                        }
                ).addOnFailureListener(e ->
                        taskCompletionSource.setResult(new ValidationResult(false, finalErrorMsg, null))
                );
        Log.d(TAG, "🏁 Fin del proceso de eliminado");
        return taskCompletionSource.getTask();
    }
}
