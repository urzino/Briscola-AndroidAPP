package it.polimi.ma.group10.trump;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import it.polimi.ma.group10.trump.model.game.Card;
import it.polimi.ma.group10.trump.model.game.Dealer;
import it.polimi.ma.group10.trump.model.game.Deck;
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.GameTable;
import it.polimi.ma.group10.trump.model.game.Player;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.game.enumerations.GameState;
import it.polimi.ma.group10.trump.model.game.exceptions.card.CardException;
import it.polimi.ma.group10.trump.model.game.exceptions.dealer.DealerException;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckException;
import it.polimi.ma.group10.trump.model.game.exceptions.game.GameErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.game.GameException;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;


/**
 * This class contains all methods used by test classes
 */
class TestMethods {
    private static final TestMethods ourInstance = new TestMethods();

    static TestMethods getInstance() {
        return ourInstance;
    }

    private TestMethods() {
    }


    /**
     * test execution of a match starting from a configuration
     * @param configuration the starting game point
     * @param moves list of moves performed by the players
     * @return
     */
    public String moveTest(String configuration, String moves)  {
        //check moves char correctness
        String result = new String();
        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();
        Dealer dealer = Dealer.getInstance();
        try {
            for (char ch: moves.toCharArray()) {
                if(ch != '0' && ch !='1' && ch !='2'){throw new GameException(GameErrorCode.INVALID_MOVES_CHAR)
                        .set("message","ERROR: Invalid char:'"+ ch +"' for a move");}
            }
            gameMaster.gameConfigurationFromString(configuration);
            if(remaingMoves() < moves.length()){throw new GameException(GameErrorCode.INVALID_NUMBER_OF_MOVES)
                    .set("message","ERROR: Too much moves in the configuration");}
            ArrayList<Integer> movesList = new ArrayList<Integer>();
            for (int i = 0; i < moves.length(); i++) {
                movesList.add(Character.getNumericValue(moves.charAt(i)));
            }

            //moveOn is used to stop the test in the correct moment due to 'moveTest' specification
            int moveOn = 0;
            while (movesList.size() > 0 || moveOn > 0) {
                checkCardsUniqueness();
                GameState currentState = GameMaster.getInstance().getGameState();

                ArrayList<Player> players = gameMaster.getPlayers();
                switch (currentState){
                    case ROUND_OVER: {
                        dealer.declareRoundWinner();
                        moveOn--;
                        break;
                    }
                    case ROUND_WINNER_DECLARED: {
                        dealer.distributeCards();
                        moveOn--;
                        break;
                    }
                    case MATCH_OVER: {
                        Player winner = dealer.declareWinner();
                        moveOn--;
                        if(winner == null){ return "DRAW";}
                        else {return "WINNER" + winner.getId() + winner.getPoints();}
                    }
                    case PLAY: {
                        if(movesList.size()==0){
                            moveOn--;
                            break;
                        }
                        int move = movesList.remove(0);
                        askForMoveTest(move);
                        if( players.get(0).getHand().size() == players.get(1).getHand().size()){
                            moveOn = 2;
                        }
                        break;
                    }
                    case INVALID_STATE: {
                        return "ERROR: Game state is invalid";
                    }
                }
            }

            return gameMaster.gameConfigurationToString();



        } catch (DealerException e) {
            return (String) e.getProperties().get("message");
        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        }
    }

    /**
     * method to execute the given move (in combination with moveTest(...)
     * @param move
     * @throws PlayerException
     */
    private void askForMoveTest(int move) throws PlayerException {
        GameTable gameTable = GameTable.getInstance();
        GameMaster gameMaster = GameMaster.getInstance();
        ArrayList<Player> players = gameMaster.getPlayers();
        if( gameTable.getCurrentPlayer() == players.get(0).getId()){
            players.get(0).performMove(move);
        }else{
            players.get(1).performMove(move);
        }
    }

    /**
     * method to test initial state of the game
     */
    public String initialStateTest(){
        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();

        Player player1 = new Player(0, "testPl1");
        PlayerAI player2 = new PlayerAI(1, "testPl2",0);

        try {
            gameMaster.startGame(player1 , player2);
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        }

        if(gameTable.getDeck().getCards().size() != 34 ||
           gameTable.getDeck().getCards().get(33).getSuit() != gameTable.getTrump() ||
           gameTable.getCardsOnTable().size() != 0 ||
           player1.getHand().size() != 3 ||
           player2.getHand().size() != 3 ||
           player1.getPile().size() != 0 ||
           player2.getPile().size() != 0){
            return "ERROR: cards wrongly distributed";
        }

        if(gameTable.getCurrentPlayer() != gameTable.getFirstPlayerCurrentRound()){
            return "ERROR: Players round wrongly initialized ";
        }

        try {
            checkCardsUniqueness();
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        }


        return "OK";
    }

