package com.example.claimit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.claimit.image_upload.ApiConstants;
import com.example.claimit.image_upload.ServiceInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;
    private SignInButton mSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog mProgress;
    ServiceInterface serviceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Assign fields
        mSignInButton = findViewById(R.id.sign_in_button);

        // Set click listeners
        mSignInButton.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mProgress = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            signIn();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            handleSignIn(account);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                mProgress.setTitle("Signing In");
                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setIndeterminate(true);
                mProgress.setProgressNumberFormat(null);
                mProgress.setProgressPercentFormat(null);
                mProgress.show();
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult();
                handleSignIn(account);
            } else {
                // Google Sign-In failed
                Log.e(TAG, "Google Sign-In failed.", task.getException());
                Toast.makeText(SignInActivity.this, "Authentication failed.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleSignIn(GoogleSignInAccount account) {
        if (account != null) {
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            Log.i(TAG, personName+"--"+personEmail+"--"+personId);

            serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);
            JsonObject user_details = new JsonObject();
            user_details.addProperty("userId", personId);
            user_details.addProperty("emailId", personEmail);
            user_details.addProperty("name", personName);
            if(personPhoto != null) {
                user_details.addProperty("photoURL", personPhoto.toString());
            } else {
                user_details.addProperty("photoURL", "");
            }
            Log.i(TAG, user_details.toString());
            Call<ResponseBody> call = serviceInterface.addOrUpdateUser(user_details.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String responseBody = response.body().string();
                        Log.i("RESPONSE", responseBody + "--" + response.toString());
                        if (response.code() == 200) {
                            // Storing data into SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                            // Creating an Editor object to edit(write to the file)
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            // Storing the key and its value as the data fetched from edittext
                            myEdit.putString("name", personName);
                            myEdit.putString("email", personEmail);
                            myEdit.putString("id", personId);
                            myEdit.apply();

                            Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                            intent.putExtra("account",account);
                            mProgress.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Log.d("Exception", "|=>" + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });

            // Storing data into SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            // Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            // Storing the key and its value as the data fetched from edittext
            myEdit.putString("name", personName);
            myEdit.putString("email", personEmail);
            myEdit.putString("id", personId);
            myEdit.apply();

            Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
            intent.putExtra("account",account);
            mProgress.dismiss();
            startActivity(intent);
            finish();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}