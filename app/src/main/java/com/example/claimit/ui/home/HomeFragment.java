package com.example.claimit.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.claimit.ClaimFormActivity;
import com.example.claimit.DashboardActivity;
import com.example.claimit.ImageUploadActivity;
import com.example.claimit.R;
import com.example.claimit.image_upload.ApiConstants;
import com.example.claimit.image_upload.ServiceInterface;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ArrayList<ClaimModel> courseModelArrayList;
    ServiceInterface serviceInterface;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.idRVCourse);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);
        JsonObject jsonObject = new JsonObject();
        SharedPreferences sh = this.getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        jsonObject.addProperty("userId", sh.getString("id", ""));
        Call<ResponseBody> call = serviceInterface.getClaims(jsonObject.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String claimsBody = response.body().string();
                    Log.i("GET_CLAIM", claimsBody + "--" + response.toString());
                    if (response.code() == 200) {
                        ClaimModel[] claimArray = new Gson().fromJson(claimsBody, ClaimModel[].class);
                        Log.i("CLAIMS", claimArray[0].toString());
                        setAdapter(claimArray);
                    }
                } catch (Exception e) {
                    Log.d("Exception GET_CLAIM", "|=>" + e.getMessage());
                    Toast.makeText(getActivity(), "No Claims found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Get Claims", t.getMessage());
                Toast.makeText(getActivity(), "No Claims found", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    private void setAdapter(ClaimModel[] claimArray) {
        ClaimAdapter claimAdapter = new ClaimAdapter(this, claimArray);
        recyclerView.setAdapter(claimAdapter);
    }

}