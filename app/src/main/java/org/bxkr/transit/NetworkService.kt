package org.bxkr.transit

import org.bxkr.transit.models.SearchStation
import org.bxkr.transit.models.DateTravel
import org.bxkr.transit.models.TrainCategory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object NetworkService {

    private const val baseUrl = "https://backend.cppktrain.ru"

    interface API {
        @GET("/train-schedule/search-station")
        fun searchStation(
            @Query("query") query: String,
            @Query("limit") limit: Int = 100
        ): Call<List<SearchStation>>

        @GET("/train-schedule/date-travel")
        fun dateTravel(
            @Query("date") date: String,
            @Query("fromStationId") fromStationId: Long,
            @Query("toStationId") toStationId: Long
        ): Call<List<DateTravel>>

        @GET("/train-schedule/train-category")
        fun trainCategory(): Call<List<TrainCategory>>
    }

    fun api(): API {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(API::class.java)
    }
}