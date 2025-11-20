package com.eventmanagement.models

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val venue: String = "",
    val capacity: Int = 0,
    val registeredCount: Int = 0,
    val rewardPoints: Int = 0,
    val imageUrl: String = ""
)