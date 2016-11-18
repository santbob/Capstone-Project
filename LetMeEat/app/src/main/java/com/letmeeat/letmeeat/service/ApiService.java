package com.letmeeat.letmeeat.service;

import com.letmeeat.letmeeat.models.Recommendation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by santhosh on 16/11/2016.
 * Retrospect Interface for Service
 */

public interface ApiService {
    @GET("bins/4ev7m")
    Call<List<Recommendation>> getRecommendations();
}
