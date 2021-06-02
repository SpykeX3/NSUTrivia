package ru.nsu.trivia.quiz.clientTasks;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import static android.content.ContentValues.TAG;

public class APIConnector {

    private static String serverAddress = "http://10.0.2.2:4888/";

    public static ConnectionResult doLongPoling(String url, String token, long lastUpdate) throws IOException {
        URL obj = new URL(serverAddress + url + "?token=" + token + "&lastUpdate=" + lastUpdate);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(36000000);
        Log.d(TAG, "Ask: " + obj);
        return getConnResult(con);
    }

    public static String doGet(String url, String token) throws IOException {
        URL obj = new URL(serverAddress + url + "?token=" + token);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        Log.d(TAG, "Get: " + sb.toString());
        return sb.toString();
    }

    public static ConnectionResult doPost(String url, Object requestBody) throws IOException {
        URL obj = new URL(serverAddress + url);

        Log.d(TAG, "Ask: " + obj);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(requestBody);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(json);
            wr.flush();
            wr.close();
            return getConnResult(con);
    }

    private static ConnectionResult getConnResult(HttpURLConnection con) throws IOException {
        int status = con.getResponseCode();
        StringBuilder sb = new StringBuilder();

        BufferedReader br;
        if (status == 200) {
            br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"));
        }else {
            br = new BufferedReader(
                    new InputStreamReader(con.getErrorStream(), "utf-8"));
        }
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();
        Log.d(TAG, "Get: " + sb.toString());
        ConnectionResult result = new ConnectionResult(status, null, sb.toString());
        return result;
    }
}
