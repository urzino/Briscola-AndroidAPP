package it.polimi.ma.group10.trump.model.game.enumerations;

/**
 * Define the possibles state of the game
 */
public enum GameState {

    /**
     * one of the player should play
     */
    PLAY,
    /**
     * round winner has been declared
     */
    ROUND_WINNER_DECLARED,
    /**
     * both the player have played their card
     */
    ROUND_OVER,
    /**
     * match is complete
     */
    MATCH_OVER,
    /**
     * invalid state of the game caused by an error
     */
    INVALID_STATE
}
