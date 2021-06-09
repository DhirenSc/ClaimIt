package com.example.claimit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.claimit.image_upload.ApiConstants;
import com.example.claimit.image_upload.FileUtil;
import com.example.claimit.image_upload.ServiceInterface;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadActivity extends AppCompatActivity {

    ImageView selectedImage;
    CircularProgressButton btnSubmit;
    ServiceInterface serviceInterface;
    List<Uri> files = new ArrayList<>();

    private LinearLayout parentLinearLayout;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_upload_main);

        parentLinearLayout = findViewById(R.id.parent_linear_layout);

        ImageView addImage = findViewById(R.id.iv_add_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });

        btnSubmit = findViewById(R.id.submit_button);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
                uploadImages();
            }
        });


    }


    //===== add image in layout
    public void addImage() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.image_view, null);
        // Add the new row before the add field button.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);
        parentLinearLayout.isFocusable();

        selectedImage = rowView.findViewById(R.id.number_edit_text);
        selectImage(ImageUploadActivity.this);
    }

    //===== select image
    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("Choose a Media");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {

                        Bitmap img = (Bitmap) data.getExtras().get("data");
                        selectedImage.setImageBitmap(img);
                        Picasso.get().load(getImageUri(ImageUploadActivity.this, img)).into(selectedImage);

                        String imgPath = FileUtil.getPath(ImageUploadActivity.this, getImageUri(ImageUploadActivity.this, img));

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri img = data.getData();
                        Picasso.get().load(img).into(selectedImage);

                        String imgPath = FileUtil.getPath(ImageUploadActivity.this,img);

                        files.add(Uri.parse(imgPath));
                        Log.e("image", imgPath);
                    }
                    break;
            }
        }
    }

    //===== bitmap to Uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "intuenty", null);
        Log.d("image uri", path);
        return Uri.parse(path);
    }

    //===== Upload files to server
    public void uploadImages() {

        btnSubmit.startAnimation();

        List<MultipartBody.Part> list = new ArrayList<>();
        int count = 1;
        for (Uri uri : files) {

            Log.i("uris", uri.getPath());

            list.add(prepareFilePart("file" + count, uri));
            count++;
        }

        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);

        Call<ResponseBody> call = serviceInterface.uploadImages(list);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String responseBody = response.body().string();
                    Log.i("UPLOAD_IMAGES", responseBody + "--" + response.toString());
                    if (response.code() == 200) {
                        detectImages(responseBody);
                        //Toast.makeText(ImageUploadActivity.this, "Claim report ready", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("main", "the status is ----> " + response.code());
                    Log.e("main", "the message is ----> " + response.message());

                } catch (Exception e) {
                    Log.d("Exception UPLOAD_IMAGES", "|=>" + e.getMessage());
//
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnSubmit.revertAnimation();
                Log.i("my", t.getMessage());
            }
        });
    }

    private void detectImages(String responseBody) {
        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);
        JsonObject responseObject = new JsonParser().parse(responseBody).getAsJsonObject();
        responseObject.remove("filename");
        Call<ResponseBody> call = serviceInterface.detectImages(responseObject.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String detectionsBody = response.body().string();
                    Log.i("DETECT_IMAGES", detectionsBody + "--" + response.toString());
                    if (response.code() == 200) {
                        JsonObject detectionsObject = new JsonParser().parse(detectionsBody).getAsJsonObject();
                        JsonObject claimObject = new JsonObject();
                        // from the SharedPreference
                        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        claimObject.addProperty("claimId", detectionsObject.get("claimId").getAsString());
                        claimObject.addProperty("severity", detectionsObject.get("severity").getAsString());
                        claimObject.addProperty("imageUrls", detectionsObject.get("output_urls").getAsString());
                        claimObject.addProperty("make", sh.getString("make", ""));
                        claimObject.addProperty("model", sh.getString("model", ""));
                        claimObject.addProperty("phoneNo", sh.getString("phone", ""));
                        claimObject.addProperty("year", sh.getString("year", ""));
                        claimObject.addProperty("userId", sh.getString("id", ""));
                        claimObject.addProperty("status", "Not approved");
                        fileClaim(claimObject);
                    }
                } catch (Exception e) {
                    Log.d("Exception DETECT_IMAGES", "|=>" + e.getMessage());
//
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnSubmit.revertAnimation();
                Log.i("my", t.getMessage());
            }
        });
    }

    private void fileClaim(JsonObject claimObject) {
        serviceInterface = ApiConstants.getClient().create(ServiceInterface.class);
        Call<ResponseBody> call = serviceInterface.fileClaim(claimObject.toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String claimsBody = response.body().string();
                    Log.i("FILE_CLAIM", claimsBody + "--" + response.toString());
                    if (response.code() == 200) {
                        btnSubmit.revertAnimation();
                        if (claimsBody.equals("1 record inserted.")) {
                            Toast.makeText(ImageUploadActivity.this, "Claim report ready", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(ImageUploadActivity.this, DashboardActivity.class);
                            startActivity(homeIntent);
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception", "|=>" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnSubmit.revertAnimation();
                Log.i("my", t.getMessage());
            }
        });
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(fileUri.getPath());
        Log.i("here is error", file.getAbsolutePath());
        // create RequestBody instance from file

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }

    // this is all you need to grant your application external storage permision
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageUploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadImages();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}
