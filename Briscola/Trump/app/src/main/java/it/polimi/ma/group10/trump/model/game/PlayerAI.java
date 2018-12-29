package it.polimi.ma.group10.trump.model.game;

import android.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;

/**
 * This class represent a player that do not perform moves
 * asking to the user what to do but computing the "best" move to do.
 */
public class PlayerAI extends Player {

    /**
     * List of the possible cards available for the opponent
     */
    public ArrayList<Card> possibleOpponentsCards;

    /**
     * The current trump of the match
     */
    private Card trump;

    /**
     * The eventual card on tavble
     */
    private Card cardOnTable;

    /**
     * True if the trump has just been seen distributing
     */
    public boolean trumpSeen;

    /**
     * the difficulty level of the AI
     */
    private int difficulty;

    /**
     * The playerAI constructor
     *
     * @param id       it's id
     * @param nickname it's nickname
     */
    public PlayerAI(int id, String nickname, int difficulty) {
        super(id, nickname);
        this.difficulty = difficulty;
        this.possibleOpponentsCards = new Deck().getCards();
        this.cardOnTable = null;
        this.trumpSeen = false;
    }

    /**
     * With this method the AI learns which is the current trump of the match and removes it from
     * the possible opponents cards
     *
     * @param trump is the current match trump
     */
    public void setTrump(Card trump) {
        this.trump = new Card(trump.getValue(), trump.getSuit());
        removeCardFromPossibleOpponentsCards(trump);
    }

    /**
     * The setter method that  updates the current card on table for the AI decision
     *
     * @param cardOnTable the current card on table
     */
    public void setCardOnTable(Card cardOnTable) {
        this.cardOnTable = cardOnTable;
    }

    /**
     * the overrided method used by the playerAI do decide autonomously the move
     *
     * @return ArrayList containing the played card and the index of it
     * @throws PlayerException if the move is not allowed
     */
    public ArrayList<Object> performMove() throws PlayerException {

        for (Card card : this.getHand()) {
            removeCardFromPossibleOpponentsCards(card);
        }

        int move = pickUpBestCard(this.cardOnTable, this.getHand(), this.difficulty, this.possibleOpponentsCards, this.trump);
        Card selectedCard = super.performMove(move);
        ArrayList<Object> cardAndPosition = new ArrayList<Object>();
        cardAndPosition.add(new Integer(move));
        cardAndPosition.add(selectedCard);

        cardOnTable = null;

        return cardAndPosition;

    }

    /**
     * The method that performs a move when the AI is playng remotely
     *
     * @return the card and its index
     */
    public ArrayList<Object> remotePerformMove() {

        for (Card card : this.getHand()) {
            removeCardFromPossibleOpponentsCards(card);
        }

        int move = pickUpBestCard(this.cardOnTable, this.getHand(), this.difficulty, this.possibleOpponentsCards, this.trump);
        Card selectedCard = super.remotePerformMove(move);
        ArrayList<Object> cardAndPosition = new ArrayList<Object>();
        cardAndPosition.add(new Integer(move));
        cardAndPosition.add(selectedCard);

        cardOnTable = null;

        return cardAndPosition;

    }

    /**
     * This method allows to remove a card from the list of the possible opponent's cards
     *
     * @param cardToRemove the card that needs to be removed
     * @return true if succesful
     */
    public boolean removeCardFromPossibleOpponentsCards(Card cardToRemove) {
        return possibleOpponentsCards.remove(cardToRemove);
    }


    public int pickUpBestCard(Card cardOnTable, ArrayList<Card> hand, int difficulty, ArrayList<Card> possibleOpponentsCards, Card trump) {
        boolean isFirst = false;
        if (cardOnTable == null) {
            isFirst = true;
        }
        if (difficulty == 0) {
            return ThreadLocalRandom.current().nextInt(0, hand.size());
        } else if (difficulty == 1) {
            return ruleBasedDecision(isFirst, cardOnTable, hand, possibleOpponentsCards, trump);
        } else if (possibleOpponentsCards.size() > 6) {
            Log.i("AI task", "ruling");
            return ruleBasedDecision(isFirst, cardOnTable, hand, possibleOpponentsCards, trump);
        } else {
            Log.i("AI task", "simulating");
            return simulationBasedDecision();
        }
    }

