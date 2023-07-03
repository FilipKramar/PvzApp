package com.example.pvzapp.zombie

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

class ZombieListActivity : AppCompatActivity() {
    private lateinit var zombieRecyclerView: RecyclerView
    private lateinit var zombieAdapter: ZombieAdapter
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zombie_list)

        zombieRecyclerView = findViewById(R.id.zombieRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        zombieAdapter = ZombieAdapter(this)
        zombieRecyclerView.layoutManager = LinearLayoutManager(this)
        zombieRecyclerView.adapter = zombieAdapter

        zombieAdapter.fetchZombiesFromFirestore()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchQuery = s?.toString()?.trim()
                zombieAdapter.filterZombies(searchQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        zombieAdapter.fetchZombiesFromFirestore()
    }

}

