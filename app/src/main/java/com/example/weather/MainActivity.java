package com.example.weather;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText user_field;
    private Button main_btn;
    private Button exit_btn;
    private TextView result_info;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        exit_btn = findViewById(R.id.exit_btn);
        result_info = findViewById(R.id.result_info);
        image = findViewById(R.id.image);

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_field.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.input_text, Toast.LENGTH_LONG).show();
                } else {
                    String city = user_field.getText().toString();
                    String key = "6fe46e4bc66fb75ece9eb550572b22e9";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    try {
                        new GetUrlData().execute(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                a_builder.setMessage("Вы хотите закрыть приложение?")
                        .setCancelable(false)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = a_builder.create();
                alertDialog.show();
            }
        });
    }

    private class GetUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            image.setImageDrawable(null);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    int temp = (int) jsonObject.getJSONObject("main").getDouble("temp");

                    if (temp == 0)
                        result_info.setText("0°C");
                    else if (temp > 0)
                        result_info.setText("+" + temp + "°C");
                    else if (temp < 0)
                        result_info.setText(temp + "°C");

                    if (jsonObject.getJSONObject("clouds").getInt("all") == 0)
                        image.setImageResource(R.drawable.sun);
                    else if (jsonObject.has("rain"))
                        image.setImageResource(R.drawable.rain);
                    else if (jsonObject.has("snow"))
                        image.setImageResource(R.drawable.snow);
                    else
                        image.setImageResource(R.drawable.cloud);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                result_info.setText("Возникла ошибка.\nУточните название города и введите повторно");
            }
        }
    }
}