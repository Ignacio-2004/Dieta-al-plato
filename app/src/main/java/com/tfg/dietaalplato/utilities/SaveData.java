package com.tfg.dietaalplato.utilities;

import android.util.Log;

import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;
import com.tfg.dietaalplato.utilities.tipe_collection.CacheCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Esta clase nacio por la necesidad de poder acceder desde todas las clases a los datos de la persona que esta logada, esta clase es un singleton y segun se van pidiendo datos a Firestore estos se van guardadndo en sus respectivas listas.

 * Dietas y foosdiets son diferentes tienen un map dentro con el objetivo de utilizar la primera Key para el idCLient | idDieta y el segundo para el nombre
 *
 * la coleccion de FoodDiet tiene una arraylist porque hay alimentos que estan con el mismo nombre
 */

public class SaveData {

    private static SaveData saveData;
    private User user;
    private boolean admin;
    private CacheCollection<User> students;
    private CacheCollection<Client> clients;
    private CacheCollection<Food> foods;
    private CacheCollection<Map<String, Diet>> diets;
    private CacheCollection<Map<String, ArrayList<FoodDiet>>> foodDiets;

    private User currentStudent;
    private Client currentClient;
    private Diet currentDiet;
    private FoodDiet currentFood;
    private int currentDay;
    private String momentOfDay;
    private String day;
    private final String TAG = "SaveData";

    private SaveData() {
        Log.d(TAG, "SaveData instanciando");
        user = new User("","","");
        students = new CacheCollection<>();
        clients = new CacheCollection<>();
        foods = new CacheCollection<>();
        diets = new CacheCollection<>();
        foodDiets = new CacheCollection<>();
        currentStudent = new User();
        currentClient = new Client();
        currentDiet = new Diet();
        currentFood = new FoodDiet();
        day = "";
        momentOfDay = "";
    }

    public static SaveData getInstance() {
        if (saveData == null) {
            saveData = new SaveData();
        }
        return saveData;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMomentOfDay() {
        return momentOfDay;
    }

    public void setMomentOfDay(String momentOfDay) {
        this.momentOfDay = momentOfDay;
    }

    public Client getCurrentClient() {
        Log.d(TAG, "getCurrentClient: " + currentClient);
        return currentClient;
    }

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
        Log.d(TAG, "setCurrentClient: " + currentClient.getName());
    }

    public Diet getCurrentDiet() {
        Log.d(TAG, "getCurrentDiet: " + currentDiet);
        return currentDiet;
    }

    public void setCurrentDiet(Diet currentDiet) {
        Log.d(TAG, "setCurrentDiet: " + currentDiet);
        this.currentDiet = currentDiet;
    }

    public FoodDiet getCurrentFood() {
        Log.d(TAG, "getCurrentFood: " + currentFood);
        return currentFood;
    }

    public void setCurrentFood(FoodDiet currentFood) {
        Log.d(TAG, "setCurrentFood: " + currentFood);
        this.currentFood = currentFood;
    }

    public User getUser() {
        Log.d(TAG, "getUser: " + user);
        return user;
    }

    public void setUser(User user) {
        Log.d(TAG, "setUser: " + user);
        this.user = user;
    }

    public void setCollectionClient ( CacheCollection<Client> cacheCollection){
        this.clients = cacheCollection;
        Log.d(TAG, "setCollectionClient: " + clients);
    }

    public void setCollectionFood ( CacheCollection<Food> cacheCollection) {
        this.foods = cacheCollection;
        Log.d(TAG, "setCollectionFood: " + foods);
    }

    public void setCollectionDiet ( CacheCollection<Map<String, Diet>> cacheCollection) {
        this.diets = cacheCollection;
        Log.d(TAG, "setCollectionDiet: " + diets);
    }

    public void setCollectionFoodDiet ( CacheCollection<Map<String, ArrayList<FoodDiet>>> cacheCollection) {
        this.foodDiets = cacheCollection;
        Log.d(TAG, "setCollectionFoodDiet: " + foodDiets);
    }

