package com.spiresmiths.tcg.domain.entities

import kotlinx.serialization.Serializable

/**
 * Represents a card in the SpireSmiths TCG.
 * This is the core domain entity for all card types.
 */
@Serializable
data class Card(
    val id: String,
    val name: String,
    val cost: Int,
    val type: CardType,
    val rarity: Rarity,
    val description: String,
    val imageUrl: String? = null,
    
    // Combat stats (for creatures and weapons)
    val attack: Int? = null,
    val health: Int? = null,
    val durability: Int? = null, // For weapons
    
    // Card abilities and effects
    val abilities: List<CardAbility> = emptyList(),
    val keywords: List<Keyword> = emptyList(),
    
    // Card set information
    val setId: String = "core",
    val collectible: Boolean = true
) {
    /**
     * Determines if this card is a creature that can be placed on the board.
     */
    val isCreature: Boolean
        get() = type == CardType.CREATURE
    
    /**
     * Determines if this card is a spell that has immediate effects.
     */
    val isSpell: Boolean
        get() = type == CardType.SPELL
    
    /**
     * Determines if this card is a weapon that can be equipped.
     */
    val isWeapon: Boolean
        get() = type == CardType.WEAPON
    
    /**
     * Gets the total stats value for creatures (attack + health).
     */
    val totalStats: Int
        get() = (attack ?: 0) + (health ?: 0)
    
    /**
     * Validates that the card has valid stats for its type.
     */
    fun isValid(): Boolean {
        return when (type) {
            CardType.CREATURE -> attack != null && health != null && attack >= 0 && health > 0
            CardType.SPELL -> attack == null && health == null && durability == null
            CardType.WEAPON -> attack != null && durability != null && attack > 0 && durability > 0 && health == null
        } && cost >= 0 && name.isNotBlank()
    }
}

/**
 * Enumeration of card types in SpireSmiths.
 */
@Serializable
enum class CardType {
    CREATURE,   // Creatures that stay on the board
    SPELL,      // Single-use spells with immediate effects
    WEAPON      // Weapons that can be equipped by the player
}

/**
 * Enumeration of card rarities.
 */
@Serializable
enum class Rarity(val displayName: String) {
    COMMON("Common"),
    RARE("Rare"),
    EPIC("Epic"),
    LEGENDARY("Legendary")
}

/**
 * Represents a card ability or effect.
 */
@Serializable
data class CardAbility(
    val id: String,
    val name: String,
    val description: String,
    val trigger: AbilityTrigger,
    val effects: List<Effect> = emptyList()
)

/**
 * When a card ability triggers.
 */
@Serializable
enum class AbilityTrigger {
    BATTLECRY,      // When played from hand
    DEATHRATTLE,    // When destroyed
    START_OF_TURN,  // At the start of turn
    END_OF_TURN,    // At the end of turn
    ON_ATTACK,      // When attacking
    ON_DAMAGE,      // When taking damage
    PASSIVE         // Always active
}

/**
 * Card keywords that provide special abilities.
 */
@Serializable
enum class Keyword(val displayName: String, val description: String) {
    TAUNT("Taunt", "Enemies must attack this creature first"),
    CHARGE("Charge", "Can attack immediately"),
    DIVINE_SHIELD("Divine Shield", "Prevents the first damage taken"),
    STEALTH("Stealth", "Cannot be attacked until this creature attacks"),
    LIFESTEAL("Lifesteal", "Damage dealt heals your hero"),
    WINDFURY("Windfury", "Can attack twice per turn"),
    SPELL_DAMAGE("Spell Damage", "Increases spell damage"),
    POISONOUS("Poisonous", "Destroys any creature damaged by this")
}

/**
 * Represents an effect that can be applied.
 */
@Serializable
data class Effect(
    val type: EffectType,
    val value: Int = 0,
    val target: TargetType = TargetType.NONE,
    val condition: String? = null
)

/**
 * Types of effects that can be applied.
 */
@Serializable
enum class EffectType {
    DAMAGE,
    HEAL,
    DRAW_CARD,
    GAIN_MANA,
    SUMMON_CREATURE,
    DESTROY,
    SILENCE,
    BUFF_ATTACK,
    BUFF_HEALTH,
    GIVE_KEYWORD
}

/**
 * Types of targets for effects.
 */
@Serializable
enum class TargetType {
    NONE,
    SELF,
    ENEMY_HERO,
    FRIENDLY_HERO,
    ANY_CREATURE,
    FRIENDLY_CREATURE,
    ENEMY_CREATURE,
    ALL_CREATURES,
    RANDOM_ENEMY
}