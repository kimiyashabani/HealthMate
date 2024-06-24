package com.example.healthmate;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Log;
public class PostTask extends AsyncTask<String, Void, String> {
    private TextView airesponse;
    public PostTask(TextView airesponse) {
        this.airesponse = airesponse;
    }
    protected String doInBackground(String... params) {
        String query = params[0];
        String response = "";
        try {
            URL url = new URL("http://192.168.1.90:5000/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("query", query);

            OutputStream os = connection.getOutputStream();
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            response = content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    protected void onPostExecute(String result) {
        try {
            JSONObject jsonResponse = new JSONObject(result);
            String responseText = jsonResponse.getString("response");
            Log.d("PostTask", responseText);
            airesponse.setText(responseText);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("PostTask", e.getMessage());
            airesponse.setText("Error: " + e.getMessage());
        }

    }
}
