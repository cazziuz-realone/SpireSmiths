package com.spiresmiths.tcg.domain.entities

import kotlinx.serialization.Serializable

/**
 * Represents a player in the SpireSmiths TCG.
 */
@Serializable
data class Player(
    val id: String,
    val name: String,
    val isAI: Boolean = false,
    val difficulty: AIDifficulty = AIDifficulty.MEDIUM
) {
    /**
     * Determines if this is a human player.
     */
    val isHuman: Boolean
        get() = !isAI
}

/**
 * AI difficulty levels.
 */
@Serializable
enum class AIDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert")
}

/**
 * Represents a player's state during a game.
 */
@Serializable
data class PlayerState(
    val playerId: String,
    val health: Int,
    val maxHealth: Int = 30,
    val mana: Int,
    val maxMana: Int,
    val hand: List<Card> = emptyList(),
    val board: List<Creature> = emptyList(),
    val weapon: Weapon? = null,
    val heroPower: HeroPower? = null,
    val fatigueDamage: Int = 0
) {
    /**
     * Determines if the player is alive.
     */
    val isAlive: Boolean
        get() = health > 0
    
    /**
     * Determines if the player is dead.
     */
    val isDead: Boolean
        get() = health <= 0
    
    /**
     * Gets the number of cards in hand.
     */
    val handSize: Int
        get() = hand.size
    
    /**
     * Gets the number of creatures on board.
     */
    val boardSize: Int
        get() = board.size
    
    /**
     * Determines if the player's hand is full (10 cards).
     */
    val isHandFull: Boolean
        get() = handSize >= 10
    
    /**
     * Determines if the player's board is full (7 creatures).
     */
    val isBoardFull: Boolean
        get() = boardSize >= 7
    
    /**
     * Determines if the player can play any card from their hand.
     */
    fun canPlayAnyCard(): Boolean {
        return hand.any { it.cost <= mana }
    }
    
    /**
     * Gets all cards that can be played with current mana.
     */
    fun getPlayableCards(): List<Card> {
        return hand.filter { it.cost <= mana }
    }
}

/**
 * Represents a creature on the board.
 */
@Serializable
data class Creature(
    val cardId: String,
    val instanceId: String,
    val attack: Int,
    val health: Int,
    val maxHealth: Int,
    val canAttack: Boolean = false,
    val hasAttacked: Boolean = false,
    val keywords: List<Keyword> = emptyList(),
    val temporaryEffects: List<TemporaryEffect> = emptyList()
) {
    /**
     * Determines if the creature is alive.
     */
    val isAlive: Boolean
        get() = health > 0
    
    /**
     * Determines if the creature is damaged.
     */
    val isDamaged: Boolean
        get() = health < maxHealth
    
    /**
     * Determines if the creature can attack this turn.
     */
    val canAttackThisTurn: Boolean
        get() = canAttack && !hasAttacked
    
    /**
     * Determines if the creature has a specific keyword.
     */
    fun hasKeyword(keyword: Keyword): Boolean {
        return keywords.contains(keyword)
    }
}

/**
 * Represents a weapon equipped by a player.
 */
@Serializable
data class Weapon(
    val cardId: String,
    val attack: Int,
    val durability: Int,
    val maxDurability: Int
) {
    /**
     * Determines if the weapon is broken.
     */
    val isBroken: Boolean
        get() = durability <= 0
}

/**
 * Represents a hero power.
 */
@Serializable
data class HeroPower(
    val id: String,
    val name: String,
    val cost: Int,
    val description: String,
    val canUse: Boolean = true,
    val usedThisTurn: Boolean = false
)

/**
 * Represents a temporary effect on a creature.
 */
@Serializable
data class TemporaryEffect(
    val type: EffectType,
    val value: Int,
    val duration: Int // Number of turns remaining
)