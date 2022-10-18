package com.example.sensormonitor_datacollector;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileReadWrite {
    private Context context;

    private String getDirectory(int activity_class) {
        String studentId = "49003975";
        return context.getExternalFilesDir(null).toString() + "/" + studentId + "/" + activity_class;
    }

    public FileReadWrite(Context context) {
        this.context = context;
    }

    public String readFromFile(int activity_class, String fileName) {

        String ret = "";

        try {
            String path = getDirectory(activity_class) + "/" + fileName;
            InputStream inputStream = new FileInputStream(path);

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append("\n").append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void writeToFile(int activity_class, String fileName, String data) {
        try {
            String path = getDirectory(activity_class) + "/" + fileName;
            File dir = new File(getDirectory(activity_class));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(stream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
