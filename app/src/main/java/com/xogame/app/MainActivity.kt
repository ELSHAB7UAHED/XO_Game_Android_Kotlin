package com.xogame.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views using findViewById
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Get buttons by ID and set click listeners
        val btnSinglePlayer = findViewById<Button>(R.id.btn_single_player)
        val btnTwoPlayers = findViewById<Button>(R.id.btn_two_players)
        val btnSettings = findViewById<Button>(R.id.btn_settings)
        val btnAbout = findViewById<Button>(R.id.btn_about)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        
        btnSinglePlayer.setOnClickListener {
            // Navigate to Game Activity in Single Player mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GAME_MODE", "single")
            startActivity(intent)
        }
        
        btnTwoPlayers.setOnClickListener {
            // Navigate to Game Activity in Two Player mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GAME_MODE", "two")
            startActivity(intent)
        }
        
        btnSettings.setOnClickListener {
            // Navigate to Settings Activity
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        btnAbout.setOnClickListener {
            // Navigate to About Activity
            startActivity(Intent(this, AboutActivity::class.java))
        }
        
        btnExit.setOnClickListener {
            // Exit the application
            finish()
        }
    }
}