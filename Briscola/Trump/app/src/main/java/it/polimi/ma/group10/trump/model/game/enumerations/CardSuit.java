package it.polimi.ma.group10.trump.model.game.enumerations;

import it.polimi.ma.group10.trump.model.game.exceptions.card.CardErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;

/**
 * Enumeration for all the possible suit associated to cards
 */
public enum CardSuit {
    BATONS ('B') ,
    SWORDS('S'),
    GOLDS('G'),
    CUPS('C');

    private char symbol;

    CardSuit(char symbol) {
        this.symbol = symbol;
    }


    public char getSymbol() {
        return symbol;
    }

    /**
     * Return the correct enumeration given the associated symbol
     * @param symbol value symbol (B,S,G,C)
     * @return CardSuit
     * @throws CardException
     */
    public static CardSuit valueOf(char symbol) throws CardException {
        switch (symbol) {
            case 'B': {return CardSuit.BATONS;}
            case 'S': {return CardSuit.SWORDS;}
            case 'G': {return CardSuit.GOLDS;}
            case 'C': {return CardSuit.CUPS;}
            default: throw  new CardException(CardErrorCode.INVALID_CHAR_SUIT).set("message","ERROR: " + symbol + " is an illegal suit character");
        }
    }
}
