package com.karunadakote.data.model

import java.io.Serializable

data class Fort(

    val id: Int,

    val name: String,

    val lat: Double,

    val lng: Double,

    val description: String,

    val image: String = "",

    val dynasty: String = "",

    val yearBuilt: String = "",

    val fortType: String = "",

    val districtName: String = "",

    val highlights: List<String> = emptyList()

) : Serializable