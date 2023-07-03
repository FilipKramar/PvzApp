package com.example.pvzapp.plant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pvzapp.R
import com.google.firebase.firestore.FirebaseFirestore

class PlantListActivity : AppCompatActivity() {
    private lateinit var plantRecyclerView: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_list)

        plantRecyclerView = findViewById(R.id.plantRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        plantAdapter = PlantAdapter(this)
        plantRecyclerView.layoutManager = LinearLayoutManager(this)
        plantRecyclerView.adapter = plantAdapter

        plantAdapter.fetchPlantsFromFirestore()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s?.toString()?.trim()
                plantAdapter.filterPlants(searchQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        plantAdapter.fetchPlantsFromFirestore()
    }
}