    public String testSingleMove(String configuration, int move , int playerId){

        GameMaster gameMaster = GameMaster.getInstance();

        try {

            gameMaster.gameConfigurationFromString(configuration);

            gameMaster.getPlayers().get(playerId).performMove(move);

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        }
        return gameMaster.gameConfigurationToString();
    }

    public String testRoundWinnerDeclaration(String configuration){

        GameMaster gameMaster = GameMaster.getInstance();
        Dealer dealer = Dealer.getInstance();

        try {

            gameMaster.gameConfigurationFromString(configuration);

            dealer.declareRoundWinner();

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        } catch (DealerException e) {
            return (String) e.getProperties().get("message");
        }


        return gameMaster.gameConfigurationToString();
    }

    public String testCardDistribution(String configuration){

        GameMaster gameMaster = GameMaster.getInstance();
        Dealer dealer = Dealer.getInstance();

        try {

            gameMaster.gameConfigurationFromString(configuration);

            dealer.distributeCards();

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        } catch (DealerException e) {
            return (String) e.getProperties().get("message");
        }


        return gameMaster.gameConfigurationToString();
    }

    public String testWinnerDeclaration(String configuration){

        GameMaster gameMaster = GameMaster.getInstance();
        Dealer dealer = Dealer.getInstance();
        Player winner;
        try {

            gameMaster.gameConfigurationFromString(configuration);
            winner = dealer.declareWinner();

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        } catch (DealerException e) {
            return (String) e.getProperties().get("message");
        }


        if(winner == null){ return "DRAW";}
        else {return "WINNER" + winner.getId() + winner.getPoints();}

    }

    public String testDeckCreation(String deck){
        try {
            Deck d = new Deck(deck);
            return d.toString();
        } catch (DeckException e){
            return (String) e.getProperties().get("message");
        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        }
    }

    public String testDeckDrawTrump(String deck){
        try {
            Deck d = new Deck(deck);
            Card trump = d.drawTrump();
            return d.toString() + " " + trump.toString() ;
        } catch (DeckException e){
            return (String) e.getProperties().get("message");
        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        }
    }

    public String testDeckDraw(String deck){
        try {
            Deck d = new Deck(deck);
            Card drawnCard = d.drawCard();
            return d.toString() + " " + drawnCard.toString();
        } catch (DeckException e){
            return (String) e.getProperties().get("message");
        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        }
    }

    /**
     * Test all the possible combination in the creation of a card
     * @param card the card in string format
     * @return the card itself in case of no problem, the error message otherwise
     */
    public String testCardCreation(String card){

        try {
            Card c = new Card(card);
            return c.toString();
        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        }
    }

    /**
     * This method checks if there are not duplicate Cards inside the game
     * but 40 different cards.
     *
     * @throws GameException if the check fails
     */
    private void checkCardsUniqueness() throws GameException{

        Set<Card> allCards = new HashSet<>();


        for (Player p: GameMaster.getInstance().getPlayers()) {
            allCards.addAll(p.getHand());
            allCards.addAll(p.getPile());
        }
        allCards.addAll(GameTable.getInstance().getCardsOnTable());
        allCards.addAll(GameTable.getInstance().getDeck().getCards());

        //add all the cards in a Set(where each element is unique) and checks if the cards are 40
        if(allCards.size() != 40) {throw  new GameException(GameErrorCode.INVALID_SUM_OF_CARDS)
                .set("message","ERROR: Cards should be unique in the game");}
    }

    /**
     * This method is charged to count how many moves are missing
     * to the end of the match
     *
     * @return the number of missing moves
     */
    public int remaingMoves() {
        GameMaster gameMaster = GameMaster.getInstance();
        int remaining = GameTable.getInstance().getDeck().getCards().size();
        for (Player p: gameMaster.getPlayers()) {
            remaining += p.getHand().size();
        }
        // the remaining moves are calculated as the sum of the cards in the deck plus the card in the hands of the players
        return  remaining;
    }

    public String testConfigurationFromString( String configuration){

        GameMaster gameMaster = GameMaster.getInstance();

        try {
            gameMaster.gameConfigurationFromString(configuration);

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        }

        return gameMaster.gameConfigurationToString();

    }

    public String testGetGameState(String configuration){
        GameMaster gameMaster = GameMaster.getInstance();

        try {
            gameMaster.gameConfigurationFromString(configuration);

        } catch (CardException e) {
            return (String) e.getProperties().get("message");
        } catch (DeckException e) {
            return (String) e.getProperties().get("message");
        } catch (GameException e) {
            return (String) e.getProperties().get("message");
        } catch (PlayerException e) {
            return (String) e.getProperties().get("message");
        }

        return gameMaster.getGameState().toString();
    }
}