    private int ruleBasedDecision(boolean isFirst, Card cardOnTable, ArrayList<Card> hand, ArrayList<Card> possibleOpponentsCards, Card trump) {
        //decide depending on the risk of each card
        if (isFirst) {
            float minRisk = Float.MAX_VALUE;
            int minRiskIdx = 0;
            for (int i = 0; i < hand.size(); i++) {
                float r = firstCardRisk(hand.get(i), possibleOpponentsCards, trump);
                Log.i("Value first", "Card:" + hand.get(i).toString() + " Value : " + Float.toString(r));
                if (r < minRisk) {
                    minRisk = r;
                    minRiskIdx = i;
                }
            }
            Log.i("abilele", String.valueOf(hand.size()) + " " + String.valueOf(minRiskIdx));
            if (hand.get(minRiskIdx).getSuit() == trump.getSuit()) { //if it is going to play a trump checks if it exists a lower one in the hand
                int option = -1;
                int k = 0;
                for (Card c : hand) {
                    if (c != hand.get(minRiskIdx) && c.getValue().getRank() > hand.get(minRiskIdx).getValue().getRank()) {
                        option = k;
                        break;
                    }
                    k++;
                }
                if (option != -1) {
                    k = 0;
                    for (Card c : hand) {
                        if (c != hand.get(option) && c.getValue().getRank() > hand.get(option).getValue().getRank()) {
                            option = k;
                            break;
                        }
                        k++;
                    }

                }
                if (option != -1) {
                    return option;
                } else {
                    return minRiskIdx;
                }

            } else {
                return minRiskIdx;
            }
        } else {
            Double max = Double.NEGATIVE_INFINITY;
            int minRiskIdx = 0;
            for (int i = 0; i < hand.size(); i++) {
                Double r = secondCardGain(hand.get(i), cardOnTable, trump, possibleOpponentsCards);
                Log.i("Value second", "Card:" + hand.get(i).toString() + " Value : " + r.toString());
                if (r > max) {
                    max = r;
                    minRiskIdx = i;
                }

            }
            Log.i("Max", max.toString());
            return minRiskIdx;
        }
    }

    private float firstCardRisk(Card card, ArrayList<Card> possibleOpponentsCards, Card trump) {
        float risk = 0f;
        float alpha = 3f;
        float beta = 3f;
        float takingCardsNr = 0f;
        float loads = 0f;
        for (Card temp : possibleOpponentsCards) {
            CardSuit trumpSuite = trump.getSuit();
            if (card.getSuit() == trumpSuite) {
                // both are trump and the opponent has an higher value trump (unlikely that a trump is used against another trump)
                if (temp.getSuit() == trumpSuite &&
                        temp.getValue().getValue() > card.getValue().getValue())
                    risk += (card.getValue().getValue() + temp.getValue().getValue()) / beta;
                takingCardsNr++;

                if (temp.getSuit() != trumpSuite && temp.getValue().getValue() >= 4) {
                    loads += 1f;
                }
            } else {
                //case both no trump, but opponent could have higher value card
                if (temp.getSuit() != trumpSuite &&
                        temp.getValue().getValue() > card.getValue().getValue()) {
                    int opCardValue = temp.getValue().getValue();
                    if (opCardValue >= 10) {
                        opCardValue *= 1.4;
                    }
                    risk += (card.getValue().getValue() + opCardValue);
                    takingCardsNr++;
                }
                //case opponent could have a trump (so the card will be taken for sure)
                if (temp.getSuit() == trumpSuite) {
                    risk += (card.getValue().getValue() + temp.getValue().getValue()) * alpha;
                    takingCardsNr++;
                }
            }
        }
        risk /= (possibleOpponentsCards.size() + 0.00002f);
        if (card.getSuit() == trump.getSuit()) {
            risk += (loads + ((34 - possibleOpponentsCards.size()) / 7d)); //add a nr that increases when remaining cards reduces
        }
        //add a small value depending on the rank, in order to discriminate different ranks ( but same risk)
        risk -= card.getValue().getRank() / 100;
        if (card.getValue().getValue() >= 10 && card.getSuit() != trump.getSuit()) {
            risk *= 1.5f;
        }

        Log.d("risk card", card.toString() + "risk " + risk);
        //add a penalty to risk free trump (usually high trumps have 0 risk if there is
        //no other trump higher then it

        return risk;
    }

