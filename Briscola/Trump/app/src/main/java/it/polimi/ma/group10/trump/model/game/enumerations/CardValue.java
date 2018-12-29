package it.polimi.ma.group10.trump.model.game.enumerations;


import it.polimi.ma.group10.trump.model.game.exceptions.card.CardErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;

/**
 * Enumeration for all the possible values associated to cards
 */
public enum CardValue {
    ACE('1',11,1),
    TWO('2',0,10),
    THREE('3',10,2),
    FOUR('4',0,9),
    FIVE('5',0,8),
    SIX('6',0,7),
    SEVEN('7',0,6),
    JACK('J',2,5),
    HORSE('H',3,4),
    KING('K',4,3),;


    private char symbol;
    /**
     * Points associated to the card
     */
    private int value;

    private int rank;

    CardValue(char symbol, int value, int rank){
        this.symbol = symbol;
        this.value = value;
        this.rank = rank;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getValue() {
        return value;
    }

    public int getRank() {
        return rank;
    }


    /**
     * Return the correct enumeration given the associated symbol
     * @param symbol value symbol (1-9,J,H,K)
     * @return CardValue enum associated to the symbol
     * @throws CardException if the passed argument does not match any of the possible
     * symbols
     */
    public static CardValue valueOf(char symbol) throws CardException {
        switch (symbol) {
            case '1': {return CardValue.ACE;}
            case '2': {return CardValue.TWO;}
            case '3': {return CardValue.THREE;}
            case '4': {return CardValue.FOUR;}
            case '5': {return CardValue.FIVE;}
            case '6': {return CardValue.SIX;}
            case '7': {return CardValue.SEVEN;}
            case 'J': {return CardValue.JACK;}
            case 'H': {return CardValue.HORSE;}
            case 'K': {return CardValue.KING;}
            default:{ throw new CardException(CardErrorCode.INVALID_CHAR_VALUE).set("message","ERROR: " + symbol + " is an illegal value character");}
        }
    }
}
