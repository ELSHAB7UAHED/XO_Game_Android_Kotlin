package com.xogame.app

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class StatisticsActivity : AppCompatActivity() {
    
    private lateinit var tvTotalGames: TextView
    private lateinit var tvPlayerWins: TextView
    private lateinit var tvComputerWins: TextView
    private lateinit var tvDraws: TextView
    private lateinit var tvWinRate: TextView
    private lateinit var tvComputerWinRate: TextView
    private lateinit var tvDrawRate: TextView
    private lateinit var btnReset: Button
    private lateinit var btnBack: Button
    
    private lateinit var preferences: SharedPreferences
    private lateinit var gameManager: GameManager
    
    companion object {
        private const val PREFS_NAME = "game_statistics"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        
        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        gameManager = GameManager()
        
        initializeViews()
        loadStatistics()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        tvTotalGames = findViewById(R.id.tvTotalGames)
        tvPlayerWins = findViewById(R.id.tvPlayerWins)
        tvComputerWins = findViewById(R.id.tvComputerWins)
        tvDraws = findViewById(R.id.tvDraws)
        tvWinRate = findViewById(R.id.tvWinRate)
        tvComputerWinRate = findViewById(R.id.tvComputerWinRate)
        tvDrawRate = findViewById(R.id.tvDrawRate)
        btnReset = findViewById(R.id.btnReset)
        btnBack = findViewById(R.id.btnBack)
    }
    
    private fun loadStatistics() {
        gameManager.loadStatistics(preferences)
        updateStatisticsDisplay()
    }
    
    private fun updateStatisticsDisplay() {
        val statistics = gameManager.getStatistics()
        
        // Update text displays
        tvTotalGames.text = "إجمالي الألعاب: ${statistics.totalGames}"
        tvPlayerWins.text = "انتصاراتك: ${statistics.playerWins}"
        tvComputerWins.text = "انتصارات الكمبيوتر: ${statistics.computerWins}"
        tvDraws.text = "التعادل: ${statistics.draws}"
        
        // Update rate displays with percentage symbols and colors
        val winRate = String.format("%.1f%%", statistics.getWinRate())
        val computerWinRate = String.format("%.1f%%", statistics.getComputerWinRate())
        val drawRate = String.format("%.1f%%", statistics.getDrawRate())
        
        tvWinRate.text = "معدل انتصارك: $winRate"
        tvComputerWinRate.text = "معدل انتصار الكمبيوتر: $computerWinRate"
        tvDrawRate.text = "معدل التعادل: $drawRate"
        
        // Set colors based on performance
        when {
            statistics.getWinRate() >= 70 -> {
                tvWinRate.setTextColor(ContextCompat.getColor(this, R.color.success_green))
            }
            statistics.getWinRate() >= 40 -> {
                tvWinRate.setTextColor(ContextCompat.getColor(this, R.color.accent_orange))
            }
            else -> {
                tvWinRate.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            }
        }
        
        // Set color for computer win rate (inverse logic)
        when {
            statistics.getComputerWinRate() <= 30 -> {
                tvComputerWinRate.setTextColor(ContextCompat.getColor(this, R.color.success_green))
            }
            statistics.getComputerWinRate() <= 60 -> {
                tvComputerWinRate.setTextColor(ContextCompat.getColor(this, R.color.accent_orange))
            }
            else -> {
                tvComputerWinRate.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            }
        }
        
        // Set draw rate color
        when {
            statistics.getDrawRate() <= 20 -> {
                tvDrawRate.setTextColor(ContextCompat.getColor(this, R.color.success_green))
            }
            statistics.getDrawRate() <= 50 -> {
                tvDrawRate.setTextColor(ContextCompat.getColor(this, R.color.accent_orange))
            }
            else -> {
                tvDrawRate.setTextColor(ContextCompat.getColor(this, R.color.warning_yellow))
            }
        }
    }
    
    private fun setupClickListeners() {
        btnReset.setOnClickListener {
            showResetConfirmationDialog()
        }
        
        btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun showResetConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("إعادة تعيين الإحصائيات")
            .setMessage("هل أنت متأكد من إعادة تعيين جميع الإحصائيات؟\n\nلا يمكن التراجع عن هذا الإجراء.")
            .setPositiveButton("نعم") { _, _ ->
                resetStatistics()
            }
            .setNegativeButton("لا", null)
            .show()
    }
    
    private fun resetStatistics() {
        gameManager.resetStatistics()
        gameManager.saveStatistics(preferences)
        updateStatisticsDisplay()
        
        // Show confirmation message
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("تم!")
            .setMessage("تم إعادة تعيين الإحصائيات بنجاح.")
            .setPositiveButton("حسناً", null)
            .show()
    }
}