    private Double secondCardGain(Card card, Card cardOnTable, Card trump, ArrayList<Card> possibleOpponentsCards) {

        Double value = 0d;

        //case card on table is a trump
        if (cardOnTable.getSuit() == trump.getSuit() && card.getSuit() == trump.getSuit()) {

            if (cardOnTable.getValue().getValue() < card.getValue().getValue()) {
                //case card is a trump and has an higher value than opponent's card
                value += new Double((cardOnTable.getValue().getValue()) + ((card.getValue().getRank()) / 100d));//prioritize lowest trump
                value *= 1.5;
            } else {
                if (card.getSuit() == trump.getSuit()) {
                    value = new Double(-(cardOnTable.getValue().getValue() + card.getValue().getValue()));
                    value *= 2;
                } else {
                    value = new Double(-(cardOnTable.getValue().getValue() + card.getValue().getValue()));
                }
            }

            for (Card c : possibleOpponentsCards) {
                if (c.getValue().getValue() >= 4 && c.getSuit() != trump.getSuit()) {
                    value -= 1.3d;
                }
            }


        } else if (cardOnTable.getSuit() == card.getSuit()) {
            //case card has the same suit of opponent'card
            if (cardOnTable.getValue().getValue() < card.getValue().getValue()) {
                //case card has the same suit of opponent'card and has an higher value
                value += cardOnTable.getValue().getValue() + card.getValue().getValue();
                if (value >= 10) {
                    value *= 3;
                } else if (value >= 4) {
                    value *= 2;
                } else {
                    value *= 1.5;
                }
            } else {
                //case card has the same suit of opponent'card and has an lower value
                value = new Double(-(cardOnTable.getValue().getValue() + card.getValue().getValue()));
            }
        } else if (cardOnTable.getSuit() == trump.getSuit()) {
            //case opponent's card is a trump and card is not a trump
            value = new Double(-(cardOnTable.getValue().getValue() + card.getValue().getValue()));
        } else if (card.getSuit() == trump.getSuit()) {
            //case opponent's card is not trump and card is a trump
            value = new Double((cardOnTable.getValue().getValue()) + ((card.getValue().getRank()) / 100d));//prioritize lowest trump

            for (Card c : possibleOpponentsCards) {
                if (c.getValue().getValue() >= 4 && c.getSuit() != trump.getSuit()) {
                    value -= 1.3d;
                }
            }
        } else {
            //case opponent's card is not a trump and card is not a trump
            value = new Double(-(cardOnTable.getValue().getValue() + card.getValue().getValue()));
        }

        return value;
    }

    private int simulationBasedDecision() {

        ArrayList<Double> points = new ArrayList<Double>();

        for (Card c : this.getHand()) {

            Card trump = null;

            if (this.getHand().contains(this.trump) || this.possibleOpponentsCards.contains(this.trump) || this.trump.equals(cardOnTable)) {
                Log.i("sim trump ", "distributed");
                this.trumpSeen = true;
            }

            if (!this.trumpSeen) {//trump still on table
                Log.i("sim trump ", "not yet distributed");
                trump = this.trump;
            }

            ArrayList<Card> myHandCopy = (ArrayList<Card>) this.getHand().clone();
            ArrayList<Card> cardsOnTable = new ArrayList<Card>();
            ArrayList<Card> possibleOpponentCardCopy = (ArrayList<Card>) possibleOpponentsCards.clone();
            int myPointsCopy = this.getPoints();
            if (cardOnTable != null) {
                cardsOnTable.add(cardOnTable);
            }
            myHandCopy.remove(c);
            cardsOnTable.add(c);

            Log.i("sim starting situation", "myHand =" + myHandCopy.toString() + " cardsTable= " + cardsOnTable.toString() +
                    "possible opp cards= " + possibleOpponentCardCopy.toString() + " trump= " + trump +
                    " points= " + String.valueOf(myPointsCopy));


            points.add(simulateMatches(myHandCopy, cardsOnTable, possibleOpponentCardCopy, trump, myPointsCopy, false, this.trump));
        }

        Double max = Double.NEGATIVE_INFINITY;
        int bestCardIdx = 0;
        for (int i = 0; i < points.size(); i++) {
            Double r = points.get(i);
            Log.i("card and sim res", r.toString() + " index " + String.valueOf(i) + " card " + this.getHand().get(i).toString());
            if (r.compareTo(max) > 0) {
                max = r;
                bestCardIdx = i;
            }

        }

        Log.i("sim card chosen", String.valueOf(bestCardIdx));

        return bestCardIdx;
    }

