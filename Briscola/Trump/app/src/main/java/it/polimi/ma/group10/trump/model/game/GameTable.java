package it.polimi.ma.group10.trump.model.game;


import java.util.ArrayList;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;

/**
 * The game table is the place where the match is made;
 * it contains the the deck;
 * players can play their cards on it;
 * the match trump is saved on it;
 * it keep track of the match turns.
 *
 */
public class GameTable {

    /**
     * The Singleton instance
     */
    private static final GameTable ourInstance = new GameTable();

    /**
     * The game deck present on the table containing the cards that the Dealer will distribute
     */
    private Deck deck;

    /**
     * The suit of the trupm of the current match
     */
    private CardSuit trump;

    private Card trumpCard;

    /**
     * The list of cards played by Players currently on the table
     */
    private ArrayList<Card> cardsOnTable;

    /**
     * The first Player who must play / played in the current round
     */
    private int firstPlayerCurrentRound;

    /**
     * The next player that have tu perform his move in the current round
     */
    private int currentPlayer;

    /**
     * This method is charged to return the only instance inside the game of the GameTable,
     * following the Singleton design pattern
     * @return the GameTable instance
     */
    public static GameTable getInstance() {
        return ourInstance;
    }

    /**
     * The constructor
     */
    private GameTable() {
        cardsOnTable = new ArrayList<Card>();
    }

    /**
     * The getter method of the Deck
     * @return the current deck on the table
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * The setter method of the deck
     * @param deck the deck to use for the current match
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    /**
     * This method allows to obtain the trump card;
     * @return the trump card
     */
    public Card getTrumpCard() {
        return trumpCard;
    }

    /**
     * This method allows to set the trump card
     * @param trumpCard the trump card
     */
    public void setTrumpCard(Card trumpCard) {
        this.trumpCard = trumpCard;
    }

    /**
     * The getter of the suit of the trump of the current match
     *
     * @return the suit of the trump of the current match
     */
    public CardSuit getTrump() {
        return trump;
    }

    /**
     * The setter of the trump suit of the current match
     * @param trump the suit of the trump of the current match
     */
    public void setTrump(CardSuit trump) {
        this.trump = trump;
    }

    /**
     * The getter method of the current player of the match
     *
     * @return the player who have to do his move
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * The setter method of the current player of the match
     * @param currentPlayer the player who have to perform the next move of the match
     */
    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * The getter method for the cards present on the table
     * @return the list of Cards present on the table currently
     */
    public ArrayList<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    /**
     * The getter method who returns the first Player of the current round
     * @return the first player of the current round
     */
    public int getFirstPlayerCurrentRound() {
        return firstPlayerCurrentRound;
    }

    /**
     * The method who set the first player of the current round
     * @param firstPlayerCurrentRound the first player of the current round
     */
    public void setFirstPlayerCurrentRound(int firstPlayerCurrentRound) {
        this.firstPlayerCurrentRound = firstPlayerCurrentRound;
    }

    /**
     * This method allows to the Player to perform his move adding the desired card on the table
     * @param card the card that needs to be added
     * @throws PlayerException if the table is already full
     */
    public void addCardOnTable(Card card) throws PlayerException {
        if(this.cardsOnTable.size() == 2){
            throw new PlayerException(PlayerErrorCode.INVALID_PLAYER_MOVE)
                    .set("message" , "ERROR: cannot play a card, table already full.");
        }
        this.cardsOnTable.add(card);
    }

    /**
     * This method converts into a String format the cards present on the table
     * @return the String format of the cards present on the table
     */
    public String cardsOnTableToString(){
        String cards = new String();
        for (int i=0; i < cardsOnTable.size();i++){
            cards += cardsOnTable.get(i).toString();
        }
        return cards;
    }

    /**
     * This method allows to set the cards on the table starting from a String that represent them
     * @param string the String representing the card to put on the table
     * @throws CardException if the string representing the cards are not correct
     */
    public void cardsOnTableFromString(String string) throws CardException {
        cardsOnTable = new ArrayList<Card>();
        for(int i = 0; i < string.length();i=i+2){
            Card card = new Card(string.substring(i,i+2));
            cardsOnTable.add(card);
        }
    }

    /**
     * This method allows to reset the table after a match is over
     */
    public void reset(){
        this.cardsOnTable = new ArrayList<Card>();

    }

    /**
     * This method allows to get the String version of the current player id
     * @return the string version of the current player id
     */
    public String currentPlayerToString(){
        return String.valueOf(currentPlayer);
    }



}
