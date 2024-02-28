package com.example.maitinimosidienorastis;

import java.util.ArrayList;

public class MealTimeStamp {
    private String date;
    private String time;
    private ArrayList<String> products;
    public MealTimeStamp(String date, String time, ArrayList<String> products) {
        this.date = date;
        this.time = time;
        this.products = products;
    }
    public String getDate() { return this.date; }
    public String getTime() { return this.time; }
    public ArrayList<String> getProducts() { return this.products; }
}