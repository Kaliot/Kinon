package com.bolunevdev.kinon

import android.os.Parcelable
//import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parcelize


@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String
) : Parcelable



