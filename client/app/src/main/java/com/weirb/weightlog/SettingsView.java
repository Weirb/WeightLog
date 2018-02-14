package com.weirb.weightlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

public class SettingsView extends AppCompatActivity {

    EditText password, name;
    TextView status;
    SharedPreferences sharedPref;
    boolean isFirstTimeFocusedPass, isFirstTimeFocusedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_view);

        password = findViewById(R.id.password_text);
        name = findViewById(R.id.name_text);
        status = findViewById(R.id.create_user_status_text);

        sharedPref = getSharedPreferences("key", Context.MODE_PRIVATE);

        String default_string = "";

        name.setText(sharedPref.getString(getString(R.string.string_name), default_string));
        password.setText(sharedPref.getString(getString(R.string.string_password), default_string));

        isFirstTimeFocusedPass = true;
        isFirstTimeFocusedName = true;

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && isFirstTimeFocusedName){
                    name.setText("");
                    isFirstTimeFocusedName = false;
                }
            }});

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && isFirstTimeFocusedPass){
                    password.setText("");
                    isFirstTimeFocusedPass = false;
                }
            }});
    }

    public void saveSettings(View view) throws Exception {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.string_name), name.getText().toString());
        editor.putString(getString(R.string.string_password), password.getText().toString());

        editor.apply();

        Intent goToNextActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goToNextActivity);
    }

    public void create_user(View view) throws Exception {

        try {
            String[] url = {getString(R.string.url_string), getString(R.string.url_create_user_string)};

            URL obj = new URL(TextUtils.join("", url));
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            JSONObject json_obj = new JSONObject();
            json_obj.put("name", name.getText());
            json_obj.put("password", password.getText());

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
//            status.setBackgroundColor(Color.GREEN);
            status.setTextColor(Color.GREEN);
            saveSettings(null);
        } else {
            status.setText("ERROR");
//            status.setBackgroundColor(Color.RED);
            status.setTextColor(Color.RED);
        }

        } catch(Exception e){
            System.out.println(e.toString());
        }

    }

}