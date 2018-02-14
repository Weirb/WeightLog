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
    TextView status;

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
    }

    public void sendPost(View view) throws Exception {

        try {

            String[] url = {getString(R.string.url_string), getString(R.string.url_add_record_string)};

            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST"); // PUT is another valid option
            con.setDoOutput(true);

            String default_string = "";
            JSONObject json_obj = new JSONObject();
            json_obj.put("name", sharedPref.getString(getString(R.string.string_name), default_string));
            json_obj.put("password", sharedPref.getString(getString(R.string.string_password), default_string));
            json_obj.put("value",value.getText());

            byte[] out = json_obj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();
            OutputStream os = con.getOutputStream();
            os.write(out);

            int responseCode = con.getResponseCode();
            if (Integer.toString(responseCode).startsWith("2")){
                status.setText("SUCCESS");
//                status.setBackgroundColor(Color.GREEN);
                status.setTextColor(Color.GREEN);
                value.setText("");
            } else {
                status.setText("ERROR");
//                status.setBackgroundColor(Color.RED);
                status.setTextColor(Color.RED);
            }

        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    public void viewSettings(View view) {
        Intent intent = new Intent(this, SettingsView.class);
        startActivity(intent);
    }

}
