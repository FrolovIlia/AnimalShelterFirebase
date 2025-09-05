package com.pixelrabbit.animalshelterfirebase.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.Favourite
import com.pixelrabbit.animalshelterfirebase.data.model.Task

class AnimalRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun getAnimals(
        category: String,
        favIds: List<String>,
        onResult: (List<Animal>) -> Unit
    ) {
        val collection = db.collection("animals")
        val query =
            if (category == "Все") collection else collection.whereEqualTo("category", category)

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
                // Извлекаем documentId из каждого документа
                val ids = snapshot.documents.map { it.id }
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

    fun toggleFavorite(
        uid: String,
        animalKey: String,
        add: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        val favRef =
            db.collection("users").document(uid).collection("favourites").document(animalKey)

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

    fun getTasks(onLoaded: (List<Task>) -> Unit) {
        db.collection("tasks")
            .get()
            .addOnSuccessListener { result ->
                val loadedTasks = result.mapNotNull { doc ->
                    val task = doc.toObject(Task::class.java)
                    task.copy(key = doc.id)
                }
                onLoaded(loadedTasks)
            }
            .addOnFailureListener {
                Log.e("AnimalRepository", "Failed to get tasks", it)
                onLoaded(emptyList())
            }
    }

}