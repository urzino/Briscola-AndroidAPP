package it.polimi.ma.group10.trump.model.game;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.enumerations.GameState;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckException;
import it.polimi.ma.group10.trump.model.game.exceptions.game.GameErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.game.GameException;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;

/**
 * This class has a general overview on the game:
 * the game starts from a GM command;
 * it manages players;
 * it can understand the game status;
 * it can restore a game situation starting from a String representing it;
 * it can produce a String representing the current game situation.
 */
public class GameMaster {
    /**
     * The Singleton Instance
     */
    private static final GameMaster ourInstance = new GameMaster();
    /**
     * The list of players participating to the current match
     */
    private ArrayList<Player> players;
    /**
     * The regular expression used to check the game configuration string
     */
    private static final String confStringPattern = "[01][BCGS](([JHK]|[1-7])[BCGS]){0,40}\\." +
            "(([JHK]|[1-7])[BCGS]){0,2}\\.(([JHK]|[1-7])[BCGS]){0,3}\\." +
            "(([JHK]|[1-7])[BCGS]){0,3}\\.(([JHK]|[1-7])[BCGS]){0,40}\\.(([JHK]|[1-7])[BCGS]){0,40}";

    /**
     * This method is charged to return the only instance inside the game of the GameMaster,
     * following the Singleton design pattern
     * @return the GameMaster instance
     */
    public static GameMaster getInstance() {
        return ourInstance;
    }

    /**
     * This method is the constructor
     */
    private GameMaster() {
    }

    /**
     * The getter of the Players list
     * @returnthe list of players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * This method is charged to start a new game starting from the players that will participate
     * @param player1 the first Player that wants to play
     * @param player2 the second Player that wants to play
     * @throws DeckException if the game is wrongly initialized
     * @see DeckException
     */
    public void startGame(Player player1, Player player2) throws DeckException {

        this.players = new ArrayList<Player>();
        this.players.add(player1);
        this.players.add(player2);

        Dealer dealer = Dealer.getInstance();

        dealer.initializeMatch();

    }

    /**
     * get the current status of the game, in order to determine
     * the next action
     */
    public GameState getGameState(){

        GameTable gameTable = GameTable.getInstance();
        int currentPlayer = gameTable.getCurrentPlayer();
        int numCardsInDeck = gameTable.getDeck().getCards().size();
        int numCardsInPlayer0Hand = this.players.get(0).getHand().size();
        int numCardsInPlayer1Hand = this.players.get(1).getHand().size();
        int numCardsOnTable = gameTable.getCardsOnTable().size();
        if(numCardsOnTable == 2 && numCardsInPlayer0Hand == numCardsInPlayer1Hand){ //both players made their move, necessary to find round winner
            return GameState.ROUND_OVER;
        }
        else if(numCardsInDeck >= 2 && numCardsInPlayer0Hand == 2 && numCardsInPlayer1Hand == 2 && numCardsOnTable == 0){ //round winner declared, necessary to distribute cards
            return GameState.ROUND_WINNER_DECLARED;
        }
        else if(numCardsInDeck == 0 && numCardsInPlayer0Hand == 0 && numCardsInPlayer1Hand == 0 && numCardsOnTable == 0){ //the match is over, necessary to declare the winner
            return GameState.MATCH_OVER;
        }
        else if(Math.abs(numCardsInPlayer0Hand - numCardsInPlayer1Hand) <= 1 &&
                this.players.get(currentPlayer).getHand().size() >= this.players.get((currentPlayer + 1) % 2).getHand().size() &&
                numCardsInDeck % 2 == 0 ){
            return GameState.PLAY;
        } else{
            return GameState.INVALID_STATE;
        }
    }

