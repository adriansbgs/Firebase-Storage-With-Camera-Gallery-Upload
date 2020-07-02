package com.ftw.firebaseupload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private lateinit var storageRef: StorageReference
    private lateinit var dbRef: CollectionReference
    private var fileLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageRef = storage.getReference("uploads")
        dbRef = db.collection("uploads")

        btn_pick_image.setOnClickListener {
            //CropImage library
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        btn_upload.setOnClickListener {
            if (imageUri != null) {
                uploadFile()
            } else Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()

        }

        tv_show_upload.setOnClickListener {
            startActivity(Intent(this, PhotoDataActivity::class.java))
        }

    }

    private fun uploadFile() {
        val fileExtension = getFileExtension(imageUri) //ambil file ekstensi
        Log.d("MainActivity", "Isi fileExtension: $fileExtension")
        val fileReference = storageRef.child(
            System.currentTimeMillis().toString() + "." + fileExtension
        ) //nama filenya pake milliseconds + ekstensi

        fileReference.putFile(imageUri) //proses naro gambar ke firebase storage
            .addOnSuccessListener {

                Handler().postDelayed({
                    progress_horizontal.progress = 0
                }, 500) //delay dikit buat reset progress bar pas udah 100%

                it.storage.downloadUrl.addOnCompleteListener {
                    fileLink = it.result.toString() //ambil link url
                    Log.d("MainActivity", "isi filelink: $fileLink")
                    val upload = Upload(
                        et_filename.text.toString().trim(),
                        fileLink
                    ) // isinya nama, dan url (Object Class)

                    dbRef.add(upload) //upload ke Database Firestore
                }
                Toast.makeText(this, "Upload success!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount) // cara biar progressbarnya gerak ngikutin berapa jumlah data yg lagi keupload
                progress_horizontal.progress = progress.toInt()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                imageUri = result.uri
                Glide.with(this).load(imageUri).into(img_photo)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun getFileExtension(uri: Uri): String {

        //cara biar dapetin gambarnya .jpg/.png/.bmp/ ya pokoknya dapetin ekstensi filenya heuheu
        val getExtensionType = uri.toString().substring(uri.toString().lastIndexOf("."))
        Log.d("MainActivity", "getFileExtension: $getExtensionType")
        return getExtensionType
    }
}