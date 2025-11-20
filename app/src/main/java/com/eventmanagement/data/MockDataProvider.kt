package com.eventmanagement.data

import com.eventmanagement.models.Event
import com.eventmanagement.models.Registration
import com.eventmanagement.models.User

object MockDataProvider {

    // Mock logged-in user
    var currentUser: User? = User(
        id = "1",
        name = "John Doe",
        email = "john@college.edu",
        usn = "1AB20CS001",
        role = "student",
        rewardPoints = 150
    )

    // Mock events list
    val events = mutableListOf(
        Event(
            id = "1",
            title = "Tech Fest 2025",
            description = "Annual technical festival with coding competitions, hackathons, and tech talks.",
            date = "2025-11-15",
            venue = "Main Auditorium",
            capacity = 200,
            registeredCount = 145,
            rewardPoints = 50
        ),
        Event(
            id = "2",
            title = "Workshop on AI/ML",
            description = "Hands-on workshop covering machine learning fundamentals and practical applications.",
            date = "2025-11-20",
            venue = "CS Lab 3",
            capacity = 50,
            registeredCount = 48,
            rewardPoints = 30
        ),
        Event(
            id = "3",
            title = "Cultural Night",
            description = "An evening of music, dance, and cultural performances by students.",
            date = "2025-11-25",
            venue = "Open Ground",
            capacity = 500,
            registeredCount = 320,
            rewardPoints = 20
        ),
        Event(
            id = "4",
            title = "Career Fair 2025",
            description = "Meet recruiters from top companies and explore job opportunities.",
            date = "2025-12-01",
            venue = "Convention Center",
            capacity = 1000,
            registeredCount = 756,
            rewardPoints = 40
        )
    )

    // Mock registrations for current user
    val myRegistrations = mutableListOf(
        Registration(
            id = "r1",
            eventId = "1",
            userId = "1",
            status = "registered",
            attended = false,
            registeredAt = "2025-10-10"
        ),
        Registration(
            id = "r2",
            eventId = "2",
            userId = "1",
            status = "waitlisted",
            attended = false,
            registeredAt = "2025-10-12"
        )
    )

    // Function to get event by ID
    fun getEventById(eventId: String): Event? {
        return events.find { it.id == eventId }
    }

    // Function to check if user is registered for event
    fun isRegistered(eventId: String): Boolean {
        return myRegistrations.any { it.eventId == eventId }
    }

    // Function to register for event
    fun registerForEvent(eventId: String): Boolean {
        if (isRegistered(eventId)) return false

        val event = getEventById(eventId) ?: return false

        val status = if (event.registeredCount < event.capacity) {
            "registered"
        } else {
            "waitlisted"
        }

        myRegistrations.add(
            Registration(
                id = "r${myRegistrations.size + 1}",
                eventId = eventId,
                userId = currentUser?.id ?: "1",
                status = status,
                attended = false,
                registeredAt = "2025-10-20"
            )
        )

        // Update registered count
        val index = events.indexOfFirst { it.id == eventId }
        if (index != -1) {
            events[index] = events[index].copy(registeredCount = events[index].registeredCount + 1)
        }

        return true
    }

    // Function to unregister from event
    fun unregisterFromEvent(eventId: String): Boolean {
        val removed = myRegistrations.removeIf { it.eventId == eventId }

        if (removed) {
            // Update registered count
            val index = events.indexOfFirst { it.id == eventId }
            if (index != -1) {
                events[index] = events[index].copy(
                    registeredCount = maxOf(0, events[index].registeredCount - 1)
                )
            }
        }

        return removed
    }

    // Function to mark attendance
    fun markAttendance(eventId: String): Boolean {
        val registration = myRegistrations.find { it.eventId == eventId } ?: return false
        val index = myRegistrations.indexOf(registration)
        myRegistrations[index] = registration.copy(attended = true)

        // Add reward points
        val event = getEventById(eventId)
        if (event != null && currentUser != null) {
            currentUser = currentUser?.copy(
                rewardPoints = currentUser!!.rewardPoints + event.rewardPoints
            )
        }

        return true
    }
}