package it.polimi.ma.group10.trump.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.model.game.Card;
import it.polimi.ma.group10.trump.model.game.Dealer;
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.GameTable;
import it.polimi.ma.group10.trump.model.game.Player;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.game.RemoteGameMaster;
import it.polimi.ma.group10.trump.model.persistance.SharedPreferencePersistence;
import it.polimi.ma.group10.trump.model.persistance.Stats;
import it.polimi.ma.group10.trump.model.game.enumerations.GameState;
import it.polimi.ma.group10.trump.model.game.exceptions.dealer.DealerException;
import it.polimi.ma.group10.trump.model.game.exceptions.deck.DeckException;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerErrorCode;
import it.polimi.ma.group10.trump.model.game.exceptions.player.PlayerException;
import it.polimi.ma.group10.trump.view.activity.GameActivity;
import it.polimi.ma.group10.trump.view.dialog.SearchOpponentDialogFragment;

/**
 * This class is responsible for the communication between the model and the view,
 * it asks to the model to perform actions and tells the view what to show
 */
public class GameController {
    private static final GameController ourInstance = new GameController();

    public static GameController getInstance() {
        return ourInstance;
    }

    private GameController() {
    }

    /**
     * This method is charged to, accordignly to the game situation, decide what to do to move on
     * @param gameActivity the activity where the game is running
     */
    public void moveForwardWithTheMatch(final GameActivity gameActivity) {
        if (gameActivity.modality == 0) {
            moveForwardWithLocalMatch(gameActivity);
        } else {
            moveForwardWithRemoteMatch(gameActivity);
        }

    }

