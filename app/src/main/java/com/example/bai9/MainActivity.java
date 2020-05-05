package com.example.bai9;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView txt_text;
    String link= "https://vnexpress.net/rss/phap-luat.rss";
    String contents = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        click();
    }

    private void click() {
        btn = (Button) findViewById(R.id.btn);
        txt_text = (TextView) findViewById(R.id.txt_text);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            contents = new ReadData().execute(link).get();
                            txt_text.setText(contents);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    class ReadData extends AsyncTask<String, Void, String >{

        @Override
        protected String doInBackground(String... strings) {
            String content = "";
            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine())!=null){
                    content = content  + line +"\n";
                }
                bufferedReader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, contents, Toast.LENGTH_LONG);
            String title = "";
            XMLDOMParser xmldomParser = new XMLDOMParser();
            Document document = xmldomParser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("item");
            for (int i = 0; i< nodeList.getLength(); i++){
                Element e = (Element)nodeList.item(i);
                String item = xmldomParser.getValue(e, "title") + xmldomParser.getValue(e, "pubDate");
                title = title+"\n" +item;
                Toast.makeText(MainActivity.this, item, Toast.LENGTH_LONG);
            }
            txt_text = (TextView) findViewById(R.id.txt_text);
            txt_text.setText(title);
        }

    }
}
