package it.polimi.ma.group10.trump.model.game.exceptions.deck;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class defining exception thrown by the
 * creation/usage of decks
 * @see DeckErrorCode
 */
public class DeckException extends Exception {

    /**
     * errorCode associated to the exception
     */
    private DeckErrorCode errorCode;

    /**
     *Map containing properties associated to the error
     */
    private final Map<String,Object> properties = new TreeMap<String,Object>();

    /**
     * Build an excpetion starting from the error code
     * @param errorCode
     */
    public DeckException(DeckErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DeckException(String message, DeckErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DeckErrorCode getErrorCode() {
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
    public DeckException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
}
