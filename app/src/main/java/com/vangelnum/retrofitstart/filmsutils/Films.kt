package com.vangelnum.retrofitstart.filmsutils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Films(
    val alt_description: @RawValue Any,
    val blur_hash: String,
    val color: String,
    val created_at: String,
    val current_user_collections: @RawValue List<Any>,
    val description: @RawValue Any,
    val downloads: Int,
    val exif: @RawValue Exif,
    val height: Int,
    val id: String,
    val liked_by_user: Boolean,
    val likes: Int,
    val links: @RawValue Links,
    val location: @RawValue Location,
    val promoted_at: String,
    val sponsorship: @RawValue Any,
    val topic_submissions: @RawValue TopicSubmissions,
    val updated_at: String,
    val urls: @RawValue Urls,
    val user: @RawValue User,
    val views: Int,
    val width: Int
) : Parcelable