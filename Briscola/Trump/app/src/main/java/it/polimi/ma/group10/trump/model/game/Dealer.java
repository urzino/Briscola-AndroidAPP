package it.polimi.ma.group10.trump.model.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.exceptions.dealer.DealerErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.dealer.DealerException;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckException;

/**
 * This class acts as a real dealer in a real game,
 * it prepares the match shuffling the deck and distributing cards,
 * it distributes the cards,
 * it declares every round winner and, gives him the won cards,
 * it declares the match winner.
 */
public class Dealer {

    /**
     * Singleton instance
     */
    private static final Dealer ourInstance = new Dealer();

    /**
     * This method is charged to return the only instance inside the game of the Dealer,
     * following the Singleton design pattern
     *
     * @return the Dealer instance
     */
    public static Dealer getInstance() {
        return ourInstance;
    }

    /**
     * This method is the constructor
     */
    private Dealer() {
    }

    /**
     * This method initialize the match according to the game rules:
     * once the deck is created and shuffled, the starting player is chosen,
     * the cards are distributed beginning from the starting player, one each player,
     * the trump is set and the rest of the deck is placed on the table
     *
     * @throws DeckException if the deck is wrongly initialized
     */
    public void initializeMatch() throws DeckException {
        GameTable gameTable = GameTable.getInstance();

        Deck deck = new Deck(); // takes a new deck
        deck.shuffle();

        GameMaster gameMaster = GameMaster.getInstance();
        ArrayList<Player> players = gameMaster.getPlayers();


        int rand = ThreadLocalRandom.current().nextInt(0, 2); //randomly decides who is going to start the match
        gameTable.setCurrentPlayer(rand);
        gameTable.setFirstPlayerCurrentRound(rand);
        gameTable.getCardsOnTable().clear();

        int numPlayer = players.size();

        for (int i = 0; i < 3 * numPlayer; i++) { //distribute cards to players, one for each, starting from the first one playing

            if (rand == 0) {
                players.get(i % numPlayer).getHand().add(deck.drawCard());
            } else {
                players.get((i + 1) % numPlayer).getHand().add(deck.drawCard());
            }


        }
        Card trump = deck.drawTrump();
        gameTable.setTrump(trump.getSuit()); //puts the trump on the table
        gameTable.setTrumpCard(trump);
        gameTable.setDeck(deck); //puts the deck on the table

    }

    /**
     * This method is charged to find the round winner following the game rules
     *
     * @return the Player who won the round
     * @throws DealerException if the preconditions to declare the round winner are not met
     */
    public Player declareRoundWinner() throws DealerException {

        GameTable table = GameTable.getInstance();

        //Exception to handle wrong situation to declare round winner
        if (table.getCardsOnTable().size() != 2) {
            throw new DealerException(DealerErrorCode.MISSING_CARD_ON_TABLE).set("message", "ERROR: cards on table are not exactly 2, cannot declare round winner");
        }

        GameMaster gameMaster = GameMaster.getInstance();
        Player firstRoundPlayer;
        Player secondRoundPlayer;

        int firstCardPlayedByPlayerId = table.getFirstPlayerCurrentRound(); //winner of previous round is the one who played the first card

        ArrayList<Player> players = gameMaster.getPlayers();


        if (players.get(0).getId() == firstCardPlayedByPlayerId) { //identify the first and second players of the current round

            firstRoundPlayer = players.get(0);
            secondRoundPlayer = players.get(1);

        } else {

            secondRoundPlayer = players.get(0);
            firstRoundPlayer = players.get(1);

        }

        CardSuit trump = table.getTrump();
        ArrayList<Card> cardsOnTable = table.getCardsOnTable();
        Card cardFirstPlayer = cardsOnTable.get(0);
        Card cardSecondPlayer = cardsOnTable.get(1);

        if (cardFirstPlayer.getSuit() == cardSecondPlayer.getSuit()) { // if the suit of the two cards is the same
            if (cardFirstPlayer.getValue().getRank() < cardSecondPlayer.getValue().getRank()) { //the one with the best card in terms of rank wins the round

                assignCardsAndPointsToRoundWinner(firstRoundPlayer, cardsOnTable);
                table.setFirstPlayerCurrentRound(firstRoundPlayer.getId());
                table.setCurrentPlayer(firstRoundPlayer.getId());
                return firstRoundPlayer;

            } else {

                assignCardsAndPointsToRoundWinner(secondRoundPlayer, cardsOnTable);
                table.setFirstPlayerCurrentRound(secondRoundPlayer.getId());
                table.setCurrentPlayer(secondRoundPlayer.getId());
                return secondRoundPlayer;

            }
        } else {
            if (cardSecondPlayer.getSuit() == trump) { // if only the second player  played a trump, he wins the round

                assignCardsAndPointsToRoundWinner(secondRoundPlayer, cardsOnTable);
                table.setFirstPlayerCurrentRound(secondRoundPlayer.getId());
                table.setCurrentPlayer(secondRoundPlayer.getId());
                return secondRoundPlayer;
            } else {// any other case is won by the first player

                assignCardsAndPointsToRoundWinner(firstRoundPlayer, cardsOnTable);
                table.setFirstPlayerCurrentRound(firstRoundPlayer.getId());
                table.setCurrentPlayer(firstRoundPlayer.getId());
                return firstRoundPlayer;
            }
        }
    }

