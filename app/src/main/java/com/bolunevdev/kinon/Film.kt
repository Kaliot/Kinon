package com.bolunevdev.kinon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var rating: Float = 0f,
    val id: Int = poster,
    var isInFavorites: Boolean = false
) : Parcelable



