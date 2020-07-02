package com.ftw.firebaseupload

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class App: Application() {
}

val db = Firebase.firestore
val storage = FirebaseStorage.getInstance()
val auth = FirebaseAuth.getInstance()
