package com.spiresmiths.tcg.domain.entities

import kotlinx.serialization.Serializable

/**
 * Represents a game of SpireSmiths TCG.
 */
@Serializable
data class Game(
    val id: String,
    val players: List<Player>,
    val currentState: GameState,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Gets the current player whose turn it is.
     */
    val currentPlayer: Player
        get() = players[currentState.currentPlayerIndex]
    
    /**
     * Gets the opponent of the current player.
     */
    val opponentPlayer: Player
        get() = players[1 - currentState.currentPlayerIndex]
    
    /**
     * Determines if the game is over.
     */
    val isGameOver: Boolean
        get() = currentState.phase == GamePhase.GAME_OVER
    
    /**
     * Gets the winner of the game, if any.
     */
    val winner: Player?
        get() = if (isGameOver && currentState.winnerId != null) {
            players.find { it.id == currentState.winnerId }
        } else null
}

/**
 * Represents the current state of a game.
 */
@Serializable
data class GameState(
    val gameId: String,
    val turn: Int,
    val currentPlayerIndex: Int,
    val phase: GamePhase,
    val players: List<PlayerState>,
    val history: List<GameEvent> = emptyList(),
    val winnerId: String? = null,
    val winCondition: WinCondition? = null
) {
    /**
     * Gets the current player's state.
     */
    val currentPlayerState: PlayerState
        get() = players[currentPlayerIndex]
    
    /**
     * Gets the opponent player's state.
     */
    val opponentPlayerState: PlayerState
        get() = players[1 - currentPlayerIndex]
    
    /**
     * Gets the last game event.
     */
    val lastEvent: GameEvent?
        get() = history.lastOrNull()
}

/**
 * Represents the different phases of a game turn.
 */
@Serializable
enum class GamePhase {
    MULLIGAN,       // Initial card selection phase
    START_TURN,     // Beginning of turn effects
    MAIN,           // Main phase where players can take actions
    END_TURN,       // End of turn effects
    GAME_OVER       // Game has ended
}

/**
 * Represents different ways a game can be won.
 */
@Serializable
enum class WinCondition {
    OPPONENT_DEFEATED,  // Opponent's health reached 0
    OPPONENT_FATIGUE,   // Opponent died from fatigue damage
    SPECIAL_CONDITION,  // Special win condition from card effects
    CONCEDE,           // Opponent conceded
    TIMEOUT            // Opponent ran out of time
}

/**
 * Represents an event that occurred during the game.
 */
@Serializable
sealed class GameEvent {
    abstract val playerId: String
    abstract val timestamp: Long
    
    @Serializable
    data class GameStarted(
        override val playerId: String,
        val player1Id: String,
        val player2Id: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class TurnStarted(
        override val playerId: String,
        val turnNumber: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class CardDrawn(
        override val playerId: String,
        val cardId: String,
        val fatigueDamage: Int = 0,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class CardPlayed(
        override val playerId: String,
        val cardId: String,
        val targetId: String? = null,
        val manaCost: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class CreatureAttacked(
        override val playerId: String,
        val attackerId: String,
        val defenderId: String?, // null for face attacks
        val damage: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class CreatureDied(
        override val playerId: String,
        val creatureId: String,
        val cardId: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class PlayerDamaged(
        override val playerId: String,
        val damage: Int,
        val source: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class PlayerHealed(
        override val playerId: String,
        val healing: Int,
        val source: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class TurnEnded(
        override val playerId: String,
        val turnNumber: Int,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
    
    @Serializable
    data class GameEnded(
        override val playerId: String,
        val winnerId: String,
        val winCondition: WinCondition,
        override val timestamp: Long = System.currentTimeMillis()
    ) : GameEvent()
}

/**
 * Represents the result of a game action.
 */
@Serializable
data class GameResult(
    val success: Boolean,
    val newState: GameState?,
    val events: List<GameEvent> = emptyList(),
    val error: String? = null
) {
    companion object {
        fun success(newState: GameState, events: List<GameEvent> = emptyList()): GameResult {
            return GameResult(true, newState, events)
        }
        
        fun failure(error: String): GameResult {
            return GameResult(false, null, emptyList(), error)
        }
    }
}