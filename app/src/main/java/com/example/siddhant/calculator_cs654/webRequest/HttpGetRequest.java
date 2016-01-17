package com.example.siddhant.calculator_cs654.webRequest;

import com.example.siddhant.calculator_cs654.BuildConfig;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Siddhant on 1/17/2016.
 */
public class HttpGetRequest {
    public static Response makeHttpGetRequest(String expression) {
        HttpURLConnection conn = null;
        try {
            String parameters = URLEncoder.encode(expression.replaceAll("\\s+", ""), "UTF-8");
            URL url = new URL("http://107.167.178.155:8000/eval/" + parameters);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("platform", "android");
            conn.setRequestProperty("version", BuildConfig.VERSION_NAME);
            String line;
            InputStreamReader isr = new InputStreamReader(
                    conn.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String response = sb.toString();
            Response myResponse = new Gson().fromJson(response, Response.class);
            isr.close();
            reader.close();
            return myResponse;
        } catch (Exception ex) {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }
}
