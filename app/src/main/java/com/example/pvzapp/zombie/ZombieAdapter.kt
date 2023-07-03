package com.example.pvzapp.zombie

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pvzapp.R
import com.google.firebase.firestore.FirebaseFirestore

class ZombieAdapter(private val context: Context) :
    RecyclerView.Adapter<ZombieAdapter.ZombieViewHolder>() {
    private val zombieList: MutableList<Zombie> = mutableListOf()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZombieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.zombie_item, parent, false)
        return ZombieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ZombieViewHolder, position: Int) {
        val zombie = zombieList[position]
        holder.bind(zombie)
    }

    override fun getItemCount(): Int {
        return zombieList.size
    }

    fun fetchZombiesFromFirestore() {
        db.collection("zombies").get()
            .addOnSuccessListener { result ->
                zombieList.clear()
                for (document in result) {
                    val zombie = document.toObject(Zombie::class.java)
                    zombieList.add(zombie)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching zombies", e)
            }
    }

    fun filterZombies(searchQuery: String?) {
        val filteredList = if (searchQuery.isNullOrBlank()) {
            zombieList
        } else {
            zombieList.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }
        }

        zombieList.clear()
        zombieList.addAll(filteredList)
        notifyDataSetChanged()
    }

    inner class ZombieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val zombiePictureImageView: ImageView = itemView.findViewById(R.id.Picture)
        private val zombieNameTextView: TextView = itemView.findViewById(R.id.name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val zombie = zombieList[position]
                    val intent = Intent(context, ZombieDetailsActivity::class.java).apply {
                        putExtra(ZombieDetailsActivity.EXTRA_ZOMBIE, zombie)
                    }
                    context.startActivity(intent)
                }
            }
        }


        fun bind(zombie: Zombie) {
            Glide.with(itemView).load(zombie.picture).into(zombiePictureImageView)
            zombieNameTextView.text = zombie.name
        }
    }

    companion object {
        private const val TAG = "ZombieAdapter"
    }
}

