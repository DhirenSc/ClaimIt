package com.example.claimit.image_upload;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConstants {

    //==== Base Url
    public static String BASE_URL = "# YOUR BASE URL";

    //==== End point
    public static final String UPLOAD_ENDPOINT = "/api/upload";
    public static final String DETECT_ENDPOINT = "/api/detect/multiple";
    public static final String MAKE_MODEL_ENDPOINT = "/api/car/data";
    public static final String UPDATE_USER_ENDPOINT = "/api/profile/update";
    public static final String FILE_CLAIM_ENDPOINT = "/api/submit/claim";
    public static final String GET_CLAIM_ENDPOINT = "/api/claims";

    //===== Retrofit Client
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}