package com.example.pvzapp.zombie

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

class ZombieDetailsActivity : AppCompatActivity() {
    private lateinit var zombiePictureImageView: ImageView
    private lateinit var zombieNameTextView: TextView
    private lateinit var zombieAbilityTextView: TextView
    private lateinit var zombieDescriptionTextView: TextView
    private lateinit var zombieToughnessLabelTextView: TextView
    private lateinit var zombieToughnessTextView: TextView
    private lateinit var starButton: ImageButton
    private var isStarred: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zombie_details)

        zombiePictureImageView = findViewById(R.id.zombiePictureImageView)
        zombieNameTextView = findViewById(R.id.zombieNameTextView)
        zombieAbilityTextView = findViewById(R.id.zombieAbilityTextView)
        zombieDescriptionTextView = findViewById(R.id.zombieDescriptionTextView)
        zombieToughnessLabelTextView = findViewById(R.id.zombieToughnessLabelTextView)
        zombieToughnessTextView = findViewById(R.id.zombieToughnessTextView)
        starButton = findViewById(R.id.starButton)

        val zombie = intent.getParcelableExtra<Zombie>(EXTRA_ZOMBIE)

        zombie?.let {
            isStarred=it.isstarred
            updateStarButtonState(isStarred)
            displayZombieDetails(it)
            starButton.setOnClickListener {
                isStarred = !isStarred!!
                updateIsStarredInFirestore(zombie)
                updateStarButtonState(isStarred)
                val notificationTitle = if (isStarred == true) "Zombie Starred" else "Zombie Unstarred"
                val notificationMessage = "You ${if (isStarred == true) "starred" else "unstarred"} ${zombie.name}."
                showNotification(notificationTitle, notificationMessage)
            }
        }
    }

    private fun displayZombieDetails(zombie: Zombie) {
        Glide.with(this).load(zombie.picture).into(zombiePictureImageView)

        zombieNameTextView.text = zombie.name
        zombieAbilityTextView.text = zombie.Ability
        zombieDescriptionTextView.text = zombie.Description
        zombieToughnessLabelTextView.text = getString(R.string.zombie_toughness_label)
        zombieToughnessTextView.text = zombie.toughness
        Log.d("MyTag", "isStarred value: na pocetku ovo je  ${zombie.isstarred}")

        if (zombie.isstarred==true) {
            Log.d("MyTag", "isStarred value: ${zombie.isstarred}")
            isStarred = true
        } else {
            isStarred = false
        }


        Log.d("MyTag", "isStarred value: na pocetku $isStarred")
    }

    private fun updateStarButtonState(boolean: Boolean?) {
        if (boolean == true) {
            starButton.setImageResource(R.drawable.ic_star_filled)
        } else {
            starButton.setImageResource(R.drawable.ic_star_transparent)
        }
    }

    private fun updateIsStarredInFirestore(zombie: Zombie) {
        val db = FirebaseFirestore.getInstance()
        val zombiesCollectionRef = db.collection("zombies")

        zombiesCollectionRef.whereEqualTo("name", zombie.name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val updatedValue = isStarred ?: false // Use the current value of isStarred, or default to false if null
                    document.reference.update("isstarred", updatedValue)
                        .addOnSuccessListener {
                            // Successfully updated the value in Firestore
                            // Fetch the updated data from Firestore and assign it to the zombie object
                            document.reference.get()
                                .addOnSuccessListener { updatedDocument ->
                                    val updatedZombie = updatedDocument.toObject(Zombie::class.java)
                                    zombie.isstarred = updatedZombie?.isstarred
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
        val channelId = "ZombieChannel"
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
        const val EXTRA_ZOMBIE = "extra_zombie"
    }
}
