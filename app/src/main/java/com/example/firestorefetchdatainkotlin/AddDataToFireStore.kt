package com.example.firestorefetchdatainkotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.firestorefetchdatainkotlin.User
import com.example.firestorefetchdatainkotlin.databinding.ActivityAddDataToFireStoreBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddDataToFireStore : AppCompatActivity() {
    private var binding: ActivityAddDataToFireStoreBinding? = null
    private var uri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataToFireStoreBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding?.img?.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 12)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && data.data != null) {
            uri = data.data
            binding?.img?.setImageURI(uri)

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imagesRef = storageRef.child("images") // Replace with your desired storage path
            val imageRef = imagesRef.child(binding!!.age.text.toString()) // Replace with a unique name

            val uploadTask = imageRef.putFile(uri!!)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully, get the download URL
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    imageUrl = downloadUrl.toString()
                    // Now you can save 'imageUrl' to Firestore
                    saveDataToFirestore()
                }
            }.addOnFailureListener { e ->
                // Handle any errors during the upload
                Log.e("FirebaseStorage", "Error uploading image: $e")
            }
        }
    }

    private fun saveDataToFirestore() {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("name") // Replace with your Firestore collection
        val ageString = binding!!.age.text.toString()
        val ageInt = ageString.toIntOrNull()

        val newUser = User(binding!!.firstName.text.toString(), binding!!.laststName.text.toString(),ageInt, imageUrl) // 'imageUrl' is the download URL

        userCollection
            .document(binding!!.age.text.toString()) // Specify the age as the document ID
            .set(newUser)
            .addOnSuccessListener {
                Log.d("FirestoreAddData", "Document added successfully with ID: ${binding!!.age.text}")
                var intent=Intent(this@AddDataToFireStore,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreAddData", "Error adding document: $e")
            }
    }
}
