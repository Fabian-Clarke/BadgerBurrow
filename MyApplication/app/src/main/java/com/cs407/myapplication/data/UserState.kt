package com.cs407.myapplication.data

data class UserState(
    val id: Int = 0, // Room database ID (will be used in Milestone 3)
    val name: String = "", // User's display name
    val uid: String = "" // Firebase UID
)