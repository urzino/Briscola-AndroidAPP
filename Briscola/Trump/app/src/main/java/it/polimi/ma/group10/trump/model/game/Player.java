package it.polimi.ma.group10.trump.model.game;


import java.util.ArrayList;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;

/**
 * The Player acts as a normal player in a real match:
 * it can play a card in his hand;
 * it holds the pile of won cards.
 */
public class Player {
    /**
     * The id of the player
     */
    private int id;

    /**
     * The nickname of the player
     */
    private String nickname;

    /**
     * The list of cards that the player can play
     */
    private ArrayList<Card> hand;

    /**
     * The list of cards that the player won during the current match
     */
    private ArrayList<Card> pile;

    /**
     * The amount of points that the current player gained during the current match
     */
    private int points;

    /**
     * The default player constructor
     * @param id it's id
     * @param nickname it's nickname
     */
    public Player(int id, String nickname){
        this.id = id;
        this.nickname = nickname;
        this.hand = new ArrayList<Card>();
        this.pile = new ArrayList<Card>();
        this.points = 0;
    }

    /**
     * The constructor of a player starting from a known configuration
     * @param id it's id
     * @param hand the cards in his hand
     * @param pile the cards won up to now
     * @throws CardException if the cards won or in the hand are invalid
     * @throws PlayerException if the known configuration is invalid
     */
    public Player(int id, String hand, String pile) throws CardException, PlayerException {
        this.id=id;
        this.hand = new ArrayList<Card>();
        this.pile = new ArrayList<Card>();

        // Exceptions to avoid to create a Player with a wrong configuration
        if(hand.length()>6) {throw new PlayerException(PlayerErrorCode.INVALID_PLAYER_HAND).set("message" , "ERROR: Player " + this.id + " has more than 3 card in his hand");}
        if(((pile.length()/2)%2)!=0) {throw  new PlayerException(PlayerErrorCode.INVALID_PLAYER_PILE).set("message" , "ERROR: Player " + this.id + " has an odd number of cards in his pile");}
        if((pile.length()/2)>40){throw new PlayerException(PlayerErrorCode.INVALID_PLAYER_PILE_SIZE).set("message", "ERROR: Player " + this.id + " pile cannot have more than 40 cards");}

        for(int i = 0; i < hand.length();i=i+2){
            Card card = new Card(hand.substring(i,i+2));
            this.hand.add(card);
        }
        for(int i = 0; i < pile.length();i=i+2){
            Card card = new Card(pile.substring(i,i+2));
            this.pile.add(card);
        }
        this.points = calculatePoints();
    }

    /**
     * the getter method of the current Player id
     * @return the current player id
     */
    public int getId() {
        return id;
    }

    /**
     * The setter method charged to set the current player id
     * @param id the current player id to be set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The getter method used to obtain the current Player nickname
     * @return the current player nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * The setter method charged to set the current player nickname
     * @param nickname the current player nickname to be set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * The method that allows to obtain che list of cards present in the
     * current player's hand
     * @return the list of card in the player hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * This setter method allows to set the cards in the player hand
     * @param hand
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    /**
     * this method allows to get the list of cards that the player won up to a given
     * moment of time during the match flow
     * @return the list of won cards by the player
     */
    public ArrayList<Card> getPile() {
        return pile;
    }

    /**
     * This getter method returns the amount of points gained by the player
     * up to the moment it is invoked
     *
     * @return the amount of points gained by the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * The method used to set points
     * @param points the points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * This method allows the player to perform it's desired move
     * @param move the index of the card in its hand to play
     * @throws PlayerException if the move asked is not valid
     */
    public Card performMove(int move) throws PlayerException {
        GameTable gameTable=GameTable.getInstance();

        //Exceptions to handle invalid move requests
        if(gameTable.getCurrentPlayer() != this.id) {
            throw new PlayerException(PlayerErrorCode.NOT_PLAYER_TURN)
                .set("message" , "ERROR: It's not player " + this.id + " turn");
        }
        if(move >= this.hand.size() ) {
            throw new PlayerException(PlayerErrorCode.CARD_NOT_IN_HAND)
                .set("message" , "ERROR: card " + (move+1) + " can't be played; only " + this.hand.size() + " cards available.");
        }
        if(gameTable.getCardsOnTable().size() == 2){
            throw new PlayerException(PlayerErrorCode.INVALID_PLAYER_MOVE)
                    .set("message" , "ERROR: cannot play a card, table already full.");
        }


        Card selectedCard=this.hand.remove(move);
        gameTable.addCardOnTable(selectedCard);

        if(gameTable.getCardsOnTable().size() == 1){ //if the played card is the first of the round, assign the turn to the other player
            if (this.id == 0){
                gameTable.setCurrentPlayer(1);
            }else{
                gameTable.setCurrentPlayer(0);
            }
        }

        return selectedCard;
    }

    /**
     * This method allows to the player to claim the prize of a won round
     * @param cardsWon the cards won of the round
     */
    public void claimWonRoundPrize(ArrayList<Card> cardsWon){
        for (Card c: cardsWon) {
            this.points += c.getValue().getValue();
            this.pile.add(c);
        }
    }

    /**
     * This methd allows to reset the player after
     * a match is over
     */
    public void reset(){
        this.hand = new ArrayList<Card>();
        this.pile = new ArrayList<Card>();
        this.points = 0;
    }

    /**
     * This method allows to convert into String format the list of cards in the player's hand
     * @return the String format of the list of Cards in the Player's hand
     */
    public String handToString(){
        String handString = new String();
        for (int i = 0 ; i < this.hand.size() ; i++){
            handString += this.hand.get(i).toString();
        }

        return handString;
    }

    /**
     * This method allows to get the String format of the list of cards in the player's pile
     *
     * @return the String format of the list of Cards in the Player's pile
     */
    public String pileToString(){
        String pileString = new String();
        for (int i = 0 ; i < this.pile.size() ; i++){
            pileString += this.pile.get(i).toString();
        }
        return pileString;
    }

    /**
     * This method allows to calculate points gained by te player up to
     * the moment it is invoked
     * @return the amount of points gained
     */
    public int calculatePoints(){
        int points=0;
        for (int i = 0 ; i < pile.size(); i++){
            points+=pile.get(i).getValue().getValue();
        }
        return points;
    }

    public Card remotePerformMove(int card){
        return this.hand.remove(card);
    }



}
