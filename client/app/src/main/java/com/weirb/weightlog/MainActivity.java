/*

TODO:
1. Networking on a separate thread
2. Improve UI layout/colours
3. Auth token instead of storing password


https://developer.android.com/training/basics/firstapp/starting-activity.html
https://developer.android.com/training/data-storage/shared-preferences.html#java

https://proandroiddev.com/secure-data-in-android-encryption-in-android-part-1-e5fd150e316f
https://stackoverflow.com/questions/1925486/android-storing-username-and-password

 */


package com.weirb.weightlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    EditText value;
    TextView status, user_text;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        status = findViewById(R.id.textView);
        value = findViewById(R.id.value_text);

        sharedPref = getSharedPreferences("key",Context.MODE_PRIVATE);

        // Set the current user to the top of the screen
        user_text = findViewById(R.id.current_user_text);
        user_text.setText(sharedPref.getString(getString(R.string.string_username), ""));
    }

    public void sendPost(View view) throws Exception {

        try {

            String[] url = {getString(R.string.url_string), getString(R.string.url_add_record_string)};


            String default_string = "";
            JSONObject json_obj = new JSONObject();
            json_obj.put(getString(R.string.post_username), sharedPref.getString(getString(R.string.string_username), default_string));
            json_obj.put(getString(R.string.post_password), sharedPref.getString(getString(R.string.string_password), default_string));
            json_obj.put(getString(R.string.post_value),value.getText());


            byte[] out = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;


            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST"); // PUT is another valid option
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();


            OutputStream os = con.getOutputStream();
            os.write(out);

            int responseCode = con.getResponseCode();
            if (Integer.toString(responseCode).startsWith("2")){
                status.setText("SUCCESS");
                status.setTextColor(Color.GREEN);
                value.setText("");
            } else {
                // Should also check if token is still valid
                status.setText("ERROR");
                status.setTextColor(Color.RED);
            }
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

//    public void viewSettings(View view) {
//        Intent intent = new Intent(this, SettingsView.class);
//        startActivity(intent);
//    }

    public void logout(View view){
        try {
            // Deauthorise token on server
            String[] url = {getString(R.string.url_string), getString(R.string.url_deauth_token)};

            JSONObject json_obj = new JSONObject();
            json_obj.put(getString(R.string.post_username), sharedPref.getString(getString(R.string.string_username), ""));

            byte[] out = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();

            OutputStream os = con.getOutputStream();
            os.write(out);

            Log.d("RESPONSE", Integer.toString(con.getResponseCode()));
            Log.d("RESPONSE", "SUCCESS");

        } catch(Exception e){
            Log.d("RESPONSE", e.toString());
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.string_username), "");
        editor.putString(getString(R.string.string_password), "");
        editor.apply();

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}
