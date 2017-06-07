package com.letmeeat.letmeeat.service;

import com.letmeeat.letmeeat.models.RecoRequest;
import com.letmeeat.letmeeat.models.Recommendation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by santhosh on 16/11/2016.
 * Retrospect Interface for Service
 */

public interface ApiService {
    @POST("v1/places")
    Call<List<Recommendation>> getRecommendations(@Body RecoRequest requestbody);

    @GET("v1/places")
    Call<List<Recommendation>> getRecommendations();
}
