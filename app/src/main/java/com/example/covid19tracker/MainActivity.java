package com.example.covid19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    RecyclerView recyclerView;
    List<Model> records;
    JavaAdapter adapter;
    ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(MainActivity.this, "If data didn't data, then Please turn off and turn on your mobile", Toast.LENGTH_SHORT).show();
                initializeData();
            }
        });
        initializeData();
    }

    private void initializeData() {

        Toast.makeText(MainActivity.this, "If data didn't load data, then Please turn off and turn on your mobile", Toast.LENGTH_LONG ).show();
        String URL = "https://api.rootnet.in/covid19-in/stats/latest";
        records = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject object = new JSONObject(response);
                    JSONObject object3 = object.getJSONObject("data");
                    JSONArray jsonArray = object3.getJSONArray("regional");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        String state = object1.getString("loc");
                        String active = object1.getString("confirmedCasesIndian");
                        String recovered = object1.getString("discharged");
                        String deaths = object1.getString("deaths");

                        records.add(new Model(state,active,recovered,deaths));
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        records.add(new Model("Maharashtra","122","23","12"));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new JavaAdapter(records);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

}

