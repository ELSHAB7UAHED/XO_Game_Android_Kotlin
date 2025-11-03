package com.xogame.app

import android.content.SharedPreferences
import android.util.Log

class GameManager {
    
    companion object {
        private const val TAG = "GameManager"
    }
    
    enum class Difficulty {
        EASY, MEDIUM, HARD
    }
    
    enum class GameState {
        IN_PROGRESS, WON_X, WON_O, DRAW
    }
    
    // Current game state - استخدام var بدلاً من val للمتغيرات القابلة للتعديل
    private var gameBoard: Array<Array<String>> = Array(3) { Array(3) { "" } }
    private var currentPlayer = "X"
    private var gameState = GameState.IN_PROGRESS
    private var difficulty = Difficulty.MEDIUM
    
    // Game statistics - استخدام var بدلاً من val
    private var totalGames = 0
    private var playerWins = 0
    private var computerWins = 0
    private var draws = 0
    
    // Initialize game with difficulty
    fun initializeGame(difficulty: Difficulty) {
        resetBoard()
        this.difficulty = difficulty
        currentPlayer = "X"
        gameState = GameState.IN_PROGRESS
        Log.d(TAG, "Game initialized with difficulty: $difficulty")
    }
    
    // Make a move on the board
    fun makeMove(row: Int, col: Int, player: String): Boolean {
        if (isValidMove(row, col)) {
            gameBoard[row][col] = player
            updateGameState()
            return true
        }
        return false
    }
    
    // Check if move is valid
    private fun isValidMove(row: Int, col: Int): Boolean {
        return row in 0..2 && col in 0..2 && 
               gameBoard[row][col] == "" && 
               gameState == GameState.IN_PROGRESS
    }
    
    // Get AI move based on difficulty
    fun getAIMove(): Pair<Int, Int> {
        return when (difficulty) {
            Difficulty.EASY -> getRandomMove()
            Difficulty.MEDIUM -> getMediumDifficultyMove()
            Difficulty.HARD -> getBestMove()
        }
    }
    
    // Easy difficulty - random move
    private fun getRandomMove(): Pair<Int, Int> {
        val availableMoves = getAvailableMoves()
        return availableMoves.random()
    }
    
    // Medium difficulty - some strategy
    private fun getMediumDifficultyMove(): Pair<Int, Int> {
        // 70% chance to make a strategic move
        if (Math.random() < 0.7) {
            return getBestMove()
        } else {
            return getRandomMove()
        }
    }
    
    // Hard difficulty - optimal play using Minimax algorithm
    private fun getBestMove(): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var bestMove = Pair(0, 0)
        
