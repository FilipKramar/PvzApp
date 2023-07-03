package com.example.pvzapp.plant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.pvzapp.R
import com.google.firebase.firestore.FirebaseFirestore

class PlantDetailsActivity : AppCompatActivity() {
    private lateinit var plantPictureImageView: ImageView
    private lateinit var plantNameTextView: TextView
    private lateinit var plantAbilityTextView: TextView
    private lateinit var plantDescriptionTextView: TextView
    private lateinit var plantDamageTextView: TextView
    private lateinit var plantLevelTextView: TextView
    private lateinit var plantRechargeTextView: TextView
    private lateinit var plantCostTextView: TextView
    private lateinit var starButton: ImageButton
    private var isStarred: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_details)

        plantPictureImageView = findViewById(R.id.plantPictureImageView)
        plantNameTextView = findViewById(R.id.plantNameTextView)
        plantAbilityTextView = findViewById(R.id.plantAbilityTextView)
        plantDescriptionTextView = findViewById(R.id.plantDescriptionTextView)
        plantCostTextView = findViewById(R.id.plantCostTextView)
        plantDamageTextView = findViewById(R.id.plantDamageTextView)
        plantLevelTextView = findViewById(R.id.plantTerrainTextView)
        plantRechargeTextView = findViewById(R.id.plantRechargeTextView)

        starButton = findViewById(R.id.starButton)

        val plant = intent.getParcelableExtra<Plant>(EXTRA_PLANT)

        // Log the entire Intent and its extras
        Log.d("PlantDetailsActivity", "Received Intent: $intent")
        intent.extras?.keySet()?.forEach { key ->
            Log.d("PlantDetailsActivity", "Extra key: $key, value: ${intent.extras?.get(key)}")
        }

        plant?.let {
            isStarred = it.isstarred ?: false
            updateStarButtonState(isStarred)
            displayPlantDetails(it)
            starButton.setOnClickListener {
                isStarred = !isStarred!!
                updateIsStarredInFirestore(plant)
                updateStarButtonState(isStarred)
                val notificationTitle = if (isStarred == true) "Plant Starred" else "Plant Unstarred"
                val notificationMessage = "You ${if (isStarred == true) "starred" else "unstarred"} ${plant.name}."
                showNotification(notificationTitle, notificationMessage)
            }
        }
    }


    private fun displayPlantDetails(plant: Plant) {
        Glide.with(this).load(plant.picture).into(plantPictureImageView)
        plantNameTextView.text = plant.name
        plantAbilityTextView.text = plant.Ability
        plantDescriptionTextView.text = plant.Description
        plantCostTextView.text = plant.cost.toString()
        plantDamageTextView.text = plant.damage
        plantLevelTextView.text = plant.terrain
        plantRechargeTextView.text = plant.recharge
    }

    private fun updateStarButtonState(boolean: Boolean?) {
        if (boolean == true) {
            starButton.setImageResource(R.drawable.ic_star_filled)
        } else {
            starButton.setImageResource(R.drawable.ic_star_transparent)
        }
    }

    private fun updateIsStarredInFirestore(plant: Plant) {
        val db = FirebaseFirestore.getInstance()
        val plantsCollectionRef = db.collection("plants")

        plantsCollectionRef.whereEqualTo("name", plant.name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val updatedValue = isStarred ?: false // Use the current value of isStarred, or default to false if null
                    document.reference.update("isstarred", updatedValue)
                        .addOnSuccessListener {
                            // Successfully updated the value in Firestore
                            // Fetch the updated data from Firestore and assign it to the plant object
                            document.reference.get()
                                .addOnSuccessListener { updatedDocument ->
                                    val updatedPlant = updatedDocument.toObject(Plant::class.java)
                                    plant.isstarred = updatedPlant?.isstarred
                                    // Update the UI or perform any other necessary actions
                                }
                                .addOnFailureListener { e ->
                                    // Error occurred while fetching the updated data
                                    // Handle the error accordingly
                                }
                        }
                        .addOnFailureListener { e ->
                            // Error occurred while updating the value in Firestore
                            // Handle the error accordingly
                        }
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while querying the documents
                // Handle the error accordingly
            }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "PlantChannel"
        val channelName = getString(R.string.channel_name)
        val channelDescription = getString(R.string.channel_description)
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                this.description = channelDescription
                enableLights(true)
                lightColor = Color.RED
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title).setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        const val EXTRA_PLANT = "extra_plant"
    }
}
