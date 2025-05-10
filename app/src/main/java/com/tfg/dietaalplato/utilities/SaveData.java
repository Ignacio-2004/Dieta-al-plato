package com.tfg.dietaalplato.utilities;

import com.tfg.dietaalplato.firebase.tables.Client;
import com.tfg.dietaalplato.firebase.tables.Diet;
import com.tfg.dietaalplato.firebase.tables.Food;
import com.tfg.dietaalplato.firebase.tables.FoodDiet;
import com.tfg.dietaalplato.firebase.tables.User;
import com.tfg.dietaalplato.utilities.tipe_collection.CacheCollection;

public class SaveData {

    private static SaveData saveData;
    private User user;
    private CacheCollection<Client> clients;
    private CacheCollection<Food> foods;
    private CacheCollection<Diet> diets;
    private CacheCollection<FoodDiet> foodDiets;

    private SaveData(){
        user = new User();
        clients = new CacheCollection<>();
        foods = new CacheCollection<>();
        diets = new CacheCollection<>();
        foodDiets = new CacheCollection<>();
    }

    public static SaveData getInstance(){
        if(saveData == null){
            saveData = new SaveData();
        }
        return saveData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CacheCollection<Client> getClients() {
        return clients;
    }

    public void setClients(CacheCollection<Client> clients) {
        this.clients = clients;
    }

    public CacheCollection<Food> getFoods() {
        return foods;
    }

    public void setFoods(CacheCollection<Food> foods) {
        this.foods = foods;
    }

    public CacheCollection<Diet> getDiets() {
        return diets;
    }

    public void setDiets(CacheCollection<Diet> diets) {
        this.diets = diets;
    }

    public CacheCollection<FoodDiet> getFoodDiets() {
        return foodDiets;
    }

    public void setFoodDiets(CacheCollection<FoodDiet> foodDiets) {
        this.foodDiets = foodDiets;
    }

    public void addClient(Client client){
        this.clients.add(client.getId(), client);
    }

    public void addFood(Food food){
        this.foods.add(food.getId(), food);
    }

    public void addDiet(Diet diet){
        this.diets.add(diet.getId(), diet);
    }

    public void addFoodDiet(FoodDiet foodDiet){
        this.foodDiets.add(foodDiet.getId(), foodDiet);
    }

    public void removeClient(Client client){
        this.clients.remove(client.getName());
    }

    public void removeFood(Food food){
        this.foods.remove(food.getName());
    }

    public void removeDiet(Diet diet){
        this.diets.remove(diet.getName());
    }

    public void removeFoodDiet(FoodDiet foodDiet){
        this.foodDiets.remove(foodDiet.getName());
    }

    public void updateClient(Client client){
        this.clients.update(client.getName(), client);
    }

    public void updateFood(Food food){
        this.foods.update(food.getName(), food);
    }

    public void updateDiet(Diet diet){
        this.diets.update(diet.getName(), diet);
    }

    public void updateFoodDiet(FoodDiet foodDiet){
        this.foodDiets.update(foodDiet.getName(), foodDiet);
    }

    public void clear() {
        this.user = null;
        this.clients = null;
        this.foods = null;
        this.diets = null;
    }
}