    public CacheCollection<Client> getClients() {
        Log.d(TAG, "getClients: " + clients);
        return clients;
    }

    public CacheCollection<Food> getFoods() {
        Log.d(TAG, "getFoods: " + foods);
        return foods;
    }

    public CacheCollection<Map<String, Diet>> getDiets() {
        Log.d(TAG, "getDiets: " + diets);
        return diets;
    }

    public CacheCollection<Map<String, ArrayList<FoodDiet>>> getFoodDiets() {
        Log.d(TAG, "getFoodDiets: " + foodDiets);
        return foodDiets;
    }

    public Client getClient (String name){
        Log.d(TAG, "getClient: " + clients.get(name));
        return clients.get(name);
    }

    public Food getFood (String name){
        Log.d(TAG, "getFood: " + foods.get(name));
        return foods.get(name);
    }

    public Map<String, Diet> getDietsOfClient (String idClient){
        if (diets.get(idClient) == null){
            return new HashMap<>();
        }else{
            Log.d(TAG, "getDietsOfClient: " + diets.get(idClient));
            return diets.get(idClient);
        }
    }

    public Map<String, ArrayList<FoodDiet>> getFoodDietsOfDiet (String idDiet){
        Log.d(TAG, "getFoodDietsOfDiet: " + foodDiets.get(idDiet));
        return foodDiets.get(idDiet);
    }

    public Diet getDiet (String idClient,String name){
        Log.d(TAG, "getDiet: " + getDietsOfClient(idClient).get(name));
        return getDietsOfClient(idClient).get(name);
    }

    public ArrayList<FoodDiet> getFoodDiet (String idDiet, String name){
        Log.d(TAG, "getFoodDiet: " + getFoodDietsOfDiet(idDiet).get(name));
        return getFoodDietsOfDiet(idDiet).get(name);
    }

    public void addClient (Client client){

        if (clients == null){
            clients = new CacheCollection<>();
        }

        Log.d(TAG, "addClient: " + client);
        clients.add(client.getId(), client);
    }

    public void addFood (Food food){
        Log.d(TAG, "addFood: " + food);
        if (foods == null){
            foods = new CacheCollection<>();
        }
        Log.d(TAG, "addFood: " + food);
        foods.add(food.getName(), food);
    }

    public void addDiet (Diet diet){
        if (diets == null){
            diets = new CacheCollection<>();
        }
        Map<String, Diet> diets = this.diets.get(diet.getIdCli());
        this.diets.remove(diet.getIdCli());

        if (diets == null){
            diets = new HashMap<>();
        }

        diets.put(diet.getName(), diet);

        this.diets.add(diet.getIdCli(),diets);

        Log.d(TAG, "addDiet: " + diet);
        Log.d(TAG, "addDiet: " + diets);
    }

    public void addFoodDiet(FoodDiet foodDiet) {
        if (foodDiets == null) {
            foodDiets = new CacheCollection<>();
        }
        Log.d(TAG, "addFoodDiet: " + foodDiet);
        String idDieta = foodDiet.getIdDieta(); // clave externa
        String comida = foodDiet.getName();   // clave interna

        // Obtener o crear el mapa interno asociado a esa dieta
        Map<String, ArrayList<FoodDiet>> map = foodDiets.get(idDieta);
        if (map == null) {
            map = new HashMap<>();
        }

        // Obtener o crear la lista de FoodDiet asociada a esa comida
        ArrayList<FoodDiet> lista = map.getOrDefault(foodDiet.getName(), new ArrayList<>());
        lista.add(foodDiet);

        // Guardar la lista actualizada dentro del mapa interno
        map.put(comida, lista);

        // Actualizar en el CacheCollection
        foodDiets.update(idDieta, map);
        Log.d(TAG, "addFoodDiet: " + foodDiets);
    }


    public void updateClient (Client client){
        clients.update(client.getName(), client);
        Log.d(TAG, "updateClient: " + client);
    }

    public void updateFood (Food food){
        foods.update(food.getName(), food);
    }