    private Double simulateMatches(ArrayList<Card> myHand, ArrayList<Card> cardsOnTable, ArrayList<Card> possibleOpponentsCards, Card trump, int myPoints, boolean isMyTurn, Card trumpForSuit) {
        if (myHand.size() == 0 && cardsOnTable.size() == 0 && possibleOpponentsCards.size() == 0 && trump == null) { // match over
            return new Double(myPoints - 60);
        }
        switch (cardsOnTable.size()) {

            case (2): {//2 card on table

                int pointsToAdd = cardsOnTable.get(0).getValue().getValue() + cardsOnTable.get(1).getValue().getValue(); //the points to be added to the winner

                int newPoints = myPoints;
                int roundWinner = calculateRoundWinner(cardsOnTable, isMyTurn, trumpForSuit.getSuit());

                boolean newIsMyTurn;
                cardsOnTable.clear();

                if (roundWinner == 0) {//if the AI is the winner it gains the points and the first hand in the next turn
                    newPoints += pointsToAdd;
                    newIsMyTurn = true;
                } else {
                    newIsMyTurn = false;
                }

                if (trump == null) { //if all cards have been distributed
                    ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                    ArrayList<Card> newCardsOnTable = new ArrayList<Card>();
                    ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();

                    return simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, newPoints, newIsMyTurn, trumpForSuit);
                } else { //if it is necessary to distribute cards
                    if (possibleOpponentsCards.size() != 3) { //if it is not necessary to distribute the trump
                        Double tempPts = 0d;
                        int k = 0;
                        for (Card c : possibleOpponentsCards) { //tries every possible card distribution

                            ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                            ArrayList<Card> newCardsOnTable = new ArrayList<Card>();
                            ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();

                            newPossibleOpponentsCard.remove(c);
                            myNewHand.add(c);

                            tempPts += simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, newPoints, newIsMyTurn, trumpForSuit);
                            k++;
                        }
                        if (k == 0) {
                            k++;
                        }
                        return tempPts / k;
                    } else { // if it is necessary to distribute the trump
                        if (!newIsMyTurn) { //if the AI has lost the round
                            ArrayList<Card> myNewHand1 = (ArrayList<Card>) myHand.clone();
                            ArrayList<Card> newCardsOnTable = new ArrayList<Card>();
                            ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();

                            //gives the trump to te AI
                            myNewHand1.add(trump);

                            return simulateMatches(myNewHand1, newCardsOnTable, newPossibleOpponentsCard, null, newPoints, false, trumpForSuit);
                        } else {// if the AI won the round
                            Double tempPts = 0d;
                            int k = 0;
                            for (Card c : possibleOpponentsCards) {

                                ArrayList<Card> myNewHand2 = (ArrayList<Card>) myHand.clone();
                                ArrayList<Card> newCardsOnTable = new ArrayList<Card>();
                                ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();

                                //Gives the trump to the opponent
                                newPossibleOpponentsCard.add(trump);

                                //gives one of the possible cards to the AI
                                newPossibleOpponentsCard.remove(c);
                                myNewHand2.add(c);

                                tempPts += simulateMatches(myNewHand2, newCardsOnTable, newPossibleOpponentsCard, null, newPoints, true, trumpForSuit);
                                k++;
                            }
                            if (k == 0) {
                                k++;
                            }
                            return tempPts / k;
                        }
                    }
                }
            }
            default: { //1 or 0 cards on table
                if (isMyTurn) {
                    int currentPts = myPoints;
                    Double tempPts = 0d;
                    int k = 0;

                    for (Card c : myHand) { //for every card in AI hand
                        ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();
                        ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                        ArrayList<Card> newCardsOnTable = (ArrayList<Card>) cardsOnTable.clone();

                        /*Card cardOnTable = null;
                        boolean isFirst = true;
                        if (newCardsOnTable.size() == 1) {
                            isFirst = false;
                            cardOnTable = newCardsOnTable.get(0);
                        }

                        int index = ruleBasedDecision(isFirst, cardOnTable, myNewHand, newPossibleOpponentsCard, trumpForSuit);

                        Card bestCard = myNewHand.get(index);*/

                        // move the card from the hand to the table
                        myNewHand.remove(c);
                        newCardsOnTable.add(c);

                        tempPts += simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, currentPts, false, trumpForSuit);
                        k++;
                    }

                    if (k == 0) {
                        k++;
                    }
                    return tempPts / k;
                } else {
                    int currentPts = myPoints;
                    /*
                    Double tempPts = 0d;
                    int k = 0;
                    for (Card c : possibleOpponentsCards) { // for every card possible in the Opponent Hand

                        ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                        ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();
                        ArrayList<Card> newCardsOnTable = (ArrayList<Card>) cardsOnTable.clone();

                        //move the card from the hand to the table
                        newPossibleOpponentsCard.remove(c);
                        newCardsOnTable.add(c);

                        tempPts += simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, currentPts, true, trumpForSuit);
                        k++;
                    }
                    return tempPts / k;*/
                    if (possibleOpponentsCards.size() == 1) {//1 card left
                        ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                        ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();
                        ArrayList<Card> newCardsOnTable = (ArrayList<Card>) cardsOnTable.clone();

                        Card cardOnTable = null;
                        boolean isFirst = true;
                        if (newCardsOnTable.size() == 1) {
                            isFirst = false;
                            cardOnTable = newCardsOnTable.get(0);
                        }

                        Card cardPlayedOpponent = newPossibleOpponentsCard.get(ruleBasedDecision(isFirst, cardOnTable, newPossibleOpponentsCard, myNewHand, trumpForSuit)); //the opponent plays its best card

                        newPossibleOpponentsCard.remove(cardPlayedOpponent);
                        newCardsOnTable.add(cardPlayedOpponent);

                        return simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, currentPts, true, trumpForSuit);
                    } else {
                        ArrayList<Card> newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                        ArrayList<Card> myNewHand = (ArrayList<Card>) myHand.clone();
                        ArrayList<Card> newCardsOnTable = (ArrayList<Card>) cardsOnTable.clone();
                        ArrayList<Card> possibleOpponentCardCopy = (ArrayList<Card>) possibleOpponentsCards.clone();

                        Double tempPts = 0d;

                        int k = 0;
                        int th = 2;
                        if (possibleOpponentsCards.size() > 5) {
                            th = 3;
                        }

                        while (k < th) {
                            Card cardOnTable = null;
                            boolean isFirst = true;
                            if (newCardsOnTable.size() == 1) {
                                isFirst = false;
                                cardOnTable = newCardsOnTable.get(0);
                            }

                            Card cardPlayedOpponent = possibleOpponentCardCopy.get(ruleBasedDecision(isFirst, cardOnTable, possibleOpponentCardCopy, myNewHand, trumpForSuit)); //the opponent plays its best card

                            possibleOpponentCardCopy.remove(cardPlayedOpponent);

                            newPossibleOpponentsCard.remove(cardPlayedOpponent);
                            newCardsOnTable.add(cardPlayedOpponent);

                            tempPts += simulateMatches(myNewHand, newCardsOnTable, newPossibleOpponentsCard, trump, currentPts, true, trumpForSuit);

                            newPossibleOpponentsCard = (ArrayList<Card>) possibleOpponentsCards.clone();
                            newCardsOnTable = (ArrayList<Card>) cardsOnTable.clone();

                            k++;
                        }
                        if (k == 0) {
                            k++;
                        }
                        return tempPts / k;
                    }

                }
            }
        }
    }

    private int calculateRoundWinner(ArrayList<Card> cardsOnTable, boolean isMineTheFirstCard, CardSuit trumpSuit) {
        int firstCardPlayedBy = isMineTheFirstCard ? 0 : 1;
        int lastCardPlayedBy = isMineTheFirstCard ? 1 : 0;

        Card cardFirstPlayer = cardsOnTable.get(0);
        Card cardSecondPlayer = cardsOnTable.get(1);

        if (cardFirstPlayer.getSuit() == cardSecondPlayer.getSuit()) { // if the suit of the two cards is the same
            if (cardFirstPlayer.getValue().getRank() < cardSecondPlayer.getValue().getRank()) { //the one with the best card in terms of rank wins the round

                return firstCardPlayedBy;

            } else {

                return lastCardPlayedBy;

            }
        } else {
            if (cardSecondPlayer.getSuit() == trumpSuit) { // if only the second player  played a trump, he wins the round

                return lastCardPlayedBy;
            } else {// any other case is won by the first player

                return firstCardPlayedBy;
            }
        }

    }

}
