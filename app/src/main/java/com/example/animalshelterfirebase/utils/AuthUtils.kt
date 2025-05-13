package com.example.animalshelterfirebase.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun isAdmin(uid: String, onResult: (Boolean) -> Unit) {
    Firebase.firestore.collection("admin")
        .document(uid)
        .get()
        .addOnSuccessListener { doc ->
            val isAdmin = doc.getBoolean("isAdmin") == true
            onResult(isAdmin)
        }
        .addOnFailureListener {
            onResult(false)
        }
}
