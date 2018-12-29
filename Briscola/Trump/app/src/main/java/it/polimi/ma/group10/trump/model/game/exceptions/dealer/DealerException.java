package it.polimi.ma.group10.trump.model.game.exceptions.dealer;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class defining exception thrown by the
 * action performed by the dealer
 * @see DealerErrorCode
 */
public class DealerException extends Exception{

    /**
     * errorCode associated to the exception
     */
    private DealerErrorCode errorCode;

    /**
     *Map containing properties associated to the error
     */
    private final Map<String,Object> properties = new TreeMap<String,Object>();

    /**
     * Build an excpetion starting from the error code
     * @param errorCode
     */
    public DealerException(DealerErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DealerException(String message, DealerErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DealerErrorCode getErrorCode() {
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
    public DealerException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }
}
