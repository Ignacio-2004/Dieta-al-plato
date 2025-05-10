package com.tfg.dietaalplato.utilities.tipe_collection;

import java.util.HashMap;
import java.util.Map;

public class CacheCollection<T> {

    private boolean isLoaded;
    private final Map<String, T> collection;

    public CacheCollection() {
        this.isLoaded = false;
        this.collection = new HashMap<>();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }

    public Map<String, T> getCollection() {
        return collection;
    }

    public void clear() {
        collection.clear();
        isLoaded = false;
    }

    public void add(String key, T value) {
        collection.putIfAbsent(key, value);
    }

    public void update(String key, T value) {
        collection.put(key, value);
    }

    public T get(String key) {
        return collection.get(key);
    }

    public boolean contains(String key) {
        return collection.containsKey(key);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public int size() {
        return collection.size();
    }

    public void remove(String key) {
        collection.remove(key);
    }

    public void setCollection(Map<String, T> newCollection) {
        collection.clear();
        collection.putAll(newCollection);
        isLoaded = true;
    }
}
