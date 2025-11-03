package com.xogame.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var btnBack: Button
    private lateinit var rgDifficulty: RadioGroup
    private lateinit var rbEasy: RadioButton
    private lateinit var rbMedium: RadioButton
    private lateinit var rbHard: RadioButton
    private lateinit var switchSoundEffects: Switch
    private lateinit var switchBackgroundMusic: Switch
    
    private lateinit var sharedPreferences: SharedPreferences
    
    companion object {
        private const val PREFS_NAME = "game_settings"
        private const val DIFFICULTY_KEY = "difficulty"
        private const val SOUND_EFFECTS_KEY = "sound_effects"
        private const val BACKGROUND_MUSIC_KEY = "background_music"
        
        // Difficulty levels
        const val DIFFICULTY_EASY = "easy"
        const val DIFFICULTY_MEDIUM = "medium"
        const val DIFFICULTY_HARD = "hard"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        
        initializeViews()
        loadSettings()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        rgDifficulty = findViewById(R.id.rgDifficulty)
        rbEasy = findViewById(R.id.rbEasy)
        rbMedium = findViewById(R.id.rbMedium)
        rbHard = findViewById(R.id.rbHard)
        switchSoundEffects = findViewById(R.id.switchSoundEffects)
        switchBackgroundMusic = findViewById(R.id.switchBackgroundMusic)
    }
    
    private fun loadSettings() {
        // Load difficulty setting
        val difficulty = sharedPreferences.getString(DIFFICULTY_KEY, DIFFICULTY_MEDIUM)
        when (difficulty) {
            DIFFICULTY_EASY -> rbEasy.isChecked = true
            DIFFICULTY_MEDIUM -> rbMedium.isChecked = true
            DIFFICULTY_HARD -> rbHard.isChecked = true
        }
        
        // Load sound settings
        val soundEffectsEnabled = sharedPreferences.getBoolean(SOUND_EFFECTS_KEY, true)
        switchSoundEffects.isChecked = soundEffectsEnabled
        
        val backgroundMusicEnabled = sharedPreferences.getBoolean(BACKGROUND_MUSIC_KEY, false)
        switchBackgroundMusic.isChecked = backgroundMusicEnabled
    }
    
    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            saveSettings()
            finish()
        }
        
        rgDifficulty.setOnCheckedChangeListener { _, _ ->
            saveSettings()
        }
        
        switchSoundEffects.setOnCheckedChangeListener { _, _ ->
            saveSettings()
        }
        
        switchBackgroundMusic.setOnCheckedChangeListener { _, _ ->
            saveSettings()
        }
    }
    
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        
        // Save difficulty
        val selectedId = rgDifficulty.checkedRadioButtonId
        val difficulty = when (selectedId) {
            R.id.rbEasy -> DIFFICULTY_EASY
            R.id.rbHard -> DIFFICULTY_HARD
            else -> DIFFICULTY_MEDIUM // default to medium
        }
        editor.putString(DIFFICULTY_KEY, difficulty)
        
        // Save sound settings
        editor.putBoolean(SOUND_EFFECTS_KEY, switchSoundEffects.isChecked)
        editor.putBoolean(BACKGROUND_MUSIC_KEY, switchBackgroundMusic.isChecked)
        
        editor.apply()
    }
    
    companion object {
        fun getDifficulty(preferences: SharedPreferences): String {
            return preferences.getString(DIFFICULTY_KEY, DIFFICULTY_MEDIUM) ?: DIFFICULTY_MEDIUM
        }
        
        fun isSoundEffectsEnabled(preferences: SharedPreferences): Boolean {
            return preferences.getBoolean(SOUND_EFFECTS_KEY, true)
        }
        
        fun isBackgroundMusicEnabled(preferences: SharedPreferences): Boolean {
            return preferences.getBoolean(BACKGROUND_MUSIC_KEY, false)
        }
    }
}