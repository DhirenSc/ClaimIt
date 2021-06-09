package com.example.claimit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.claimit.image_upload.ApiConstants;
import com.example.claimit.image_upload.ServiceInterface;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClaimFormActivity extends AppCompatActivity {
    private TextView TVname, TVemail;
    private EditText ETphone;
    private Button btnAddImages;
    private Spinner year, make, model;
    private String makeModelResponse;
    ServiceInterface serviceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_form);
        TVname = findViewById(R.id.editTextName);
        TVemail = findViewById(R.id.editTextEmail);
        ETphone = findViewById(R.id.editTextPhone);
        btnAddImages = findViewById(R.id.button_next_step);
        year = findViewById(R.id.spinner_year);
        make = findViewById(R.id.spinner_make);
        model = findViewById(R.id.spinner_model);

        Integer[] nums = new Integer[21];
        for(int y=0;y<nums.length;y++) {
            nums[y]= y + 2000;
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, nums);
        year.setAdapter(yearAdapter);
        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);

        Call<ResponseBody> call = serviceInterface.getMakeModel();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    makeModelResponse = response.body().string();
                    addToAdapter(makeModelResponse);
                } catch (Exception e) {
                    Log.d("Exception", "|=>" + e.getMessage());
//
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("my", t.getMessage());
            }
        });

        btnAddImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                if( TextUtils.isEmpty(ETphone.getText())){
                    Toast.makeText(ClaimFormActivity.this, "Phone number required", Toast.LENGTH_LONG).show();
                }
                else {
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    // write all the data entered by the user in SharedPreference and apply
                    myEdit.putString("phone", ETphone.getText().toString());
                    myEdit.putString("year", year.getSelectedItem().toString());
                    myEdit.putString("make", make.getSelectedItem().toString());
                    myEdit.putString("model", model.getSelectedItem().toString());
                    myEdit.apply();
                    Intent imageUpload = new Intent(ClaimFormActivity.this, ImageUploadActivity.class);
                    startActivity(imageUpload);
                }
            }
        });

    }

    private void addToAdapter(String makeModelResponse) {
        JsonObject jsonObject = new JsonParser().parse(makeModelResponse).getAsJsonObject();
        JsonArray makes = jsonObject.getAsJsonArray("makes");
        ArrayList<String> allMakes = new ArrayList<>();
        JsonArray models = jsonObject.getAsJsonArray("models");
        ArrayList<String> allModels = new ArrayList<>();
        //Iterate the jsonArray and print the info of JSONObjects
        for(JsonElement make: makes){
            allMakes.add(make.getAsString());
        }
        for(JsonElement model: models){
            allModels.add(model.getAsString());
        }
        ArrayAdapter<String> makeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, allMakes);
        make.setAdapter(makeAdapter);
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, allModels);
        model.setAdapter(modelAdapter);
    }


    // Fetch the stored data in onResume()
    // Because this is what will be called
    // when the app opens again
    @Override
    protected void onResume() {
        super.onResume();

        // Fetching the stored data
        // from the SharedPreference
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String name = sh.getString("name", "");
        String email = sh.getString("email", "");

        // Setting the fetched data
        // in the EditTexts
        TVname.setText(name);
        TVemail.setText(email);
    }

    // Store the data in the SharedPreference
    // in the onPause() method
    // When the user closes the application
    // onPause() will be called
    // and data will be stored
    @Override
    protected void onPause() {
        super.onPause();

        // Creating a shared pref object
        // with a file name "MySharedPref"
        // in private mode
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String phoneNo = ETphone.getText().toString();
        if (phoneNo != "") {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            // write all the data entered by the user in SharedPreference and apply
            myEdit.putString("phone", phoneNo);
            myEdit.apply();
        }
    }
}
