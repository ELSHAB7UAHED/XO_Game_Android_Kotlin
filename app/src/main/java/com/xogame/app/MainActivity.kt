package com.xogame.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        val btnSinglePlayer = findViewById<Button>(R.id.btnSinglePlayer)
        val btnTwoPlayers = findViewById<Button>(R.id.btnTwoPlayers)
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        val btnAbout = findViewById<Button>(R.id.btnAbout)
        val btnExit = findViewById<Button>(R.id.btnExit)
        
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