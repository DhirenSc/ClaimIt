package com.example.claimit;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ClaimImageViewer extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ImageItem> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_recycler_view);

        recyclerView = findViewById(R.id.img_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String urls = getIntent().getStringExtra("MODEL");
        String severity = getIntent().getStringExtra("SEVERITY");
        JsonParser parser = new JsonParser();
        JsonArray allUrls = new JsonArray();
        try {
            JsonElement urlElement = parser.parse(urls);
            allUrls = urlElement.getAsJsonArray();
        } catch (Exception e) {
            Log.i("URL - ", urls);
        }
        mList = new ArrayList<>();
        fetchData(allUrls, severity);
    }

    private void fetchData(JsonArray allUrls, String severity) {
        try {
            if (severity.equals("2")) {
                severity = "Major Damage";
            } else if(severity.equals("1")) {
                severity = "Minor Damage";
            } else {
                severity = "No Damage";
            }
            for (JsonElement url :
                    allUrls) {
                String myUrl = url.getAsString().split(" : ")[0].trim();
                Log.i("MYURL - ", myUrl);
                ImageItem post = new ImageItem(myUrl, severity);
                mList.add(post);
            }
            PostAdapter adapter = new PostAdapter(ClaimImageViewer.this, mList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}