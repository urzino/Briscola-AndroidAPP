package it.polimi.ma.group10.trump.model.game.exceptions.card;


/**
 * List all the errors related to cards
 * @see CardException
 */
public enum CardErrorCode {
    /**
     * invalid character for a suit
     */
    INVALID_CHAR_SUIT(100),
    /**
     * invalid character for a value
     */
    INVALID_CHAR_VALUE(101),
    /**
     * invalid length of the strinng representation of a card
     */
    INVALID_CARD_LEN(102);


    private final int number;

    CardErrorCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
