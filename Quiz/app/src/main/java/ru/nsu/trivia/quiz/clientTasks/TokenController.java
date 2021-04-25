package ru.nsu.trivia.quiz.clientTasks;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.FileHandler;

public class TokenController {
    public static String token;

    public static void setToken(String token, Context context){
        TokenController.token = token;
        create(context, token);
    }

    public static String getToken(Context context){
        if (token != null) return token;
        else return read(context, "token");
    }

    public static boolean isTokenSaved(Context context){
        if (token != null) return true;
        String path = context.getFilesDir() + "/" + "token";
        File file = new File(path);
        return file.exists();
    }

    static String read(Context context, String fileName) {
        synchronized (FileHandler.class) {
            try {
                FileInputStream fis;
                fis = context.openFileInput(fileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException fileNotFound) {
                return "";
            }
        }
    }

    static boolean create(Context context, String token) {
        synchronized (FileHandler.class) {
            try {
                FileOutputStream fos = context.openFileOutput("token", Context.MODE_PRIVATE);
                if (token != null) {
                    fos.write(token.getBytes());
                }
                fos.close();
                return true;
            } catch (IOException fileNotFound) {
                return false;
            }
        }
    }
}
