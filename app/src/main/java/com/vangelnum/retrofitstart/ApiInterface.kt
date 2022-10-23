package com.vangelnum.retrofitstart

import com.vangelnum.retrofitstart.filmsutils.Films
import com.vangelnum.retrofitstart.searchfilms.SearchItem
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


const val API_KEY = "eaNFANoAIUun1E3nnRkuMemTeRy57UmCvHIymIf7B28"

interface ApiInterface {
    @GET("photos/?client_id=$API_KEY")
    suspend fun getMovies(
        @Query("order_by") order_by: String,
        @Query("per_page") per_page: Int,
        @Query("page") page: Int,
    ): Response<List<Films>>

    @GET("photos/random/?client_id=$API_KEY")
    suspend fun getMovies2(
        @Query("count") count: Int,
    ): Response<List<Films>>

    @GET("search/photos/?client_id=$API_KEY")
    suspend fun getSearch(
        @Query("query") query: String,
        @Query("per_page") per_page: Int,
    ): Response<SearchItem>

    //@Query("count") count: Int, @Query("order_by") order_by: String
    companion object {
        private var BASE_URL = "https://api.unsplash.com/"

        fun create(): ApiInterface {

            val newClient = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.HOURS)
                .readTimeout(0, TimeUnit.HOURS)
                .writeTimeout(0, TimeUnit.HOURS)
                .build()

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(newClient)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}