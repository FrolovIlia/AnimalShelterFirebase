package com.example.animalshelterfirebase.utils

import android.util.Log
import com.example.animalshelterfirebase.data.Favourite
import com.google.firebase.firestore.FirebaseFirestore

fun updateFavorite(
    db: FirebaseFirestore,
    uid: String,
    favourite: Favourite,
    isFav: Boolean
) {
    if (isFav) {
        db.collection("users")
            .document(uid)
            .collection("favourites")
            .document(favourite.key)
            .set(favourite)
    } else {
        db.collection("users")
            .document(uid)
            .collection("favourites")
            .document(favourite.key)
            .delete()
    }
}

fun getAllFavsIds(db: FirebaseFirestore, uid: String, onFavs: (List<String>) -> Unit) {
    if (uid.isBlank() || uid == "guest") {
        Log.w("getAllFavsIds", "Invalid UID: $uid")
        onFavs(emptyList())
        return
    }

    db.collection("users")
        .document(uid)
        .collection("favourites")
        .get()
        .addOnSuccessListener { task ->
            val idsList = task.toObjects(Favourite::class.java)
            val keysList = idsList.map { it.key }
            onFavs(keysList)
        }
        .addOnFailureListener {
            Log.e("getAllFavsIds", "Failed to get favorites: ${it.message}", it)
            onFavs(emptyList())
        }
}