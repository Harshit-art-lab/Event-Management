// FILE: data/FirebaseRepository.kt
// Complete Firebase Backend Integration

package com.eventmanagement.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.eventmanagement.models.Event
import com.eventmanagement.models.Registration
import com.eventmanagement.models.User
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ==================== AUTHENTICATION ====================

    suspend fun signUp(
        name: String,
        email: String,
        usn: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            // Create Firebase Auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(Exception("User creation failed"))

            // Determine role based on email
            val role = if (email.startsWith("admin")) "admin" else "student"

            // Create user document in Firestore
            val userData = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                usn = usn,
                role = role,
                rewardPoints = 0
            )

            db.collection("users")
                .document(firebaseUser.uid)
                .set(userData)
                .await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
                ?: return Result.failure(Exception("Login failed"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun getUserData(userId: String): Result<User> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPoints(userId: String, points: Int): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .update("rewardPoints", points)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== EVENTS ====================

    suspend fun getAllEvents(): Result<List<Event>> {
        return try {
            val snapshot = db.collection("events")
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .await()

            val events = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Event::class.java)?.copy(id = doc.id)
            }

            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventById(eventId: String): Result<Event> {
        return try {
            val doc = db.collection("events").document(eventId).get().await()
            val event = doc.toObject(Event::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Event not found"))
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createEvent(event: Event): Result<String> {
        return try {
            val docRef = db.collection("events").add(event).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateEvent(eventId: String, event: Event): Result<Unit> {
        return try {
            db.collection("events")
                .document(eventId)
                .set(event)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            db.collection("events")
                .document(eventId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== REGISTRATIONS ====================

    suspend fun registerForEvent(eventId: String, userId: String): Result<Unit> {
        return try {
            // Check if already registered
            val existing = db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(Exception("Already registered"))
            }

            // Get event details
            val eventDoc = db.collection("events").document(eventId).get().await()
            val event = eventDoc.toObject(Event::class.java)
                ?: return Result.failure(Exception("Event not found"))

            // Check capacity
            val registrationCount = db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .await()
                .size()

            val status = if (registrationCount < event.capacity) {
                "registered"
            } else {
                "waitlisted"
            }

            // Create registration
            val registration = Registration(
                id = "",
                eventId = eventId,
                userId = userId,
                status = status,
                attended = false,
                registeredAt = System.currentTimeMillis().toString()
            )

            db.collection("registrations").add(registration).await()

            // Update event registered count
            val newCount = registrationCount + 1
            db.collection("events")
                .document(eventId)
                .update("registeredCount", newCount)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unregisterFromEvent(eventId: String, userId: String): Result<Unit> {
        return try {
            // Find registration
            val snapshot = db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Not registered"))
            }

            // Delete registration
            snapshot.documents.first().reference.delete().await()

            // Update event registered count
            val eventDoc = db.collection("events").document(eventId).get().await()
            val currentCount = eventDoc.getLong("registeredCount")?.toInt() ?: 0
            db.collection("events")
                .document(eventId)
                .update("registeredCount", maxOf(0, currentCount - 1))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMyRegistrations(userId: String): Result<List<Registration>> {
        return try {
            val snapshot = db.collection("registrations")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val registrations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Registration::class.java)?.copy(id = doc.id)
            }

            Result.success(registrations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isRegistered(eventId: String, userId: String): Result<Boolean> {
        return try {
            val snapshot = db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)  // ✅ Correct! Pass the exception
        }
    }

    suspend fun markAttendance(eventId: String, userId: String): Result<Unit> {
        return try {
            // Find registration
            val snapshot = db.collection("registrations")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.failure(Exception("Not registered for this event"))
            }

            val registrationDoc = snapshot.documents.first()
            val registration = registrationDoc.toObject(Registration::class.java)
                ?: return Result.failure(Exception("Registration not found"))

            // Check if already attended
            if (registration.attended) {
                return Result.failure(Exception("Attendance already marked"))
            }

            // Mark attendance
            registrationDoc.reference.update("attended", true).await()

            // Get event details for reward points
            val eventDoc = db.collection("events").document(eventId).get().await()
            val event = eventDoc.toObject(Event::class.java)
                ?: return Result.failure(Exception("Event not found"))

            // Add reward points to user
            val userDoc = db.collection("users").document(userId).get().await()
            val currentPoints = userDoc.getLong("rewardPoints")?.toInt() ?: 0
            val newPoints = currentPoints + event.rewardPoints

            db.collection("users")
                .document(userId)
                .update("rewardPoints", newPoints)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== HELPER FUNCTIONS ====================

    suspend fun seedInitialEvents(): Result<Unit> {
        return try {
            // Check if events already exist
            val existing = db.collection("events").limit(1).get().await()
            if (!existing.isEmpty) {
                return Result.success(Unit) // Events already exist
            }

            // Add initial events
            val events = listOf(
                Event(
                    id = "",
                    title = "Tech Fest 2025",
                    description = "Annual technical festival with coding competitions, hackathons, and tech talks.",
                    date = "2025-11-15",
                    venue = "Main Auditorium",
                    capacity = 200,
                    registeredCount = 0,
                    rewardPoints = 50
                ),
                Event(
                    id = "",
                    title = "Workshop on AI/ML",
                    description = "Hands-on workshop covering machine learning fundamentals and practical applications.",
                    date = "2025-11-20",
                    venue = "CS Lab 3",
                    capacity = 50,
                    registeredCount = 0,
                    rewardPoints = 30
                ),
                Event(
                    id = "",
                    title = "Cultural Night",
                    description = "An evening of music, dance, and cultural performances by students.",
                    date = "2025-11-25",
                    venue = "Open Ground",
                    capacity = 500,
                    registeredCount = 0,
                    rewardPoints = 20
                ),
                Event(
                    id = "",
                    title = "Career Fair 2025",
                    description = "Meet recruiters from top companies and explore job opportunities.",
                    date = "2025-12-01",
                    venue = "Convention Center",
                    capacity = 1000,
                    registeredCount = 0,
                    rewardPoints = 40
                )
            )

            events.forEach { event ->
                db.collection("events").add(event).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}