package com.example.firestorefetchdatainkotlin


import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firestorefetchdatainkotlin.databinding.ItemListBinding
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(val userList :ArrayList<User>) : RecyclerView.Adapter<MyAdapter.viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.viewHolder {
        return viewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false))
    }

    override fun onBindViewHolder(holder: MyAdapter.viewHolder, position: Int) {
        holder.binding.firstName.text=userList[position].firstName
        holder.binding.laststName.text=userList[position].lastName
        holder.binding.age.text= userList[position].age.toString()
        Glide.with(holder.binding.age.context).load(userList[position].img).into(holder.binding.img)


        holder.itemView.setOnClickListener {
            // Create an intent to open the second activity
            val context = holder.itemView.context
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("list",userList[position])
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
          // Consume the long click event
            val user = userList[position]
            val alertDialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete ${user.firstName} ${user.lastName}?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteDocument(position)

                }
                .setNegativeButton("No") { _, _ -> }
                .create()

            alertDialog.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
   public class viewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
       var binding:ItemListBinding=ItemListBinding.bind(itemView)

    }

    private fun deleteDocument(position: Int) {
        val firestore = FirebaseFirestore.getInstance()


        val user = userList[position]
        val docRef = firestore.collection("name").document(user.age.toString())

        docRef.delete()
            .addOnSuccessListener {
                // Document deleted successfully
                userList.removeAt(position)
                notifyItemRemoved(position)
            }
            .addOnFailureListener { e ->
                // Handle the error
                Log.e("MyAdapter", "Error deleting document: $e")
            }
    }
}