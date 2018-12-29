package it.polimi.ma.group10.trump.model.game.exceptions.player;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class defining exception thrown by action performed by the player
 * @see PlayerErrorCode
 */
public class PlayerException extends Exception {

    /**
     * errorCode associated to the exception
     */
    private PlayerErrorCode errorCode;

    /**
     *Map containing properties associated to the error
     */
    private final Map<String,Object> properties = new TreeMap<String,Object>();

    /**
     * Build an excpetion starting from the error code
     * @param errorCode
     */
    public PlayerException(PlayerErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public PlayerException(String message, PlayerErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PlayerErrorCode getErrorCode() {
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
    public PlayerException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }

}
