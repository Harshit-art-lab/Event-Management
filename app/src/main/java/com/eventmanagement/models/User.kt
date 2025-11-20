package com.eventmanagement.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val usn: String = "",
    val role: String = "student",
    val rewardPoints: Int = 0
)