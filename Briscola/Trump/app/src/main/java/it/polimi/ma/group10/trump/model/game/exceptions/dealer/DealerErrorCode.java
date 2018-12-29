package it.polimi.ma.group10.trump.model.game.exceptions.dealer;

/**
 * List all the errors related to dealer actions
 * @see DealerException
 */
public enum DealerErrorCode {
    /**
     * card trying to be dealt in a wrong moment of the game
     */
    ILLEGAL_CARD_DISTRIBUTION(300),
    /**
     * winner can't be delcared if a card is missing on the table
     */
    MISSING_CARD_ON_TABLE(301),
    /**
     * trying to conclude the match if it's not over yet
     */
    MATCH_NOT_OVER(302),
    /**
     * final point sum doesn't match 120
     */
    MATCH_FINAL_POINTS_NOT_COHERENT(303);

    private final int number;

    private DealerErrorCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
