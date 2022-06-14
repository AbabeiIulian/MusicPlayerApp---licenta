package com.licenta2022.musicplayerApp.data.entities

import java.io.Serializable

data class Song(
    val mediaId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val songUrl: String = "",
    val imageUrl: String = ""
) : Serializable