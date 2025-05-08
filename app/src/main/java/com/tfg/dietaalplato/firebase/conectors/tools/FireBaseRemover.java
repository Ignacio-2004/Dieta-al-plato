package com.tfg.dietaalplato.firebase.conectors.tools;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tfg.dietaalplato.firebase.conectors.FireBaseConnector;
import com.tfg.dietaalplato.firebase.utilities.ValidationResult;

public class FireBaseRemover {
    private static final String TAG = "FireBase/Remover";

    public static Task<ValidationResult> remove(String id) {
        TaskCompletionSource<ValidationResult> taskCompletionSource = new TaskCompletionSource<>();

        FirebaseFirestore fst = FireBaseConnector.getInstance().getFirestore();

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
        fst.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid ->
                        taskCompletionSource.setResult(new ValidationResult(true, finalSuccessMsg, null))
                ).addOnFailureListener(e ->
                        taskCompletionSource.setResult(new ValidationResult(false, finalErrorMsg, null))
                );
        return taskCompletionSource.getTask();
    }
}