    /**
     * This method is charged to decide what to do when the game runs locally
     * @param gameActivity the activity where the game is running
     */
    public void moveForwardWithLocalMatch(final GameActivity gameActivity) {
        Log.i("Game status", GameMaster.getInstance().gameConfigurationToString());
        try {

            GameState currentState = GameMaster.getInstance().getGameState();
            switch (currentState) {
                case ROUND_OVER: {

                    assignRoundToWinner(gameActivity);
                    break;

                }
                case ROUND_WINNER_DECLARED: {

                    distributeCards(gameActivity);
                    break;

                }
                case MATCH_OVER: {

                    declareMatchWinner(gameActivity);
                    return;

                }
                case PLAY: {

                    if (GameTable.getInstance().getCurrentPlayer() == 1) {

                        playCardOpponent(gameActivity);

                    } else {

                        Player player0 = GameMaster.getInstance().getPlayers().get(0);

                        if (player0 instanceof PlayerAI) {
                            playCardAI(gameActivity);
                        } else {

                            //Handler mainHandler = new Handler(gameActivity.getMainLooper());

                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    gameActivity.showYourTurnDialog();
                                    gameActivity.isTimeToPlay = true;
                                    Log.i("player can", " play");
                                }
                            };

                            gameActivity.UIManagementHandler.post(myRunnable);
                        }
                    }
                    break;

                }
                case INVALID_STATE: {
                    break;
                }


            }
        } catch (DealerException e) {
            Log.i("error: ", (String) e.getProperties().get("message"));
        } catch (DeckException e) {
            Log.i("error: ", (String) e.getProperties().get("message"));
        } catch (PlayerException e) {
            Log.i("error: ", (String) e.getProperties().get("message"));
        }

    }

    /**
     * This method is charged to call the model and ask a play card and
     * after that it will call the view and ask to show the card played
     * @param card the played card index
     * @param iw_card the image view where the card is located
     * @param gameActivity the activity where the game is running
     */
    public void playCard(final int card, final ImageView iw_card, final GameActivity gameActivity) {
        GameMaster gameMaster = GameMaster.getInstance();

        if (gameMaster.getGameState() != GameState.PLAY) return;

        Player player = gameMaster.getPlayers().get(0);
        Card playedCard = null;
        try {
            playedCard = player.performMove(card);
        } catch (PlayerException e) {
            Log.i("error: ", (String) e.getProperties().get("message"));
            if (e.getErrorCode() == PlayerErrorCode.CARD_NOT_IN_HAND) {
                moveForwardWithTheMatch(gameActivity);
            }
            return;
        }
        Log.i("played user: ", playedCard.toString());

        final int remCards = player.getHand().size();
        final Card cardP = playedCard;

        Player opponent = gameMaster.getPlayers().get(1);
        if (opponent instanceof PlayerAI) {
            ((PlayerAI) opponent).removeCardFromPossibleOpponentsCards(cardP);
        }


        // Handler mainHandler = new Handler(gameActivity.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCard(card, iw_card, cardP, remCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);

    }

    /**
     * This method is charged to make the AI to play a card and do show the move in the view
     * @param gameActivity the activity where the game is running
     * @throws PlayerException in case the played card is not allowed
     */
    public void playCardAI(final GameActivity gameActivity) throws PlayerException {

        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();

        if (gameMaster.getGameState() != GameState.PLAY) return;

        PlayerAI player = (PlayerAI) gameMaster.getPlayers().get(0);


        if (gameTable.getCardsOnTable().size() == 1) {
            player.setCardOnTable(gameTable.getCardsOnTable().get(0));
        }


        ArrayList<Object> playedCardAndPosition;

        playedCardAndPosition = player.performMove();

        Log.i("played user: ", playedCardAndPosition.toString());

        final int remCards = player.getHand().size();
        final Card cardP = (Card) playedCardAndPosition.get(1);
        final int card = (Integer) playedCardAndPosition.get(0);


        Player opponent = gameMaster.getPlayers().get(1);
        if (opponent instanceof PlayerAI) {
            ((PlayerAI) opponent).removeCardFromPossibleOpponentsCards(cardP);
        }

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCardForAI(card, cardP, remCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);

    }

    /**
     * This method is charged to ask the Dealer to distribute the cards and show the distribution in the view
     * @param gameActivity the activity where the game is running
     * @throws DeckException if the deck is badly managed
     * @throws DealerException if the dealer does something not allowed
     */
    public void distributeCards(final GameActivity gameActivity) throws DeckException, DealerException {

        final Card cardForPl0Temp;
        final int cardsInDeckTemp;
        final int previousRoundWinnerTemp;

        if (gameActivity.modality == 0) {
            Dealer dealer = Dealer.getInstance();
            dealer.distributeCards();
            cardForPl0Temp = GameMaster.getInstance().getPlayers().get(0).getHand().get(2);
            cardsInDeckTemp = GameTable.getInstance().getDeck().getCards().size();
            previousRoundWinnerTemp = GameTable.getInstance().getFirstPlayerCurrentRound();
        } else {
            RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
            remoteGameMaster.distributeCards();
            cardForPl0Temp = remoteGameMaster.getLocalPlayer().getHand().get(2);
            cardsInDeckTemp = remoteGameMaster.getRemainingCardsInDeck();
            previousRoundWinnerTemp = remoteGameMaster.isLocalPlayerTurn() ? 0 : 1;
        }


        final Card cardForPl0 = cardForPl0Temp;
        final int cardsInDeck = cardsInDeckTemp;
        final int previousRoundWinner = previousRoundWinnerTemp;


        Log.i("dealer action: ", "cards distributed");

        //Handler mainHandler = new Handler(gameActivity.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.distributeCards(cardForPl0, cardsInDeck, previousRoundWinner);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);

    }

    /**
     * This method is charged to ask the dealer the winner declaration and shows it in the view
     * @param gameActivity the activity where the game is running
     * @throws DealerException if the Dealer performs something wrong
     */
    public void declareMatchWinner(final GameActivity gameActivity) throws DealerException {
        int winner;
        int winnerPointsTemp;
        if (gameActivity.modality == 0) {
            Dealer dealer = Dealer.getInstance();


            Player player = dealer.declareWinner();

            if (player == null) {
                winner = -1;
                winnerPointsTemp = 60;
            } else {

                winner = player.getId();
                winnerPointsTemp = player.getPoints();
            }

        } else {
            RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
            int localPlayerPoints = remoteGameMaster.getLocalPlayer().getPoints();
            if (localPlayerPoints > 60) {
                winner = 0;
                winnerPointsTemp = localPlayerPoints;
            } else if (localPlayerPoints < 60) {
                winner = 1;
                winnerPointsTemp = 120 - localPlayerPoints;
            } else {
                winner = -1;
                winnerPointsTemp = 60;
            }
            remoteGameMaster.endRemoteGame("terminated");

        }

        //
        Stats stats = Stats.getInstance(gameActivity.getApplicationContext());

        final int winnerId = winner;

        //Handler mainHandler = new Handler(gameActivity.getMainLooper());

        stats.saveStats(winner, gameActivity);

        if (winner == 0) {
            final int winnerPoints = winnerPointsTemp;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    gameActivity.declareMatchWinner(winnerId, winnerPoints);
                    Log.i("dealer action: ", "Player " + winnerId + " won with " + winnerPoints + " points");
                }
            };

            gameActivity.UIManagementHandler.post(myRunnable);

        } else if (winner == 1) {
            final int winnerPoints = winnerPointsTemp;
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    gameActivity.declareMatchWinner(winnerId, 120 - winnerPoints);
                    Log.i("dealer action: ", "Player " + winnerId + "won with " + winnerPoints + " points");
                }
            };

            gameActivity.UIManagementHandler.post(myRunnable);


        } else {
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    gameActivity.declareMatchWinner(winnerId, 60);
                    Log.i("dealer action: ", "Player " + winnerId + "draw with " + 60 + " points");
                }
            };

            gameActivity.UIManagementHandler.post(myRunnable);


        }
    }

    /**
     * This method is charged to ask the Dealer the round winner and to show it in the view
     * @param gameActivity the activity where the game is running
     * @throws DealerException if the Dealer performs something wrong
     */
    public void assignRoundToWinner(final GameActivity gameActivity) throws DealerException {
        int winnerTemp;
        if (gameActivity.modality == 0) {
            Dealer dealer = Dealer.getInstance();
            winnerTemp = dealer.declareRoundWinner().getId();
        } else {
            winnerTemp = RemoteGameMaster.getInstance().declareRoundWinner();
        }

        final int winner = winnerTemp;
        Log.i("dealer action: ", "Player " + winner + " won the round");


        //Handler mainHandler = new Handler(gameActivity.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.assignRoundToWinner(winner);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);


    }

    /**
     * This method is charged to ask the opponent to perform his move and to show the move in the view
     * @param gameActivity the activity where the game is running
     * @throws PlayerException if the opponent Player performs something wrong
     */
    public void playCardOpponent(final GameActivity gameActivity) throws PlayerException {

        GameMaster gameMaster = GameMaster.getInstance();
        GameTable gameTable = GameTable.getInstance();
        PlayerAI player = (PlayerAI) gameMaster.getPlayers().get(1);

        if (gameTable.getCardsOnTable().size() == 1) {
            player.setCardOnTable(gameTable.getCardsOnTable().get(0));
        }


        Card playedCard1 = (Card) player.performMove().get(1);

        final Card playedCard = playedCard1;
        final int remCards = player.getHand().size();

        Log.i("played android: ", playedCard.toString());


        Player opponent = gameMaster.getPlayers().get(0);
        if (opponent instanceof PlayerAI) {
            ((PlayerAI) opponent).removeCardFromPossibleOpponentsCards(playedCard);
        }

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCardOpponent(playedCard, remCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);


    }

    /**
     * This method is charged to start a single player game and to show the initialization in the view
     * @param activity the activity asking the game to start
     */
    public void startSinglePlayerGame(Activity activity) {
        GameMaster gm = GameMaster.getInstance();

        Player player1 = new Player(0, "bob");
        PlayerAI player2 = new PlayerAI(1, "Android", SharedPreferencePersistence.getInstance(activity).getGameDifficulty());

        try {
            gm.startGame(player1, player2);
            ArrayList<Card> deck = GameTable.getInstance().getDeck().getCards();
            player2.setTrump(deck.get(deck.size() - 1));
        } catch (DeckException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("modality", 0);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * This method is charged to start a ai vs ai game and to show the initialization in the view
     * @param activity the activity asking the game to start
     */
    public void startAiVsAiGame(Activity activity) {


        GameMaster gm = GameMaster.getInstance();

        PlayerAI player1 = new PlayerAI(0, "Android1", SharedPreferencePersistence.getInstance(activity).getGameDifficulty());
        PlayerAI player2 = new PlayerAI(1, "Android2", SharedPreferencePersistence.getInstance(activity).getGameDifficulty());

        try {
            gm.startGame(player1, player2);
            ArrayList<Card> deck = GameTable.getInstance().getDeck().getCards();
            player1.setTrump(deck.get(deck.size() - 1));
            player2.setTrump(deck.get(deck.size() - 1));
        } catch (DeckException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("modality", 0);
        activity.startActivity(intent);
        activity.finish();

    }

    /**
     * This method is charged to decide what to do when the game runs remotly
     * @param gameActivity the activity where the game is running
     */
    public void moveForwardWithRemoteMatch(final GameActivity gameActivity) {

        RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
        GameState gameState = remoteGameMaster.getGameState();

        switch (gameState) {
            case ROUND_OVER: {
                try {
                    assignRoundToWinner(gameActivity);
                } catch (DealerException e) {
                    e.printStackTrace();
                }
                Log.i("state", "round over");
                break;
            }
            case ROUND_WINNER_DECLARED: {
                Log.i("state", "round winner declared");
                try {
                    distributeCards(gameActivity);
                } catch (DeckException e) {
                    e.printStackTrace();
                } catch (DealerException e) {
                    e.printStackTrace();
                }
                break;
            }
            case MATCH_OVER: {
                Log.i("state", "match over");
                try {
                    declareMatchWinner(gameActivity);
                } catch (DealerException e) {
                    e.printStackTrace();
                }
                break;
            }
            case PLAY: {
                Log.i("state", "case play");
                if (remoteGameMaster.isLocalPlayerTurn()) {
                    Player player = remoteGameMaster.getLocalPlayer();
                    if (player instanceof PlayerAI) {
                        remotePlayCardAI(gameActivity);
                    } else {
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                gameActivity.showYourTurnDialog();
                                gameActivity.isTimeToPlay = true;
                                Log.i("player can", " play");
                            }
                        };

                        gameActivity.UIManagementHandler.post(myRunnable);
                    }

                } else {
                    remotePlayCardOpponent(gameActivity);
                }

                break;
            }
            case INVALID_STATE: {
                Log.i("state", "invalid");
                break;
            }
        }

    }

    /**
     * This method is charged to start a remote match
     * @param activity the activity that performs the request
     * @param room the room where the match will be hosted remotely
     * @param queue the queue that will manage the http requests
     * @param mode the game modality
     * @param df the running dialog fragment
     * @param fragmentManager the dialog fragment manager
     */
    public void startRemoteGame(Activity activity, String room, RequestQueue queue, int mode, SearchOpponentDialogFragment df, android.support.v4.app.FragmentManager fragmentManager) {


        RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
        Player p;
        if (mode == 0) {
            p = new Player(0, "bob");
        } else {
            p = new PlayerAI(0, "android", SharedPreferencePersistence.getInstance(activity).getGameDifficulty());
        }

        remoteGameMaster.startGame(room, queue, p, activity, df, fragmentManager);


    }

    /**
     * This method is charged to show the initial situation of the game after the http request
     * of starting the game has been solved
     * @param activity the activity performing the request
     */
    public void completeRemoteGameStart(Activity activity) {
        Intent intent = new Intent(activity, GameActivity.class);
        intent.putExtra("modality", 1);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * this method is charged to ask remotely for the opponent move
     * @param gameActivity the activity where the game is running
     */
    public void remotePlayCardOpponent(GameActivity gameActivity) {
        RemoteGameMaster.getInstance().getRemotePlayerMove(gameActivity);
    }

    /**
     * This method is charged to show in the view the move that the remote player performed
     * @param card the card played
     * @param remainingCards the remaining cards in the opponent hand
     * @param gameActivity the activity where the game is running
     */
    public void completeRemotePlayerPlayCard(final Card card, final int remainingCards, final GameActivity gameActivity) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCardOpponent(card, remainingCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);
    }

    /**
     * This method is charged to inform the server of the local player move
     * @param card the card index played
     * @param iw_card the image view containing the played card
     * @param gameActivity the activity where the game is running
     */
    public void remotePlayCard(final int card, final ImageView iw_card, final GameActivity gameActivity) {
        RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
        remoteGameMaster.playRemoteCard(card, iw_card, gameActivity);
    }

    /**
     * This method is charged to ask to the local AI which card to play and communicate the decision to the server
     * @param gameActivity the activity where the game is running
     */
    public void remotePlayCardAI(GameActivity gameActivity) {
        RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
        remoteGameMaster.playRemoteCardAI(gameActivity);
    }

    /**
     * This method is charged to show the player move when the server ack it
     * @param card the played card index
     * @param iw_card the played card image view container
     * @param cardP the played card
     * @param remCards the nr of remaining cards
     * @param gameActivity the activity where the game is running
     */
    public void completeRemotePlayCard(final int card, final ImageView iw_card, final Card cardP, final int remCards, final GameActivity gameActivity) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCard(card, iw_card, cardP, remCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);
    }

    /**
     * This method is charged to show the ai move whed the server ack it
     * @param card the played card idx
     * @param cardP the palyed card
     * @param remCards the nr of remaining cards
     * @param gameActivity the activity where the game is running
     */
    public void completeRemotePlayCardAI(final int card, final Card cardP, final int remCards, final GameActivity gameActivity) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.playCardForAI(card, cardP, remCards);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);
    }

    /**
     * This method aks to an ai which is the move it would do and communicates it to the view
     * @param gameActivity the activity where the game is running
     */
    public void getSuggestion(final GameActivity gameActivity) {

        PlayerAI suggestingPlayer = new PlayerAI(2, "suggester", 1);

        Player currentPlayer;
        Card cardOnTable = null;
        Card trump;
        ArrayList<Card> possibleOpponentsCard = suggestingPlayer.possibleOpponentsCards;


        if (gameActivity.modality == 0) {

            GameMaster gameMaster = GameMaster.getInstance();
            GameTable gameTable = GameTable.getInstance();
            currentPlayer = gameMaster.getPlayers().get(0);
            if (gameTable.getCardsOnTable().size() == 1) {
                cardOnTable = gameTable.getCardsOnTable().get(0);
                possibleOpponentsCard.remove(cardOnTable);
            }

            trump = gameTable.getTrumpCard();

            for (Card c : currentPlayer.getHand()) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }
            for (Card c : currentPlayer.getPile()) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }
            for (Card c : gameMaster.getPlayers().get(1).getPile()) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }
            possibleOpponentsCard.remove(trump);

            if (gameMaster.getPlayers().get(1).getHand().contains(trump)) {
                possibleOpponentsCard.add(trump);
            }


        } else {

            RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
            currentPlayer = remoteGameMaster.getLocalPlayer();
            if (remoteGameMaster.getCardsOnTable().size() == 1) {
                cardOnTable = remoteGameMaster.getCardsOnTable().get(0);
                possibleOpponentsCard.remove(cardOnTable);
            }
            trump = remoteGameMaster.getTrump();
            for (Card c : currentPlayer.getHand()) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }
            for (Card c : currentPlayer.getPile()) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }
            for (Card c : remoteGameMaster.opponentPile) {
                if (c != trump) {
                    possibleOpponentsCard.remove(c);
                }
            }

            possibleOpponentsCard.remove(trump);

            if (remoteGameMaster.getRemainingCardsInDeck() == 0 && !currentPlayer.getHand().contains(trump) && !currentPlayer.getPile().contains(trump)
                    && !remoteGameMaster.opponentPile.contains(trump) && cardOnTable != trump
                    ) {
                possibleOpponentsCard.add(trump);
            }

        }
        suggestingPlayer.setPoints(0);


        final int suggestion = suggestingPlayer.pickUpBestCard(cardOnTable, (ArrayList<Card>) currentPlayer.getHand().clone(), 1, possibleOpponentsCard, trump);

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                gameActivity.showSuggestion(suggestion);
            }
        };

        gameActivity.UIManagementHandler.post(myRunnable);
    }

}
