package it.polimi.ma.group10.trump.model.game.exceptions.player;

/**
 * List all the possible error related to action performed
 * by the player
 * @see PlayerException
 */
public enum PlayerErrorCode {
    /**
     * try to use a card index wich is not in his hand
     */
    CARD_NOT_IN_HAND(500),
    /**
     * action forbidden beacause is not player turn
     */
    NOT_PLAYER_TURN(501),
    /**
     * too much character (>6) for the player hand
     */
    INVALID_PLAYER_HAND(502),
    /**
     * pile char should be multiple of 4
     */
    INVALID_PLAYER_PILE(503),
    /**
     * pile charshould be less then 80
     */
    INVALID_PLAYER_PILE_SIZE(504),
    /**
     *invalid move trying to be performed
     */
    INVALID_PLAYER_MOVE(505);

    private final int number;

    PlayerErrorCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
