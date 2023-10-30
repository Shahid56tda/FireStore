package com.example.firestorefetchdatainkotlin
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firestorefetchdatainkotlin.MainActivity
import com.example.firestorefetchdatainkotlin.User
import com.example.firestorefetchdatainkotlin.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UpdateActivity : AppCompatActivity() {
    var binding: ActivityUpdateBinding? = null
    var uri:Uri?=null
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val intent = intent
        val person = intent.getSerializableExtra("list") as? User

        if (person != null) {
            binding!!.firstName.setText(person.firstName)
            binding!!.laststName.setText(person.lastName)
            binding!!.age.setText(person.age.toString())
            Glide.with(this@UpdateActivity).load(person.img).into(binding!!.img)
        }

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
                    binding!!.bt1.setOnClickListener {
                        val updatedFirstName = binding!!.firstName.text.toString()
                        val updatedLastName = binding!!.laststName.text.toString()
                        val updatedAge = binding!!.age.text.toString()
                        val person = intent.getSerializableExtra("list") as? User
                        val updateimg = imageUrl


                        val updatedPerson = User(updatedFirstName, updatedLastName, updatedAge.toInt(), updateimg)

                        val db = FirebaseFirestore.getInstance()
                        val docRef = db.collection("name").document(updatedAge)

                        docRef.set(updatedPerson)
                            .addOnSuccessListener {
                                Log.d("UpdateActivity", "Data updated successfully")
                                Toast.makeText(this@UpdateActivity, "Data updated successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@UpdateActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("UpdateActivity", "Error updating data: $e")
                                Toast.makeText(this@UpdateActivity, "Error updating data: $e", Toast.LENGTH_SHORT).show()
                            }
                    }

                }
            }.addOnFailureListener { e ->
                // Handle any errors during the upload
                Log.e("FirebaseStorage", "Error uploading image: $e")
            }
        }
    }
}
