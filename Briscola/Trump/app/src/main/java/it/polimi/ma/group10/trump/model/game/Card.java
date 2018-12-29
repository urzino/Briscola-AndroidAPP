package it.polimi.ma.group10.trump.model.game;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.enumerations.CardValue;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;

/**
 * This class represent a single card inside the game, every card must be unique
 * and it's represented by its value and its suit.
 * It's allowed to obtain the String version of the card and the comparison between cards.
 */
public class Card {

    /**
     * The value of the card
     */
    private CardValue value;

    /**
     * The suit of the card
     */
    private CardSuit suit;

    /**
     * Yhe card constructor
     * @param value the card's value
     * @param suit  the card's suit
     */
    public Card(CardValue value, CardSuit suit){
        this.value = value;
        this.suit = suit;
    }

    /**
     * The card constructor from String
     * @param string the String describing the card to be created
     * @throws CardException if the card format is invalid
     */
    public Card(String string) throws CardException{
        if(string.length() > 2 ){throw new CardException(CardErrorCode.INVALID_CARD_LEN)
                .set("message","ERROR: Card string length too long");}
        value = CardValue.valueOf(string.charAt(0));
        suit = CardSuit.valueOf(string.charAt(1));
    }

    /**
     * The card value getter method
     * @return the card value
     */
    public CardValue getValue() {
        return value;
    }

    /**
     * The card suit getter method
     *
     * @return the card suit
     */
    public CardSuit getSuit() {
        return suit;
    }

    /**
     * This method is charged to convert the current card into a String
     *
     * @return the String version of the card
     */
    public String toString(){
        char temp[] = {this.value.getSymbol(), this.suit.getSymbol()};
        return new String(temp);
    }

    @Override
    public int hashCode() {
        return value.hashCode() + suit.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null)return false;
        if(this == obj)return true;
        if(!(obj instanceof Card))return false;

        Card card = (Card) obj;

        if(this.value!=card.value)return false;
        if(this.suit != card.suit) return false;

        return true;

    }
}
