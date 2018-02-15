package com.weirb.weightlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Login extends AppCompatActivity {

    EditText password, username;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        password = findViewById(R.id.password_text);
        username = findViewById(R.id.username_text);

        sharedPref = getSharedPreferences("key", Context.MODE_PRIVATE);

        // We should check if the user token is still valid
        // If yes, move to next activity
        // If no, display message and ask to reauthenticate
        if (!sharedPref.getString(getString(R.string.string_username), "").equals("")) {
            if (verifyToken()) {
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);
            }
        }
    }

    public void auth_user(View view) throws Exception {
        /*
        0.  Create POST request containing username/password JSON data.
        1.  Check response
        2.  If response is good
        2.0     Add auth token and username to shared preferences
        2.1     Move to main screen
        3.  Else
        3.0     Show error
         */

        try {
            // URL for authentication of existing user
            String[] url = {getString(R.string.url_string), getString(R.string.url_authenticate_user)};

            // Create JSON object with username/password data
            JSONObject json_obj = new JSONObject();
            json_obj.put(getString(R.string.post_username), username.getText());
            json_obj.put(getString(R.string.post_password), password.getText());

            // Array of bytes containing JSON object
            byte[] data = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = data.length;

            // Create connection object
            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();

            // Write data to the connection
            con.getOutputStream().write(data);

            if (con.getResponseCode()-200 < 10){

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                // Put the token into shared preferences for later
                setPreferences(sb.toString());

                // Move to the main screen on successful connection
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);

            } else {
                // Handle error
            }

        } catch(Exception e){
            Log.d("Response", e.toString());
        }
    }

    public void create_user(View view) throws Exception {

        /*
        0.  Create POST request containing username/password JSON data.
        1.  Check response
        2.  If response is good
        2.0     Add auth token and username to shared preferences
        2.1     Move to main screen
        3.  Else
        3.0     Show error
         */

        try {
            String[] url = {getString(R.string.url_string), getString(R.string.url_create_user_string)};

            // Create JSON object with username/password data
            JSONObject json_obj = new JSONObject();
            json_obj.put(getString(R.string.post_username), username.getText());
            json_obj.put(getString(R.string.post_password), password.getText());

            // Array of bytes containing JSON object
            byte[] data = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = data.length;

            // Create connection object
            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();

            // Write data to the connection
            con.getOutputStream().write(data);

            if (con.getResponseCode()-200 < 10){

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                // Put the token into shared preferences for later
                setPreferences(sb.toString());

                // Move to the main screen on successful connection
                Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToNextActivity);

            } else {
                // Handle error
            }

        } catch(Exception e){
            Log.d("Response", e.toString());
        }
    }

    private void setPreferences(String auth_token) {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.string_username), username.getText().toString());
        editor.putString(getString(R.string.string_password), auth_token);

        editor.apply();
    }

    private boolean verifyToken(){

        try {
            String[] url = {getString(R.string.url_string), getString(R.string.url_verify_token)};

            // Create JSON object with username/password data
            JSONObject json_obj = new JSONObject();
            json_obj.put(getString(R.string.post_username), sharedPref.getString(getString(R.string.string_username), ""));
            json_obj.put(getString(R.string.post_password), sharedPref.getString(getString(R.string.string_password), ""));

            // Array of bytes containing JSON object
            byte[] data = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = data.length;

            // Create connection object
            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();

            // Write data to the connection
            con.getOutputStream().write(data);

            if (con.getResponseCode() - 200 < 10) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            Log.d("ERROR", e.toString());
        }

        return false;
    }
}
