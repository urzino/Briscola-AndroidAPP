package it.polimi.ma.group10.trump.model.game.exceptions.game;

/**
 * List all the possible error related to game evolution,
 * configuration and states
 * @see GameException
 */
public enum GameErrorCode {
    /**
     * configuration string too long
     */
    ILLEGAL_CONFIGURATION_LEN(400),
    /**
     * configuration format invalid
     */
    INVALID_CONFIGURATION_FORMAT(401),
    /**
     * sum of cards doesn't match 40
     */
    INVALID_SUM_OF_CARDS(402),
    /**
     * mismatch between 2nd character of the configuration and the trump
     * set as last card
     */
    LAST_CARD_TRUMP_MISMATCH(403),
    /**
     * character of the move is invalid (0,1,2 allowed)
     */
    INVALID_MOVES_CHAR(404),
    /**
     * invalid number of move with respect to the given configuration
     */
    INVALID_NUMBER_OF_MOVES(405),
    /**
     * game is in an invalid state
     */
    INVALID_GAME_STATE(406);

    private final int number;

    GameErrorCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
