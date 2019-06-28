package com.example.henry.whatstheweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    TextView unitTextView;
    Switch aSwitch;
    ImageView iconImageView;

    public void getWeather(View view) {

        DownloadTask task = new DownloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=b6907d289e10d714a6e88b30761fae22");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public double kelvinToFahrenheit(double temp) {
        double conversion = ((9 * temp)/5) + 32;
        return conversion;

    }

    public void getIcon(String url){
        DownloadImage task = new DownloadImage();
        Bitmap myBitMap;
        iconImageView.setVisibility(View.VISIBLE);
        try {
            myBitMap = task.execute("http://openweathermap.org/img/w/" + url + ".png").get();
            iconImageView.setImageBitmap(myBitMap);


        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;


            } catch (Exception e) {

                e.printStackTrace();
                Toast.makeText(MainActivity.this, "City not found, please enter another city name.", Toast.LENGTH_SHORT).show();

                return null;
            }

        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = " ";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "City not found, please enter another city name.", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("WEATHER CONTENT", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                String condition = " ";
                String icon = " ";
                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Log.i("main", jsonPart.getString("main"));
                    condition = jsonPart.getString("main");

                    Log.i("description", jsonPart.getString("description"));

                    icon = jsonPart.getString("icon");



                }
                JSONObject temp = jsonObject.getJSONObject("main");
                String tempString = temp.getString("temp");
                Log.i("Temperature", tempString);
                double temperature = Double.parseDouble(tempString);
                double tempInF = kelvinToFahrenheit(temperature);
                String finalTempC = String.format("%.1f", temperature);
                String finalTempF = String.format("%.1f", tempInF);

                Log.i("ICON ID",icon);


                getIcon(icon);

                if (aSwitch.isChecked()) {
                    textView.setText("The Temperature in " + editText.getText() + " : " + finalTempC + " degrees Celsius\n\n" + "Description: " + condition);


                } else {
                    textView.setText("The Temperature in " + editText.getText() + " : " +  finalTempF + " degrees Fahrenheit\n\n" + "Description: " + condition);

                }



            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        aSwitch = findViewById(R.id.switch1);
        unitTextView = findViewById(R.id.unitTextView);
        iconImageView = findViewById(R.id.iconImageView);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    unitTextView.setText("Celsius");
                } else {
                    unitTextView.setText("Fahrenheit");
                }
            }

        });


    }
}
