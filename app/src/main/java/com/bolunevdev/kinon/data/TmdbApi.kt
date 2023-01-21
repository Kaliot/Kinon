package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.data.entity.TmdbResultsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    fun getFilms(
        @Query(API_KEY) apiKey: String,
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int
    ): Call<TmdbResultsDto>

    companion object {
        private const val API_KEY = "api_key"
        private const val LANGUAGE = "language"
        private const val PAGE = "page"
    }
}