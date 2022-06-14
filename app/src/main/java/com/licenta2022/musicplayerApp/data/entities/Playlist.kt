package com.licenta2022.musicplayerApp.data.entities

data class Playlist (
    var songs: List<Song>? = null,
    var playlistName: String = "",
    var userID: String = ""
    )