    public void updateDiet (Diet diet){
        Map<String, Diet> diets = getDietsOfClient(diet.getIdCli());
        if (diets == null){
            diets = new HashMap<>();
        }
        getDietsOfClient(diet.getIdCli()).clear();
        diets.put(diet.getName(), diet);
        this.diets.add(diet.getIdCli(),diets);
    }


    public void removeClient (String name){
        Log.d(TAG, "removeClient: " + name);
        clients.remove(name);
        Log.d(TAG, "removeClient: " + clients);
    }

    public void removeFood (String name){
        Log.d(TAG, "removeFood: " + name);
        foods.remove(name);
        Log.d(TAG, "removeFood: " + foods);
    }

    public void removeDiet (String idCli, String name){
        Log.d(TAG, "removeDiet: " + name);
        Map<String ,Diet> diets = getDietsOfClient(idCli);
        if (diets == null){
            diets = new HashMap<>();
        }
        getDietsOfClient(idCli).clear();
        diets.remove(name);
        this.diets.add(idCli,diets);
        Log.d(TAG, "removeDiet: " + diets);
    }

    public void removeFoodDiets (String name){
        Log.d(TAG, "removeFoodDiet: " + name);
        foodDiets.remove(name);
        Log.d(TAG, "removeFoodDiet: " + foodDiets);
    }
    public void removeFoodDiet (FoodDiet fd){
        Log.d(TAG, "removeFoodDiet: " + fd.getName());

        if (getFoodDietsOfDiet(fd.getIdDieta()).get(fd.getName()).size() == 1){
            getFoodDietsOfDiet(fd.getIdDieta()).remove(fd.getName());
        }else{
            for (int i = 0 ; i< getFoodDietsOfDiet(fd.getIdDieta()).get(fd.getName()).size(); i++){
                FoodDiet foodDiet = getFoodDietsOfDiet(fd.getIdDieta()).get(fd.getName()).get(i);

                if (foodDiet.getId().equals(fd.getId())){
                    getFoodDietsOfDiet(fd.getIdDieta()).get(fd.getName()).remove(i);
                }
            }
        }
    }

    public void clear(){
        Log.d(TAG, "-------------clear--------------");
        user = new User("","","");
        clients.clear();
        foods.clear();
        diets.clear();
        foodDiets.clear();
    }

    public void clearAdmin(){
        Log.d(TAG, "-------------clear--------------");
        students.clear();
        clients.clear();
        foods.clear();
        diets.clear();
        foodDiets.clear();
    }


    public void setCollectionLoaded(TablesNames type, boolean loaded) {
        switch (type) {
            case clientes:
                clients.setLoaded(loaded);
                break;
            case alimentos:
                foods.setLoaded(loaded);
                break;
            case dietas:
                diets.setLoaded(loaded);
                break;
            case comidaDietas:
                foodDiets.setLoaded(loaded);
                break;
        }
    }

    public CacheCollection<User> getStudents() {
        return students;
    }

    public void setStudents(CacheCollection<User> students) {
        this.students = students;
    }

    public User getCurrentStudent() {
        Log.d("CurrentStudent", "getCurrentStudent: " + currentStudent.getName());

        return currentStudent;
    }

    public void setCurrentStudent(User currentStudent) {
        this.currentStudent = currentStudent;
        Log.d("CurrentStudent", "setCurrentStudent: " + this.currentStudent.getName());
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    private List<FoodDiet> currentDayFoods;

    public void setCurrentDayFoods(List<FoodDiet> foods) {
        this.currentDayFoods = foods;
    }

    public List<FoodDiet> getCurrentDayFoods() {
        return currentDayFoods;
    }

    private Map<String, DailyNutrition> nutritionData = new HashMap<>();

    public void setNutritionForDay(String day, DailyNutrition nutrition) {
        nutritionData.put(day, nutrition);
    }

    public DailyNutrition getNutritionForDay(String day) {
        return nutritionData.get(day);
    }

    public void clearNutritionData() {
        nutritionData.clear();
    }
}