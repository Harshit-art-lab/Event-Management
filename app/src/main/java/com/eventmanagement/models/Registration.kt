package com.eventmanagement.models

data class Registration(
    val id: String = "",
    val eventId: String = "",
    val userId: String = "",
    val status: String = "registered",
    val attended: Boolean = false,
    val registeredAt: String = ""
)