    /**
     * This function is charged to generate a String that will represent
     * the current game situation
     *
     * @return the String describing the game situation
     */
    public String gameConfigurationToString(){
        String gameConfig = new String();
        Dealer dealer = Dealer.getInstance();
        GameTable gameTable = GameTable.getInstance();

        gameConfig += gameTable.currentPlayerToString();
        gameConfig += gameTable.getTrump().getSymbol();
        gameConfig += gameTable.getDeck().toString();
        gameConfig += ".";
        gameConfig += gameTable.cardsOnTableToString();
        gameConfig += ".";
        gameConfig += players.get(0).handToString();
        gameConfig += ".";
        gameConfig += players.get(1).handToString();
        gameConfig += ".";
        gameConfig += players.get(0).pileToString();
        gameConfig += ".";
        gameConfig += players.get(1).pileToString();
        return gameConfig;
    }

    /**
     * This function is charged to re-create a game situation starting from
     * a String representing it
     *
     * @param configuration the String that represent the game situation to be re-created
     * @throws CardException if a card inside the String is invalid
     * @throws DeckException if the created deck is invalid
     * @throws GameException if the configuration does not follow the game rules
     * @throws PlayerException if the player creation is not conform to the game rules
     */
    public void gameConfigurationFromString(String configuration) throws CardException, DeckException, GameException, PlayerException {
        if(configuration.length() != 87) {throw new GameException(GameErrorCode.ILLEGAL_CONFIGURATION_LEN)
                .set("message","ERROR: Configuration string length is "+configuration.length()+ "(87 expected)" );}

        //match if the format of the input string is correct
        Pattern pattern = Pattern.compile(confStringPattern);
        Matcher matcher = pattern.matcher(configuration);
        if(!matcher.matches()) {throw  new GameException(GameErrorCode.INVALID_CONFIGURATION_FORMAT)
        .set("message","ERROR: Configuration string doesn't respect the format");}

        String[] tokens=configuration.split("[.]",-2); // - 2 to handle the case when the piles are empty, so there are two consecutive dot(.)
        GameTable gameTable = GameTable.getInstance();

        int currentPlayer = Character.getNumericValue(tokens[0].charAt(0));//extract the first character of the first token of the configuration string, which is the current player
        CardSuit trump = CardSuit.valueOf(tokens[0].charAt(1));//extract the second character of the first token of the configuration string, which is the trump
        Deck deck = new Deck(tokens[0].substring(2));//extract the remaining part of the first token of the configuration string, which is the deck

        //check if last card correspond to the correct trump
        if(deck.getCards().size()!= 0){
            int lastElIdx = deck.getCards().size()-1;
            CardSuit trumpDeck = deck.getCards().get(lastElIdx).getSuit();
            if(trumpDeck != trump) {throw new GameException(GameErrorCode.LAST_CARD_TRUMP_MISMATCH)
                    .set("message","ERROR: Mismatch between given trump(" + trump +") and " +
                            "last deck card trump(" + trumpDeck + ")");}
        }

        //sets the current player, the trump and the deck in the gameTable
        gameTable.setCurrentPlayer(currentPlayer);
        gameTable.setTrump(trump);
        gameTable.setDeck(deck);

        //build the cards on table from configuration string
        //token[1] is the string associated to the cards on table
        gameTable.cardsOnTableFromString(tokens[1]);

        //build the player hand and the player pile from the configuration string
        //tokens[2] is the string associated to player0's hand
        //tokens[3] is the string associated to player1's hand
        //tokens[4] is the string associated to player0's pile
        //tokens[5] is the string associated to player1's hand
        players = new ArrayList<Player>();
        players.add(new Player(0,tokens[2],tokens[4]));
        players.add(new Player(1,tokens[3],tokens[5]));

        if(gameTable.getCardsOnTable().size() == 0){ // when there are no cards on table the current players needs to perform the first move
            gameTable.setFirstPlayerCurrentRound(currentPlayer);
        } else{ //if there is already one card the table it means that the first card of the round has been played by the other player
                //if there are two cards on the table it means that the other player is the one who played the first card since
                //the current player is updated only when the first card of the match is played and after the round winner is declared
            gameTable.setFirstPlayerCurrentRound((currentPlayer + 1) % 2);
        }

    }





}


