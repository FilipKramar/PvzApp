package com.example.pvzapp.plant

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

class PlantAdapter(private val context: Context) :
    RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {
    private val plantList: MutableList<Plant> = mutableListOf()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return PlantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.bind(plant)
    }

    override fun getItemCount(): Int {
        return plantList.size
    }

    fun fetchPlantsFromFirestore() {
        db.collection("plants").get()
            .addOnSuccessListener { result ->
                plantList.clear()
                for (document in result) {
                    val plant = document.toObject(Plant::class.java)
                    plantList.add(plant)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching plants", e)
            }
    }

    fun filterPlants(searchQuery: String?) {
        val filteredList = if (searchQuery.isNullOrBlank()) {
            plantList
        } else {
            plantList.filter { it.name?.contains(searchQuery, ignoreCase = true) == true }
        }

        plantList.clear()
        plantList.addAll(filteredList)
        notifyDataSetChanged()
    }

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val plantPictureImageView: ImageView = itemView.findViewById(R.id.Picture)
        private val plantNameTextView: TextView = itemView.findViewById(R.id.name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val plant = plantList[position]
                    val intent = Intent(context, PlantDetailsActivity::class.java).apply {
                        putExtra(PlantDetailsActivity.EXTRA_PLANT, plant)
                    }
                    context.startActivity(intent)
                }
            }
        }


        fun bind(plant: Plant) {
            Glide.with(itemView).load(plant.picture).into(plantPictureImageView)
            plantNameTextView.text = plant.name
        }
    }

    companion object {
        private const val TAG = "PlantAdapter"
    }
}
