package it.polimi.ma.group10.trump.model.game.exceptions.card;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class defining exception thrown by the
 * creation/usage of cards
 * @see CardErrorCode
 */
public class CardException extends Exception  {

    /**
     * errorCode associated to the exception
     */
    private CardErrorCode errorCode;

    /**
     *Map containing properties associated to the error
     */
    private final Map<String,Object> properties = new TreeMap<String,Object>();

    /**
     * Build an excpetion starting from the error code
     * @param errorCode
     */
    public CardException(CardErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CardException(String message, CardErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public CardErrorCode getErrorCode() {
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
    public CardException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
}
