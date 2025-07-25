package com.spiresmiths.tcg.domain.entities

import kotlinx.serialization.Serializable

/**
 * Represents a deck of cards in SpireSmiths TCG.
 */
@Serializable
data class Deck(
    val id: String,
    val name: String,
    val cards: List<DeckCard>,
    val heroClass: HeroClass = HeroClass.NEUTRAL,
    val isValid: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Gets the total number of cards in the deck.
     */
    val totalCards: Int
        get() = cards.sumOf { it.count }
    
    /**
     * Gets the total mana cost of all cards in the deck.
     */
    val totalManaCost: Int
        get() = cards.sumOf { it.card.cost * it.count }
    
    /**
     * Gets the average mana cost of cards in the deck.
     */
    val averageManaCost: Double
        get() = if (totalCards > 0) totalManaCost.toDouble() / totalCards else 0.0
    
    /**
     * Validates the deck according to game rules.
     */
    fun validate(): DeckValidation {
        val errors = mutableListOf<String>()
        
        // Check deck size (must be exactly 30 cards)
        if (totalCards != 30) {
            errors.add("Deck must contain exactly 30 cards (currently has $totalCards)")
        }
        
        // Check card limits (max 2 of any card, 1 for legendaries)
        cards.forEach { deckCard ->
            val maxAllowed = if (deckCard.card.rarity == Rarity.LEGENDARY) 1 else 2
            if (deckCard.count > maxAllowed) {
                errors.add("Too many copies of ${deckCard.card.name} (${deckCard.count}/$maxAllowed)")
            }
        }
        
        // Check for duplicate cards
        val cardIds = cards.map { it.card.id }
        val duplicates = cardIds.groupBy { it }.filter { it.value.size > 1 }
        if (duplicates.isNotEmpty()) {
            errors.add("Duplicate cards found: ${duplicates.keys.joinToString(", ")}")
        }
        
        return DeckValidation(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    /**
     * Gets cards grouped by mana cost.
     */
    fun getCardsByManaCost(): Map<Int, List<DeckCard>> {
        return cards.groupBy { it.card.cost }
    }
    
    /**
     * Gets cards grouped by type.
     */
    fun getCardsByType(): Map<CardType, List<DeckCard>> {
        return cards.groupBy { it.card.type }
    }
    
    /**
     * Gets the mana curve of the deck.
     */
    fun getManaCurve(): List<Int> {
        val curve = IntArray(11) // 0-10+ mana
        cards.forEach { deckCard ->
            val cost = minOf(deckCard.card.cost, 10)
            curve[cost] += deckCard.count
        }
        return curve.toList()
    }
    
    /**
     * Shuffles the deck and returns a new list of cards.
     */
    fun shuffle(): List<Card> {
        val allCards = mutableListOf<Card>()
        cards.forEach { deckCard ->
            repeat(deckCard.count) {
                allCards.add(deckCard.card)
            }
        }
        return allCards.shuffled()
    }
}

/**
 * Represents a card in a deck with its count.
 */
@Serializable
data class DeckCard(
    val card: Card,
    val count: Int
) {
    init {
        require(count > 0) { "Card count must be positive" }
        require(count <= 2 || (count == 1 && card.rarity == Rarity.LEGENDARY)) {
            "Invalid card count for ${card.name}: $count"
        }
    }
}

/**
 * Represents the validation result of a deck.
 */
data class DeckValidation(
    val isValid: Boolean,
    val errors: List<String>
)

/**
 * Hero classes in SpireSmiths TCG.
 */
@Serializable
enum class HeroClass(val displayName: String) {
    NEUTRAL("Neutral"),
    WARRIOR("Warrior"),
    MAGE("Mage"),
    PRIEST("Priest"),
    ROGUE("Rogue"),
    HUNTER("Hunter"),
    WARLOCK("Warlock"),
    SHAMAN("Shaman"),
    PALADIN("Paladin"),
    DRUID("Druid")
}

/**
 * Predefined starter decks.
 */
object StarterDecks {
    /**
     * Creates a basic starter deck for new players.
     */
    fun createBasicDeck(): Deck {
        // This will be populated with actual card data later
        return Deck(
            id = "starter_basic",
            name = "Basic Starter",
            cards = emptyList(), // Will be populated with starter cards
            heroClass = HeroClass.NEUTRAL
        )
    }
    
    /**
     * Creates an AI deck for computer opponents.
     */
    fun createAIDeck(difficulty: AIDifficulty): Deck {
        return Deck(
            id = "ai_${difficulty.name.lowercase()}",
            name = "AI ${difficulty.displayName}",
            cards = emptyList(), // Will be populated based on difficulty
            heroClass = HeroClass.NEUTRAL
        )
    }
}