package com.example.hrishabh.translator;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
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

    Button button;
    TextView textView;
    EditText editText;
    String out;
    Context context = this;
    Spinner fromSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSpinner();
        initView();
    }

    private void setSpinner() {
        fromSpinner=findViewById(R.id.from);
        String[] items = new String[]{"1", "2", "three"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        fromSpinner.setAdapter(adapter);
    }


    private void initView() {
        button = findViewById(R.id.submit);
        textView = findViewById(R.id.output);
        editText = findViewById(R.id.input);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                out = editText.getText().toString().trim();
                String languagePair = "en-hi";
                Translate(out, languagePair);
            }
        });
    }

    void Translate(String textToTranslate, String languagePair) {
        TranslatorBackgroundTask translatorBackgroundTask = new TranslatorBackgroundTask(context);
        AsyncTask<String, Void, String>  translationResult = translatorBackgroundTask.execute(textToTranslate,languagePair); // Returns the translated text as a String
        Log.d("Translation Result", String.valueOf(translationResult));
    }


    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {

        Context ct;

        TranslatorBackgroundTask(Context ct) {
            this.ct = ct;
        }

        @Override
        protected String doInBackground(String... strings) {
            String textToBeTranslated = strings[0];
            String languagePair = strings[1];

            String jsonString;

            try {
                String yandexKey = "trnsl.1.1.20181016T211940Z.847c9e83c0c0799e.2dad996259a05d2c706930ef1cd1dba45c20a86b";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateUrl = new URL(yandexUrl);

                //establishing connection
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateUrl.openConnection();
                InputStream inputStream = null;
                try {
                    inputStream = httpJsonConnection.getInputStream();
                } catch (IOException ioe) {
                    if (httpJsonConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpConn = (HttpURLConnection) httpJsonConnection;
                        int statusCode = httpConn.getResponseCode();
                        if (statusCode != 200) {
                            inputStream = httpConn.getErrorStream();
                        }
                    }
                }

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                //insert json retrieved result
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }


                //closing connection
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();


                String result=jsonStringBuilder.toString().trim();
                result=result.substring(result.indexOf('[')+1);
                result=result.substring(0,result.indexOf(']'));

                result = result.substring(result.indexOf("\"")+1);
                result = result.substring(0,result.indexOf("\""));


                return jsonStringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            JSONObject reader = null;

            try {
                reader = new JSONObject(s);

                JSONArray sys  = reader.getJSONArray("text");
                for(int i =0 ;i<sys.length();i++){
                    textView.setText(sys.getString(0));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}

