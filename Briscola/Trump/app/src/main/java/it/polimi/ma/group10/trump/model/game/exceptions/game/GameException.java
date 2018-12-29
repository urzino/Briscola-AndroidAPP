package it.polimi.ma.group10.trump.model.game.exceptions.game;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class defining exception thrown during the evolution
 * of the game
 * @see GameErrorCode
 */
public class GameException extends Exception {

    /**
     * errorCode associated to the exception
     */
    private GameErrorCode errorCode;

    /**
     *Map containing properties associated to the error
     */
    private final Map<String,Object> properties = new TreeMap<String,Object>();



    /**
     * Build an excpetion starting from the error code
     * @param errorCode
     */
    public GameException(GameErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public GameException(String message, GameErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public GameErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Add a parameter for dynamic exception handling
     * @param name Key
     * @param value Object
     * @return The exception itself
     */
    public GameException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
}
