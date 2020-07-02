package com.ftw.firebaseupload

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_photo_data.*

class PhotoDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_data)

        btn_get_photo.setOnClickListener {
            db.collection("uploads")
                .whereEqualTo("imageName", "Test") //pake whereEqual buat contoh aja, biar cepet testnya
                .get()
                .addOnSuccessListener {
                    val model = it.toObjects(Upload::class.java)
                    Glide.with(this).load(model[0].imageUrl).into(img_photo_container)
                    Toast.makeText(this, "Berhasil! load ${model[0].imageUrl}", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }

    }
}