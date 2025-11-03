package com.xogame.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class ResultActivity : AppCompatActivity() {
    
    private lateinit var ivResultIcon: ImageView
    private lateinit var ivDrawIcon: ImageView
    private lateinit var tvResult: TextView
    private lateinit var tvWinningInfo: TextView
    private lateinit var btnNewGame: Button
    private lateinit var btnMainMenu: Button
    private lateinit var btnShare: Button
    
    private var winner = ""
    private var gameMode = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        
        winner = intent.getStringExtra("WINNER") ?: ""
        gameMode = intent.getStringExtra("GAME_MODE") ?: "single"
        
        initializeViews()
        setupResultDisplay()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        ivResultIcon = findViewById(R.id.ivResultIcon)
        ivDrawIcon = findViewById(R.id.ivDrawIcon)
        tvResult = findViewById(R.id.tvResult)
        tvWinningInfo = findViewById(R.id.tvWinningInfo)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnMainMenu = findViewById(R.id.btnMainMenu)
        btnShare = findViewById(R.id.btnShare)
    }
    
    private fun setupResultDisplay() {
        when (winner) {
            "X" -> {
                ivResultIcon.visibility = android.view.View.VISIBLE
                ivDrawIcon.visibility = android.view.View.GONE
                
                val resultText = if (gameMode == "single") {
                    "🎉 مبارك! فزت في اللعبة!"
                } else {
                    "🎉 فاز اللاعب الأول!"
                }
                
                tvResult.text = resultText
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.player_x_color))
                
                tvWinningInfo.text = "اللاعب X هو الفائز!"
                tvWinningInfo.setTextColor(ContextCompat.getColor(this, R.color.player_x_color))
            }
            
            "O" -> {
                ivResultIcon.visibility = android.view.View.VISIBLE
                ivDrawIcon.visibility = android.view.View.GONE
                
                val resultText = if (gameMode == "single") {
                    "😅 الكمبيوتر فاز! جرب مرة أخرى!"
                } else {
                    "🎉 فاز اللاعب الثاني!"
                }
                
                tvResult.text = resultText
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.player_o_color))
                
                tvWinningInfo.text = "اللاعب O هو الفائز!"
                tvWinningInfo.setTextColor(ContextCompat.getColor(this, R.color.player_o_color))
            }
            
            "draw" -> {
                ivResultIcon.visibility = android.view.View.GONE
                ivDrawIcon.visibility = android.view.View.VISIBLE
                
                tvResult.text = "🤝 تعادل!"
                tvResult.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
                
                tvWinningInfo.text = "لعبة ممتعة! جرب مرة أخرى!"
                tvWinningInfo.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
            }
        }
    }
    
    private fun setupClickListeners() {
        btnNewGame.setOnClickListener {
            // Start new game with same mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("GAME_MODE", gameMode)
            startActivity(intent)
            finish()
        }
        
        btnShare.setOnClickListener {
            shareResult()
        }
        
        btnMainMenu.setOnClickListener {
            // Return to main menu
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun shareResult() {
        val shareText = buildShareText()
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        
        startActivity(Intent.createChooser(shareIntent, "مشاركة النتيجة"))
    }
    
    private fun buildShareText(): String {
        return when (winner) {
            "X" -> if (gameMode == "single") {
                "🎮 لعبت لعبة XO وفزت! 👑 #لعبةXO #ألعاب_أندرويد"
            } else {
                "🎮 لعبت لعبة XO وفاز اللاعب الأول! 👑 #لعبةXO #ألعاب_أندرويد"
            }
            "O" -> if (gameMode == "single") {
                "🎮 لعبت لعبة XO والكمبيوتر فاز! 😅 #لعبةXO #ألعاب_أندرويد"
            } else {
                "🎮 لعبت لعبة XO وفاز اللاعب الثاني! 👑 #لعبةXO #ألعاب_أندرويد"
            }
            else -> {
                "🎮 لعبت لعبة XO وتعادلنا! 🤝 #لعبةXO #ألعاب_أندرويد"
            }
        }
    }
}