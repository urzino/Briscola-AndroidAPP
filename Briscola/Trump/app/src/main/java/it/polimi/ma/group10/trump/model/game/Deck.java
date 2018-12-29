package it.polimi.ma.group10.trump.model.game;

import java.util.ArrayList;
import java.util.Collections;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.enumerations.CardValue;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckException;

/**
 * This class represent the deck of a match;
 * the deck can be shuffled;
 * it's possible to draw a card from it or draw it in a trump manner, placing the drawn card on its bottom;
 * it's possible to obtain the String version of the deck.
 */
public class Deck {

    /**
     * This parameter contains the cards composing the deck
     * in a given moment of the game
     */
    private ArrayList<Card> cards;

    /**
     * This is the constructor that creates the deck, initializing its cards according to the deck composition
     * of the game
     */
    public Deck(){
        this.cards = new ArrayList<Card>();
        for (CardSuit suit: CardSuit.values()) { //for every possible suit
            for(CardValue value: CardValue.values()){ //for every possible value
                cards.add(new Card(value,suit)); //add a new card to the deck
            }
        }
    }

    /**
     * This is the constructor that creates the deck starting from a string
     * @param deck the String describing the deck to be created
     * @throws CardException if a card inside the deck description is not a possible card
     */
    public Deck(String deck) throws CardException, DeckException{
        this.cards = new ArrayList<Card>();

        if(deck.length() % 2 != 0 || deck.length()>80) {throw  new DeckException(DeckErrorCode.INVALID_DECK)
                .set("message", "ERROR: String of the deck invalid");}

        for(int i=0;i<deck.length();i=i+2){  //for every card in the string
            Card card = new Card(deck.substring(i,i+2)); //create the related object
            this.cards.add(card); //and adds it to the deck
        }
    }

    /**
     *
     * @return The list of the cards composing the Deck
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * This method is charged to shuffle the Deck before the Card distribution
     */
    public void shuffle(){
        Collections.shuffle(this.cards);
    }

    /**
     * This method is charged to remove (draw, in game terms) the Card on the top of the Deck
     * @return the removed cards
     * @throws DeckException if there are no Cards to be removed
     */
    public Card drawCard() throws DeckException {
        if(this.cards.size() == 0) {
            throw  new DeckException(DeckErrorCode.EMPTY_DECK).set("message", "ERROR: there are no cards inside the deck");
        }
        return this.cards.remove(0);
    }

    /**
     * This method is charged to remove a Card from the deck when it's a trump
     * and puts it on the bottom of the Deck
     *
     * @return the trump Card
     * @throws DeckException if it's the wrong moment to draw the trump (which should be the 7th,
     * when there are 34 cards in the deck)
     */
    public Card drawTrump() throws DeckException {
        if(this.cards.size() != 34) {
            throw  new DeckException(DeckErrorCode.INVALID_CARD_DRAW_ATTEMPT)
                .set("message","ERROR: attempt to draw the trump in a wrong moment of the game");
        }
        Card trump = this.cards.remove(0);
        this.cards.add(trump);
        return trump;
    }

    /**
     * This method converts to String the current Deck
     * @return the String version of the Deck
     */
    public String toString(){
        String deckString = "";
        for (int i = 0; i < this.cards.size() ; i++){
            deckString += this.cards.get(i).toString();
        }
        return deckString;
    }


}
