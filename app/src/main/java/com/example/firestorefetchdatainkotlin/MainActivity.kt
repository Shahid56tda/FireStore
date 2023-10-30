package com.example.firestorefetchdatainkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestorefetchdatainkotlin.databinding.ActivityMainBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var db: FirebaseFirestore? = null
    var userList: ArrayList<User>? = null
    var myAdapter: MyAdapter? = null
    var REQUEST_CODE = 1 // Define your request code


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
      binding!!.add.setOnClickListener{
          var intent=Intent(this@MainActivity,AddDataToFireStore::class.java)
          startActivity(intent)
          finish()
      }

        db = FirebaseFirestore.getInstance()
        binding!!.Rc1.layoutManager = LinearLayoutManager(this)
        binding!!.Rc1.setHasFixedSize(true)
        userList = ArrayList()
        myAdapter = MyAdapter(userList!!)



        // Set the adapter to the RecyclerView
        binding!!.Rc1.adapter = myAdapter

        val userCollection = db!!.collection("name")

        userCollection.get()
            .addOnSuccessListener { querySnapshot ->
                userList!!.clear()
                for (document in querySnapshot.documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        // Add the user to the list
                        userList!!.add(user)

                    }
                }
                // Notify the adapter that the data has changed
                myAdapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                Log.e("FirestoreFetchData", "Error fetching data: $exception")
            }
    }


}