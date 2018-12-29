package it.polimi.ma.group10.trump.model.game.exceptions.deck;

/**
 * List of all the errors related to deck usage
 * @see DeckException
 */
public enum DeckErrorCode {
    /**
     * action forbidden due to an empty deck
     */
    EMPTY_DECK(200),
    /**
     * deck creation invalid
     */
    INVALID_DECK(201),
    /**
     * trump drawn in the wrong moment of the game
     */
    INVALID_CARD_DRAW_ATTEMPT(202);

    private final int number;

    DeckErrorCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
