package com.pixelrabbit.animalshelterfirebase.utils

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun isAdmin(uid: String, onResult: (Boolean) -> Unit) {
    if (uid.isBlank() || uid == "guest") { // Добавьте проверку для гостя, если нужно
        onResult(false)
        return
    }

    Firebase.firestore.collection("users") // <-- ИЗМЕНЕНИЕ: теперь проверяем коллекцию "users"
        .document(uid)
        .get()
        .addOnSuccessListener { doc ->
            if (doc.exists()) {
                val isAdmin = doc.getBoolean("isAdmin") == true
                onResult(isAdmin)
                Log.d("isAdmin", "Admin status for $uid: $isAdmin")
            } else {
                Log.d("isAdmin", "User document $uid not found in 'users' collection.")
                onResult(false) // Если документа нет, то не администратор
            }
        }
        .addOnFailureListener { e ->
            Log.e("isAdmin", "Error checking admin status for UID: $uid", e)
            onResult(false)
        }
}