        for (i in 0..2) {
            for (j in 0..2) {
                if (gameBoard[i][j] == "") {
                    // Make move
                    gameBoard[i][j] = "O"
                    
                    // Calculate score using minimax
                    val score = minimax(0, false, Int.MIN_VALUE, Int.MAX_VALUE)
                    
                    // Undo move
                    gameBoard[i][j] = ""
                    
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = Pair(i, j)
                    }
                }
            }
        }
        
        return bestMove
    }
    
    // Minimax algorithm with alpha-beta pruning
    private fun minimax(depth: Int, isMaximizing: Boolean, alpha: Int, beta: Int): Int {
        val winner = checkWinner()
        
        // Base cases
        when {
            winner == "O" -> return 10 - depth  // Computer wins
            winner == "X" -> return depth - 10  // Human wins
            getAvailableMoves().isEmpty() -> return 0  // Draw
        }
        
        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (move in getAvailableMoves()) {
                gameBoard[move.first][move.second] = "O"
                val eval = minimax(depth + 1, false, alpha, beta)
                gameBoard[move.first][move.second] = ""
                
                maxEval = maxOf(maxEval, eval)
                alpha = maxOf(alpha, eval)
                if (beta <= alpha) break  // Alpha-beta pruning
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in getAvailableMoves()) {
                gameBoard[move.first][move.second] = "X"
                val eval = minimax(depth + 1, true, alpha, beta)
                gameBoard[move.first][move.second] = ""
                
                minEval = minOf(minEval, eval)
                beta = minOf(beta, eval)
                if (beta <= alpha) break  // Alpha-beta pruning
            }
            return minEval
        }
    }
    
    // Get all available moves
    private fun getAvailableMoves(): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (gameBoard[i][j] == "") {
                    moves.add(Pair(i, j))
                }
            }
        }
        return moves
    }
    
    // Check for winner
    private fun checkWinner(): String? {
        // Check rows
        for (i in 0..2) {
            if (gameBoard[i][0] == gameBoard[i][1] && 
                gameBoard[i][1] == gameBoard[i][2] && 
                gameBoard[i][0].isNotEmpty()) {
                return gameBoard[i][0]
            }
        }
        
        // Check columns
        for (j in 0..2) {
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
    
    // Update game state
    private fun updateGameState() {
        val winner = checkWinner()
        when (winner) {
            "X" -> gameState = GameState.WON_X
            "O" -> gameState = GameState.WON_O
            null -> {
                // Check if board is full (draw)
                if (getAvailableMoves().isEmpty()) {
                    gameState = GameState.DRAW
                } else {
                    gameState = GameState.IN_PROGRESS
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                }
            }
        }
    }
    
    // Reset the game board
    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                gameBoard[i][j] = ""
            }
        }
    }
    
    // Get current board state
    fun getBoard(): Array<Array<String>> {
        return gameBoard.map { it.copyOf() }.toTypedArray()
    }
    
    // Get current game state
    fun getGameState(): GameState {
        return gameState
    }
    
    // Get current player
    fun getCurrentPlayer(): String {
        return currentPlayer
    }
    
    // Check if game is over
    fun isGameOver(): Boolean {
        return gameState != GameState.IN_PROGRESS
    }
    
    // Get game winner
    fun getWinner(): String? {
        return when (gameState) {
            GameState.WON_X -> "X"
            GameState.WON_O -> "O"
            else -> null
        }
    }
    
    // Get game statistics
    fun getStatistics(): GameStatistics {
        return GameStatistics(totalGames, playerWins, computerWins, draws)
    }
    
    // Update statistics after game ends - استخدام var بشكل آمن
    fun updateStatistics() {
        totalGames = totalGames + 1
        when (gameState) {
            GameState.WON_X -> playerWins = playerWins + 1
            GameState.WON_O -> computerWins = computerWins + 1
            GameState.DRAW -> draws = draws + 1
            else -> {} // Game not over yet
        }
    }
    
    // Save statistics to SharedPreferences
    fun saveStatistics(preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putInt("total_games", totalGames)
        editor.putInt("player_wins", playerWins)
        editor.putInt("computer_wins", computerWins)
        editor.putInt("draws", draws)
        editor.apply()
    }
    
    // Load statistics from SharedPreferences
    fun loadStatistics(preferences: SharedPreferences) {
        totalGames = preferences.getInt("total_games", 0)
        playerWins = preferences.getInt("player_wins", 0)
        computerWins = preferences.getInt("computer_wins", 0)
        draws = preferences.getInt("draws", 0)
    }
    
    // Reset all statistics
    fun resetStatistics() {
        totalGames = 0
        playerWins = 0
        computerWins = 0
        draws = 0
    }
    
    // Get available winning combinations
    fun getWinningCombinations(): List<List<Pair<Int, Int>>> {
        return listOf(
            listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)), // First row
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2)), // Second row
            listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2)), // Third row
            listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)), // First column
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1)), // Second column
            listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2)), // Third column
            listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)), // Main diagonal
            listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))  // Anti-diagonal
        )
    }
    
    // Check if a winning combination exists
    fun checkWinningCombination(): List<Pair<Int, Int>>? {
        val combinations = getWinningCombinations()
        
        for (combination in combinations) {
            val symbols = combination.map { gameBoard[it.first][it.second] }
            if (symbols.all { it == "X" } || symbols.all { it == "O" }) {
                return combination
            }
        }
        
        return null
    }
}

// Game statistics data class
data class GameStatistics(
    val totalGames: Int,
    val playerWins: Int,
    val computerWins: Int,
    val draws: Int
) {
    fun getWinRate(): Float {
        return if (totalGames > 0) {
            (playerWins.toFloat() / totalGames) * 100
        } else {
            0f
        }
    }
    
    fun getComputerWinRate(): Float {
        return if (totalGames > 0) {
            (computerWins.toFloat() / totalGames) * 100
        } else {
            0f
        }
    }
    
    fun getDrawRate(): Float {
        return if (totalGames > 0) {
            (draws.toFloat() / totalGames) * 100
        } else {
            0f
        }
    }
}