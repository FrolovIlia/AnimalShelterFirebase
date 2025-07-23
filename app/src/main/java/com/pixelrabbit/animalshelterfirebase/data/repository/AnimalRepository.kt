package com.pixelrabbit.animalshelterfirebase.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.pixelrabbit.animalshelterfirebase.data.Favourite

class AnimalRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun getAnimals(
        category: String,
        favIds: List<String>,
        onResult: (List<Animal>) -> Unit
    ) {
        val collection = db.collection("animals")
        val query = if (category == "Все") collection else collection.whereEqualTo("category", category)

        query.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(Animal::class.java).map {
                    if (favIds.contains(it.key)) it.copy(isFavourite = true) else it
                }
                onResult(list)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to get animals", it)
                onResult(emptyList())
            }
    }

    fun getFavoriteIds(uid: String, onResult: (List<String>) -> Unit) {
        if (uid.isBlank() || uid == "guest") {
            onResult(emptyList())
            return
        }

        db.collection("users").document(uid).collection("favourites")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.toObjects(Favourite::class.java).map { it.key }
                onResult(ids)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to get favorite IDs", it)
                onResult(emptyList())
            }
    }

    fun getFavoriteAnimals(favIds: List<String>, onResult: (List<Animal>) -> Unit) {
        val validIds = favIds.filter { it.isNotBlank() }
        if (validIds.isEmpty()) {
            onResult(emptyList())
            return
        }

        db.collection("animals")
            .whereIn(FieldPath.documentId(), validIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val animals = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Animal::class.java)?.copy(key = doc.id, isFavourite = true)
                }
                onResult(animals)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to get favorite animals", it)
                onResult(emptyList())
            }
    }

    fun toggleFavorite(uid: String, animalKey: String, add: Boolean, onComplete: (Boolean) -> Unit) {
        val favRef = db.collection("users").document(uid).collection("favourites").document(animalKey)

        if (add) {
            favRef.set(Favourite(animalKey))
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener {
                    Log.e("AnimalRepository", "Failed to add favorite", it)
                    onComplete(false)
                }
        } else {
            favRef.delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener {
                    Log.e("AnimalRepository", "Failed to remove favorite", it)
                    onComplete(false)
                }
        }
    }

    fun getUserName(uid: String, onResult: (String) -> Unit) {
        if (uid == "guest" || uid.isBlank()) {
            onResult("Гость")
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getString("name") ?: ""
                onResult(name)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to get user name", it)
                onResult("")
            }
    }

    fun checkIfUserIsAdmin(uid: String, onResult: (Boolean) -> Unit) {
        if (uid == "guest" || uid.isBlank()) {
            onResult(false)
            return
        }

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val isAdmin = snapshot.getBoolean("isAdmin") == true
                onResult(isAdmin)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to check admin status", it)
                onResult(false)
            }
    }
}
