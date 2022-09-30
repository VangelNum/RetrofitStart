package com.vangelnum.retrofitstart

import com.vangelnum.retrofitstart.filmsutils.Films
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val API_KEY = "eaNFANoAIUun1E3nnRkuMemTeRy57UmCvHIymIf7B28"

interface ApiInterface {
    @GET("photos/random/?client_id=$API_KEY")
    fun getMovies(@Query("count") count: Int): Call<List<Films>>

    companion object {

        var BASE_URL = "https://api.unsplash.com/"

        fun create(): ApiInterface {


            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}