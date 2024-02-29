package com.example.maitinimosidienorastis;

import java.util.ArrayList;
import java.util.Map;

public class MealTimeStamp {
    private String date;
    private String time;
    private String key;
    private ArrayList<String> personalProducts;
    private ArrayList<String> localProducts;
    public MealTimeStamp(String key, String date, String time, ArrayList<String> personalProducts, ArrayList<String> localProducts) {
        this.key = key;
        this.date = date;
        this.time = time;
        this.personalProducts = personalProducts;
        this.localProducts = localProducts;
    }
    public MealTimeStamp(String date, String time, ArrayList<String> personalProducts, ArrayList<String> localProducts) {
        this.date = date;
        this.time = time;
        this.personalProducts = personalProducts;
        this.localProducts = localProducts;
    }
    public String getKey() { return this.key; }
    public String getDate() { return this.date; }
    public String getTime() { return this.time; }
    public ArrayList<String> getPersonalProducts() { return this.personalProducts; }
    public ArrayList<String> getLocalProducts() { return this.localProducts; }
}