package com.example.siddhant.calculator_cs654;

/**
 * Created by Siddhant Saurabh on 1/10/2016.
 */
public class DatabaseClass {
    public String query;
    public String response;
    public boolean animationStates;

    public DatabaseClass(String query, String response) {
        this.query = query;
        this.response = response;
        animationStates = false;
    }
}
