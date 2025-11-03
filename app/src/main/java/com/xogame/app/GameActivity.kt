package com.xogame.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors

class GameActivity : AppCompatActivity() {
    
    // UI Components
    private lateinit var tvGameStatus: TextView
    private lateinit var tvCurrentPlayer: TextView
    private lateinit var btnNewGame: Button
    private lateinit var btnMainMenu: Button
    private lateinit var gameCells: Array<Button>
    
    // Game logic
    private val gameBoard = Array(3) { Array(3) { "" } }
    private var currentPlayer = "X"
    private var gameMode = "single" // "single" or "two"
    private var isGameOver = false
    private var movesCount = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        
        gameMode = intent.getStringExtra("GAME_MODE") ?: "single"
        
        initializeViews()
        setupGameBoard()
        updateGameStatus()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        tvGameStatus = findViewById(R.id.tvGameStatus)
        tvCurrentPlayer = findViewById(R.id.tvCurrentPlayer)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnMainMenu = findViewById(R.id.btnMainMenu)
        
        // Initialize game cells
        gameCells = arrayOf(
            findViewById(R.id.btnCell1),
            findViewById(R.id.btnCell2),
            findViewById(R.id.btnCell3),
            findViewById(R.id.btnCell4),
            findViewById(R.id.btnCell5),
            findViewById(R.id.btnCell6),
            findViewById(R.id.btnCell7),
            findViewById(R.id.btnCell8),
            findViewById(R.id.btnCell9)
        )
    }
    
    private fun setupGameBoard() {
        for (i in gameCells.indices) {
            gameCells[i].setOnClickListener { onCellClick(i) }
            gameCells[i].text = ""
            gameCells[i].setBackgroundResource(R.drawable.game_cell_background)
        }
    }
    
    private fun setupClickListeners() {
        btnNewGame.setOnClickListener {
            resetGame()
        }
        
        btnMainMenu.setOnClickListener {
            finish()
        }
    }
    
    private fun onCellClick(position: Int) {
        if (isGameOver) return
        
        val row = position / 3
        val col = position % 3
        
        if (gameBoard[row][col] == "") {
            makeMove(row, col, currentPlayer)
            movesCount++
            
            // Check for win or draw
            val winner = checkWinner()
            if (winner != null) {
                endGame(winner)
            } else if (movesCount == 9) {
                endGame("draw")
            } else {
                // Switch player
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                
                // If single player and computer's turn
                if (gameMode == "single" && currentPlayer == "O") {
                    tvGameStatus.text = "كمبيوتر يفكر..."
                    tvGameStatus.setTextColor(ContextCompat.getColor(this, R.color.accent_orange))
                    
                    // Add delay for computer thinking
                    Handler(Looper.getMainLooper()).postDelayed({
                        makeComputerMove()
                        movesCount++
                        
                        val computerWinner = checkWinner()
                        if (computerWinner != null) {
                            endGame(computerWinner)
                        } else if (movesCount == 9) {
                            endGame("draw")
                        } else {
                            currentPlayer = "X"
                            updateGameStatus()
                        }
                    }, 800)
                } else {
                    updateGameStatus()
                }
            }
        }
    }
    
    private fun makeMove(row: Int, col: Int, player: String) {
        gameBoard[row][col] = player
        
        val position = row * 3 + col
        gameCells[position].text = player
        gameCells[position].setTextColor(
            if (player == "X") 
                ContextCompat.getColor(this, R.color.player_x_color) 
            else 
                ContextCompat.getColor(this, R.color.player_o_color)
        )
    }
    
    private fun makeComputerMove() {
        val bestMove = findBestMove()
        makeMove(bestMove.first, bestMove.second, "O")
    }
    
    private fun findBestMove(): Pair<Int, Int> {
        // Simple AI implementation - can be enhanced
        // For now, random move selection
        val availableMoves = mutableListOf<Pair<Int, Int>>()
        
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (gameBoard[i][j] == "") {
                    availableMoves.add(Pair(i, j))
                }
            }
        }
        
        return if (availableMoves.isNotEmpty()) {
            availableMoves.random()
        } else {
            Pair(0, 0) // fallback
        }
    }
    
    private fun checkWinner(): String? {
        // Check rows
        for (i in 0 until 3) {
            if (gameBoard[i][0] == gameBoard[i][1] && 
                gameBoard[i][1] == gameBoard[i][2] && 
                gameBoard[i][0].isNotEmpty()) {
                return gameBoard[i][0]
            }
        }
        
        // Check columns
        for (j in 0 until 3) {
            if (gameBoard[0][j] == gameBoard[1][j] && 
                gameBoard[1][j] == gameBoard[2][j] && 
                gameBoard[0][j].isNotEmpty()) {
                return gameBoard[0][j]
            }
        }
        
        // Check diagonals
        if (gameBoard[0][0] == gameBoard[1][1] && 
            gameBoard[1][1] == gameBoard[2][2] && 
            gameBoard[0][0].isNotEmpty()) {
            return gameBoard[0][0]
        }
        
        if (gameBoard[0][2] == gameBoard[1][1] && 
            gameBoard[1][1] == gameBoard[2][0] && 
            gameBoard[0][2].isNotEmpty()) {
            return gameBoard[0][2]
        }
        
        return null
    }
    
    private fun endGame(winner: String) {
        isGameOver = true
        
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("WINNER", winner)
        intent.putExtra("GAME_MODE", gameMode)
        startActivity(intent)
        finish()
    }
    
    private fun updateGameStatus() {
        val playerName = when {
            gameMode == "single" && currentPlayer == "X" -> "أنت"
            gameMode == "single" && currentPlayer == "O" -> "الكمبيوتر"
            gameMode == "two" && currentPlayer == "X" -> "اللاعب الأول"
            else -> "اللاعب الثاني"
        }
        
        tvGameStatus.text = "دور $playerName"
        tvGameStatus.setTextColor(ContextCompat.getColor(this, R.color.neon_blue))
        tvCurrentPlayer.text = currentPlayer
        tvCurrentPlayer.setTextColor(
            if (currentPlayer == "X") 
                ContextCompat.getColor(this, R.color.player_x_color) 
            else 
                ContextCompat.getColor(this, R.color.player_o_color)
        )
    }
    
    private fun resetGame() {
        // Clear game board
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                gameBoard[i][j] = ""
            }
        }
        
        // Reset UI
        for (cell in gameCells) {
            cell.text = ""
            cell.setBackgroundResource(R.drawable.game_cell_background)
        }
        
        currentPlayer = "X"
        isGameOver = false
        movesCount = 0
        
        updateGameStatus()
    }
}