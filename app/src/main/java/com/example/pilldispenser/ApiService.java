package com.example.pilldispenser;

import java.sql.Time;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
   @GET("api/upcoming_pills")
    Call<UpcomingPills> getPillSchedule();

    @GET("/api/logs")
    Call<LogsResponse> getLogs();

    @GET("api/now")
    Call<TimeModel> getTime();

}