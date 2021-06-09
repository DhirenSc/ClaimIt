package com.example.claimit.image_upload;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceInterface {

    @Multipart
    @POST(ApiConstants.UPLOAD_ENDPOINT)
    Call<ResponseBody> uploadImages(@Part List<MultipartBody.Part> files);

    @POST(ApiConstants.DETECT_ENDPOINT)
    Call<ResponseBody> detectImages(@Body String body);

    @GET(ApiConstants.MAKE_MODEL_ENDPOINT)
    Call<ResponseBody> getMakeModel();

    @POST(ApiConstants.UPDATE_USER_ENDPOINT)
    Call<ResponseBody> addOrUpdateUser(@Body String userDetails);

    @POST(ApiConstants.FILE_CLAIM_ENDPOINT)
    Call<ResponseBody> fileClaim(@Body String claimDetails);

    @POST(ApiConstants.GET_CLAIM_ENDPOINT)
    Call<ResponseBody> getClaims(@Body String claimDetails);
}