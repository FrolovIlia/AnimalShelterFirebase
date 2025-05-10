package com.example.animalshelterfirebase.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun isAdmin(onResult: (Boolean) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        onResult(false)
        return
    }

    Firebase.firestore.collection("admin")
        .document(user.uid)
        .get()
        .addOnSuccessListener { doc ->
            val isAdmin = doc.getBoolean("isAdmin") == true
            onResult(isAdmin)
        }
        .addOnFailureListener {
            onResult(false)
        }
}