    /**
     * This method is charged to distribute the cards to the player for the next round:
     * the first card is given to the one that won the previous round, the second to the other player
     *
     * @throws DeckException   if there are still cards on the table, so the previous round is not yet over
     * @throws DealerException if a player is not in an optimal situation to get the new card
     */
    public void distributeCards() throws DeckException, DealerException {

        GameTable table = GameTable.getInstance();
        GameMaster gameMaster = GameMaster.getInstance();

        ArrayList<Player> players = gameMaster.getPlayers();

        //Exceptions to handle invalid situations for cards distribution
        if (table.getCardsOnTable().size() != 0) {
            throw new DealerException(DealerErrorCode.ILLEGAL_CARD_DISTRIBUTION).set("message", "ERROR: There are still cards on table");
        }

        for (Player player : players) {
            if (player.getHand().size() != 2) {
                throw new DealerException(DealerErrorCode.ILLEGAL_CARD_DISTRIBUTION)
                        .set("message", "ERROR: player " + player.getId() + " has " + player.getHand().size() + "!=2 cards in its hand");
            }
        }


        int previousHandWinnerId = table.getFirstPlayerCurrentRound();

        if (players.get(0).getId() == previousHandWinnerId) { //gives the first card of the deck to the winner of the previous round and the second one to the looser

            players.get(0).getHand().add(table.getDeck().drawCard());
            Card cardForLooser = table.getDeck().drawCard();
            players.get(1).getHand().add(cardForLooser);

            if (players.get(0) instanceof PlayerAI) {//winningroundAI notified of loosing the trump
                if (table.getDeck().getCards().size() == 0) {
                    Log.i("AAAAAAAAAAAA", "GIVEN TRUMP TO AI 0 " + cardForLooser.toString());
                    ((PlayerAI)players.get(0)).possibleOpponentsCards.add(cardForLooser);
                }
            }

        } else {

            players.get(1).getHand().add(table.getDeck().drawCard());
            Card cardForLooser = table.getDeck().drawCard();
            players.get(0).getHand().add(cardForLooser);


            if (players.get(1) instanceof PlayerAI) {//winningroundAI notified of loosing the trump
                if (table.getDeck().getCards().size() == 0) {
                    Log.i("AAAAAAAAAAAA", "GIVEN TRUMP TO AI 1 " + cardForLooser.toString());
                    ((PlayerAI)players.get(1)).possibleOpponentsCards.add(cardForLooser);
                }
            }

        }

    }

    /**
     * This method is charged, at the end of the match, to compare the two players points
     * and to declare the winner according to the game rules
     *
     * @return the Player won the match or null in case of DRAW
     * @throws DealerException if the preconditions to perform this task are not met
     */
    public Player declareWinner() throws DealerException {
        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();

        Player player1 = gameMaster.getPlayers().get(0);
        Player player2 = gameMaster.getPlayers().get(1);

        //Exceptions to detect wrong game configurations to declare the winner
        if (gameTable.getDeck().getCards().size() != 0 || gameTable.getCardsOnTable().size() != 0 || player1.getHand().size() != 0 || player2.getHand().size() != 0) {
            throw new DealerException(DealerErrorCode.MATCH_NOT_OVER).set("message", "ERROR: the match is not over, cannot declare a winner");
        }
        if ((player1.getPoints() + player2.getPoints()) != 120) {
            throw new DealerException(DealerErrorCode.MATCH_FINAL_POINTS_NOT_COHERENT)
                    .set("message", "ERROR: cumulative points at the end of the match are not 120 but " + (player1.getPoints() + player2.getPoints()));
        }

        //the winner player is the one with the greater amount of points
        if (player1.getPoints() > player2.getPoints()) {

            return player1;

        } else if (player1.getPoints() < player2.getPoints()) {

            return player2;
        } else {
            return null; //draw
        }


    }

    /**
     * This method is charged to reset the Game once a match is over
     */
    public void resetGame() {

        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();

        ArrayList<Player> players = gameMaster.getPlayers();

        for (Player p : players) {
            p.reset();
        }

        gameTable.reset();

    }

    /**
     * This method is charged to assign cards and their related points to the round winner
     * according to the game rules, cleaning the table from the mentioned cards
     *
     * @param winner       the Player elected as the round winner
     * @param cardsOnTable the Cards won by the Player
     */
    private void assignCardsAndPointsToRoundWinner(Player winner, ArrayList<Card> cardsOnTable) {

        winner.claimWonRoundPrize(cardsOnTable);
        GameTable.getInstance().getCardsOnTable().clear();

    }


}
