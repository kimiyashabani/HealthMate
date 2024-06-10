package com.example.healthmate;
// OpenFDAApi.java
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenFDAApi {
    @GET("drug/label.json")
    Call<DrugLabelResponse> getDrugLabels(@Query("search") String searchQuery, @Query("limit") int limit);
}
