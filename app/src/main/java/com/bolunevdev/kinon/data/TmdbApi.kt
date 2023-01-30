package com.bolunevdev.kinon.data

import com.bolunevdev.kinon.data.entity.TmdbResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/{category}")
    fun getFilms(
        @Path(CATEGORY_KEY) category: String,
        @Query(API_KEY) apiKey: String,
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int
    ): Call<TmdbResults>

    companion object {
        private const val CATEGORY_KEY = "category"
        private const val API_KEY = "api_key"
        private const val LANGUAGE = "language"
        private const val PAGE = "page"
    }
}