package com.bolunevdev.remote_module.entity

import com.google.gson.annotations.SerializedName

data class TmdbResults(
    @SerializedName(PAGE)
    val page: Int,
    @SerializedName(RESULTS)
    val results: List<TmdbFilm>,
    @SerializedName(TOTAL_PAGES)
    val total_pages: Int,
    @SerializedName(TOTAL_RESULTS)
    val total_results: Int
) {
    companion object {
        private const val PAGE = "page"
        private const val RESULTS = "results"
        private const val TOTAL_PAGES = "total_pages"
        private const val TOTAL_RESULTS = "total_results"
    }
}