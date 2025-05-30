package com.tfg.dietaalplato.utilities;

import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.firebase.utilities.TablesNames;
import com.tfg.dietaalplato.utilities.tipe_collection.CacheCollection;

import java.util.ArrayList;
import java.util.HashMap;
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
    private CacheCollection<Client> clients;
    private CacheCollection<Food> foods;
    private CacheCollection<Map<String, Diet>> diets;
    private CacheCollection<Map<String, ArrayList<FoodDiet>>> foodDiets;

    private Client idCurrentClient;
    private Diet idCurrentDiet;
    private FoodDiet idCurrentFood;

    private SaveData() {
        user = new User("","","");
        clients = new CacheCollection<>();
        foods = new CacheCollection<>();
        diets = new CacheCollection<>();
        foodDiets = new CacheCollection<>();
        idCurrentClient = new Client();
        idCurrentDiet = new Diet();
        idCurrentFood = new FoodDiet();
    }

    public static SaveData getInstance() {
        if (saveData == null) {
            saveData = new SaveData();
        }
        return saveData;
    }

    public Client getIdCurrentClient() {
        return idCurrentClient;
    }

    public void setIdCurrentClient(Client idCurrentClient) {
        this.idCurrentClient = idCurrentClient;
    }

    public Diet getIdCurrentDiet() {
        return idCurrentDiet;
    }

    public void setIdCurrentDiet(Diet idCurrentDiet) {
        this.idCurrentDiet = idCurrentDiet;
    }

    public FoodDiet getIdCurrentFood() {
        return idCurrentFood;
    }

    public void setIdCurrentFood(FoodDiet idCurrentFood) {
        this.idCurrentFood = idCurrentFood;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCollectionClient ( CacheCollection<Client> cacheCollection){
        this.clients = cacheCollection;
    }

    public void setCollectionFood ( CacheCollection<Food> cacheCollection) {
        this.foods = cacheCollection;
    }

    public void setCollectionDiet ( CacheCollection<Map<String, Diet>> cacheCollection) {
        this.diets = cacheCollection;
    }

    public void setCollectionFoodDiet ( CacheCollection<Map<String, ArrayList<FoodDiet>>> cacheCollection) {
        this.foodDiets = cacheCollection;
    }

    public CacheCollection<Client> getClients() {
        return clients;
    }

    public CacheCollection<Food> getFoods() {
        return foods;
    }

    public CacheCollection<Map<String, Diet>> getDiets() {
        return diets;
    }

    public CacheCollection<Map<String, ArrayList<FoodDiet>>> getFoodDiets() {
        return foodDiets;
    }

    public Client getClient (String name){
        return clients.get(name);
    }

    public Food getFood (String name){
        return foods.get(name);
    }

    public Map<String, Diet> getDietsOfClient (String idClient){
        if (diets.get(idClient) == null){
            return new HashMap<>();
        }else{
            return diets.get(idClient);
        }
    }

    public Map<String, ArrayList<FoodDiet>> getFoodDietsOfDiet (String idDiet){
        return foodDiets.get(idDiet);
    }

    public Diet getDiet (String idClient,String name){
        return getDietsOfClient(idClient).get(name);
    }

    public ArrayList<FoodDiet> getFoodDiet (String idDiet, String name){
        return getFoodDietsOfDiet(idDiet).get(name);
    }

    public void addClient (Client client){
        clients.add(client.getId(), client);
    }

    public void addFood (Food food){
        foods.add(food.getId(), food);
    }

    public void addDiet (Diet diet){
        Map<String, Diet> diets = getDietsOfClient(diet.getIdCli());
        getDietsOfClient(diet.getIdCli()).clear();
        diets.putIfAbsent(diet.getName(), diet);
        this.diets.add(diet.getIdCli(),diets);
    }

    public void addFoodDiet(FoodDiet foodDiet) {
        String idDieta = foodDiet.getIdDieta(); // clave externa
        String comida = foodDiet.getComida();   // clave interna

        // Obtener o crear el mapa interno asociado a esa dieta
        Map<String, ArrayList<FoodDiet>> comidaMap = foodDiets.get(idDieta);
        if (comidaMap == null) {
            comidaMap = new HashMap<>();
        }

        // Obtener o crear la lista de FoodDiet asociada a esa comida
        ArrayList<FoodDiet> lista = comidaMap.getOrDefault(comida, new ArrayList<>());
        lista.add(foodDiet);

        // Guardar la lista actualizada dentro del mapa interno
        comidaMap.put(comida, lista);

        // Actualizar en el CacheCollection
        foodDiets.update(idDieta, comidaMap);
    }


    public void updateClient (Client client){
        clients.update(client.getName(), client);
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
        clients.remove(name);
    }

    public void removeFood (String name){
        foods.remove(name);
    }

    public void removeDiet (String name){
        diets.remove(name);
    }

    public void removeFoodDiet (String name){
        foodDiets.remove(name);
    }

    public void clear(){
        user = new User("","","");
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


}