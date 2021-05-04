package ru.nsu.trivia.quiz.clientTasks;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

//Просто этот код на java был у меня из другого проета, так что зачем переписывать всё на котлин - нипанятна
public class APIConnector {

    private static String serverAddress = "http://10.0.2.2:4888/";

    enum RequestType{
        GET,
        POST
    }

    public static String doGet(String url, Object requestBody) throws IOException {
        return doRequest(url, requestBody, RequestType.GET);
    }

    public static String doPost(String url, Object requestBody) throws IOException {
        return doRequest(url, requestBody, RequestType.POST);
    }

    public static String doRequest(String url, Object requestBody, RequestType type) throws IOException {
        URL obj = new URL(serverAddress + url);
        Log.d(TAG, "Ask: " + obj);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod(type.toString());

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(requestBody);

        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write(json);
        wr.flush();
       // Log.d(TAG, con.toString());
        //Log.d(TAG, json);

        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            Log.d(TAG, "Get: " + sb.toString());
            return sb.toString();
        } else {
            return "";
        }
    }
}
