package com.xogame.app

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import java.util.*

class SoundManager(private val context: Context) {
    
    private lateinit var soundPool: SoundPool
    private var soundMap = HashMap<Int, Int>()
    private var isSoundEffectsEnabled = true
    private var isBackgroundMusicEnabled = false
    private var currentVolume = 1.0f
    
    companion object {
        private const val PREFS_NAME = "audio_settings"
        private const val SOUND_EFFECTS_KEY = "sound_effects_enabled"
        private const val BACKGROUND_MUSIC_KEY = "background_music_enabled"
        private const val VOLUME_KEY = "volume_level"
        
        // Sound IDs
        const val SOUND_CLICK = 1
        const val SOUND_MOVE = 2
        const val SOUND_WIN = 3
        const val SOUND_LOSE = 4
        const val SOUND_DRAW = 5
        const val SOUND_BUTTON_PRESS = 6
    }
    
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    init {
        loadSettings()
        initializeSoundPool()
    }
    
    private fun initializeSoundPool() {
        soundMap.clear()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        }
        
        // Load sound effects (no system sounds for now)
        // In a real app, you'd load your custom sound files here
        loadSoundEffects()
    }
    
    private fun loadSoundEffects() {
        // Note: No system sounds loading to avoid unresolved references
        // In production, you would load actual sound files from res/raw/ or assets/
        
        soundPool.setOnLoadCompleteListener { _, id, status ->
            if (status == 0) {
                // Sound loaded successfully
                println("Sound $id loaded successfully")
            }
        }
    }
    
    fun loadSettings() {
        isSoundEffectsEnabled = preferences.getBoolean(SOUND_EFFECTS_KEY, true)
        isBackgroundMusicEnabled = preferences.getBoolean(BACKGROUND_MUSIC_KEY, false)
        currentVolume = preferences.getFloat(VOLUME_KEY, 1.0f)
    }
    
    fun saveSettings() {
        val editor = preferences.edit()
        editor.putBoolean(SOUND_EFFECTS_KEY, isSoundEffectsEnabled)
        editor.putBoolean(BACKGROUND_MUSIC_KEY, isBackgroundMusicEnabled)
        editor.putFloat(VOLUME_KEY, currentVolume)
        editor.apply()
    }
    
    fun setSoundEffectsEnabled(enabled: Boolean) {
        isSoundEffectsEnabled = enabled
        saveSettings()
    }
    
    fun setBackgroundMusicEnabled(enabled: Boolean) {
        isBackgroundMusicEnabled = enabled
        saveSettings()
        
        if (enabled) {
            // Start background music logic would go here
        } else {
            // Stop background music logic would go here
        }
    }
    
    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        saveSettings()
    }
    
    fun isSoundEffectsEnabled(): Boolean = isSoundEffectsEnabled
    fun isBackgroundMusicEnabled(): Boolean = isBackgroundMusicEnabled
    fun getVolume(): Float = currentVolume
    
    fun playSound(soundId: Int) {
        if (!isSoundEffectsEnabled) return
        
        val sound = soundMap[soundId]
        sound?.let {
            try {
                soundPool.play(it, currentVolume, currentVolume, 0, 0, 1.0f)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun playClickSound() {
        playSound(SOUND_CLICK)
    }
    
    fun playMoveSound() {
        playSound(SOUND_MOVE)
    }
    
    fun playWinSound() {
        playSound(SOUND_WIN)
        // Play win sound multiple times for celebration effect
        soundPool.play(soundMap[SOUND_WIN] ?: 0, currentVolume, currentVolume, 1, 0, 1.0f)
        soundPool.play(soundMap[SOUND_WIN] ?: 0, currentVolume, currentVolume, 1, 1, 1.5f)
    }
    
    fun playLoseSound() {
        playSound(SOUND_LOSE)
    }
    
    fun playDrawSound() {
        playSound(SOUND_DRAW)
    }
    
    fun playButtonPressSound() {
        playSound(SOUND_BUTTON_PRESS)
    }
    
    // Method to release resources
    fun release() {
        soundPool.release()
    }
    
    // Method to check if audio focus is available
    private fun requestAudioFocus(): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        // Restore volume
                    }
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        // Pause music
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        // Pause music
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        // Lower volume
                    }
                }
            },
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
    
    // Method to play sequence of sounds
    fun playSoundSequence(soundIds: IntArray, delay: Long = 100L) {
        soundIds.forEachIndexed { index, soundId ->
            android.os.Handler().postDelayed({
                playSound(soundId)
            }, index * delay)
        }
    }
    
    // Method to play random sounds
    fun playRandomSound(vararg soundIds: Int) {
        if (soundIds.isNotEmpty()) {
            val randomSound = soundIds.random()
            playSound(randomSound)
        }
    }
}

// Vibration Manager for haptic feedback
class VibrationManager(private val context: Context) {
    
    fun vibrate(duration: Long = 50L) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(duration, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(duration)
        }
    }
    
    fun vibrateLong() {
        vibrate(200L)
    }
    
    fun vibrateShort() {
        vibrate(30L)
    }
    
    fun vibratePattern() {
        val pattern = longArrayOf(0, 50, 50, 50, 50, 100)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(pattern, -1)
        }
    }
}