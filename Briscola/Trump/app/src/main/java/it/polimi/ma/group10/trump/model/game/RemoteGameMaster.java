package it.polimi.ma.group10.trump.model.game;


import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polimi.ma.group10.trump.R;
import it.polimi.ma.group10.trump.controller.GameController;
import it.polimi.ma.group10.trump.model.game.enumerations.GameState;
import it.polimi.ma.group10.trump.view.activity.GameActivity;
import it.polimi.ma.group10.trump.view.dialog.StartMatchFailDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.RemoteMatchInterruptedDialogFragment;
import it.polimi.ma.group10.trump.view.dialog.SearchOpponentDialogFragment;

/**
 * This class is charged to fully manage a remote game
 */
public class RemoteGameMaster {

    public static final String TAG = "ReqTag";
    public RequestQueue gameQueue;
    private Player localPlayer;
    private String gameID;
    private Card trump, nextPlayerCard;
    private String matchUrl;
    private boolean isLocalPlayerTurn;
    private int remainingCardsInDeck;
    private int remaingCardsInRemoPlayerHand;
    private int lastCardPlayedBy;
    private ArrayList<Card> cardsOnTable;
    public ArrayList<Card> opponentPile;

    private static final RemoteGameMaster ourInstance = new RemoteGameMaster();

    /**
     * This method return true if it is the local player turn to play
     * @return true if it is the local player turn to play
     */
    public boolean isLocalPlayerTurn() {
        return isLocalPlayerTurn;
    }

    /**
     * This method returns the list of cards currently ont the table
     * @return the cards on table
     */
    public ArrayList<Card> getCardsOnTable() {
        return cardsOnTable;
    }

    /**
     * The getter method able to obtain the local player
     * @return the local player
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }

    /**
     * The getter method able to obtain the game trump card
     * @return the trump
     */
    public Card getTrump() {
        return trump;
    }

    /**
     * The getter method able to retrieve the nr of remaining cards in the deck
     * @return the # of remaining cards
     */
    public int getRemainingCardsInDeck() {
        return remainingCardsInDeck;
    }

    /**
     * The method that returns the singleton instance of the RemoteGameMaster
     * @return the singleton RemoteGameMaster Instance
     */
    public static RemoteGameMaster getInstance() {
        return ourInstance;
    }

    private RemoteGameMaster() {
    }

    /**
     * This method is charged to ask the server to start a game
     * @param room the rome where the game will be hosted
     * @param queue the queue that will manage the http requests
     * @param player the local player
     * @param gameActivity the acrivity where the game runs
     * @param dialogFragment the running dialog fragment
     * @param fragmentManager the dialog fragment manager
     */
    public void startGame(String room, RequestQueue queue, Player player, final Activity gameActivity, final SearchOpponentDialogFragment dialogFragment, final FragmentManager fragmentManager) {

        localPlayer = player;
        gameQueue = queue;
        gameQueue.start();
        gameQueue.cancelAll(TAG);
        String url = "http://mobile17.ifmledit.org/api/room/" + room;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                        try {
                            gameID = response.getString("game");
                            trump = new Card(response.getString("last_card"));

                            String hand = response.getString("cards");
                            ArrayList<Card> localPlayerHand = new ArrayList<Card>();
                            for (int i = 0; i < 3; i++) {
                                localPlayerHand.add(new Card(String.valueOf(hand.charAt(i * 2)) + String.valueOf(hand.charAt((i * 2) + 1))));
                            }
                            localPlayer.setHand(localPlayerHand);

                            if (localPlayer instanceof PlayerAI) {
                                ((PlayerAI) localPlayer).setTrump(trump);
                            }

                            isLocalPlayerTurn = response.getBoolean("your_turn");
                            matchUrl = response.getString("url");
                            remainingCardsInDeck = 34;
                            remaingCardsInRemoPlayerHand = 3;
                            GameController.getInstance().completeRemoteGameStart(gameActivity);
                            cardsOnTable = new ArrayList<Card>();
                            opponentPile = new ArrayList<Card>();

                        } catch (Exception e) {
                            Log.e("ERROR", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error response: ", error.toString());

                        dialogFragment.callDismiss();
                        String message = "Sorry, an error occourred!";
                        if (!(error instanceof NoConnectionError)) {
                            if (!(error instanceof TimeoutError)) {
                                if (error.networkResponse != null) {
                                    switch (error.networkResponse.statusCode) {
                                        case 401: {
                                            message = gameActivity.getString(R.string.room_closed);
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                message = gameActivity.getString(R.string.no_opponent_found);
                            }
                        } else {
                            message = gameActivity.getString(R.string.connection_lost);
                        }

                        StartMatchFailDialogFragment startMatchFailDialogFragment = StartMatchFailDialogFragment.newInstance(message);
                        startMatchFailDialogFragment.show(fragmentManager, "test");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "APIKey d1eb6cfd-16fd-4332-92ae-607c950a5c0b";
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }

        };
        // ripova se dopo 30 sec non ha risposta, se dopo altri 30 sec ancora niente, lancia TimeOutError
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                35000,
                2,
                1f));
        jsObjRequest.setTag(TAG);

        gameQueue.add(jsObjRequest);

    }

    /**
     * this method is able to understand the game state enumeration of the current game
     * @return the game state enumeration
     */
    public GameState getGameState() {

        int numCardsOnTable = this.cardsOnTable.size();
        int numCardsInPlayer0Hand = this.localPlayer.getHand().size();
        int numCardsInPlayer1Hand = this.remaingCardsInRemoPlayerHand;
        int numCardsInDeck = this.remainingCardsInDeck;

        if (numCardsOnTable == 2 && numCardsInPlayer0Hand == numCardsInPlayer1Hand) { //both players made their move, necessary to find round winner
            return GameState.ROUND_OVER;
        } else if (numCardsInDeck >= 2 && numCardsInPlayer0Hand == 2 && numCardsInPlayer1Hand == 2 && numCardsOnTable == 0) { //round winner declared, necessary to distribute cards
            return GameState.ROUND_WINNER_DECLARED;
        } else if (numCardsInDeck == 0 && numCardsInPlayer0Hand == 0 && numCardsInPlayer1Hand == 0 && numCardsOnTable == 0) { //the match is over, necessary to declare the winner
            return GameState.MATCH_OVER;
        } else if (Math.abs(numCardsInPlayer0Hand - numCardsInPlayer1Hand) <= 1 && numCardsInDeck % 2 == 0) {
            return GameState.PLAY;
        } else {
            return GameState.INVALID_STATE;
        }
    }

    /**
     * The method that asks the server which card has played the remoted opponent
     * @param gameActivity the acrivity where the game runs
     * @return the card played
     */
    public Card getRemotePlayerMove(final GameActivity gameActivity) {
        gameQueue.cancelAll(TAG);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, this.matchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                        Card opponentCard;
                        try {
                            if (!response.has("opponent")) {
                                response = response.getJSONObject("card");
                            }

                            opponentCard = new Card(response.getString("opponent"));
                            cardsOnTable.add(opponentCard);
                            lastCardPlayedBy = 1;
                            remaingCardsInRemoPlayerHand--;
                            if (cardsOnTable.size() == 2 && response.has("card")) {
                                nextPlayerCard = new Card(response.getString("card"));
                            } else {
                                isLocalPlayerTurn = true;
                            }

                            if (localPlayer instanceof PlayerAI) {
                                ((PlayerAI) localPlayer).removeCardFromPossibleOpponentsCards(opponentCard);
                            }


                            GameController.getInstance().completeRemotePlayerPlayCard(opponentCard, remaingCardsInRemoPlayerHand, gameActivity);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ERROR", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error response: ", error.toString());
                        String message = gameActivity.getString(R.string.general_error);
                        if (!(error instanceof NoConnectionError)) {
                            if (!(error instanceof TimeoutError)) {
                                if (error.networkResponse != null) {
                                    switch (error.networkResponse.statusCode) {
                                        case 401: {
                                            message = gameActivity.getString(R.string.wrong_match);
                                            break;
                                        }
                                        case 403: {
                                            message = gameActivity.getString(R.string.not_your_turn);
                                            break;
                                        }
                                        case 409: {
                                            message = gameActivity.getString(R.string.card_not_valid);
                                            break;
                                        }
                                        case 410: {
                                            message = gameActivity.getString(R.string.opponent_gone);
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                message = gameActivity.getString(R.string.server_off);
                            }
                        } else {
                            message = gameActivity.getString(R.string.connection_lost);
                        }

                        FragmentManager fm = gameActivity.getSupportFragmentManager();
                        RemoteMatchInterruptedDialogFragment playerLostDialogFragment = RemoteMatchInterruptedDialogFragment.newInstance(message);
                        playerLostDialogFragment.show(fm, "test");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "APIKey d1eb6cfd-16fd-4332-92ae-607c950a5c0b";
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
                    }
                    Log.i("response status code", String.valueOf(response.statusCode));
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    je.printStackTrace();
                    return Response.error(new ParseError(je));
                }
            }

        };
        // ripova se dopo 30 sec non ha risposta, se dopo altri 30 sec ancora niente, lancia TimeOutError
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                1f));
        jsObjRequest.setTag(TAG);

        gameQueue.add(jsObjRequest);
        return null;
    }

    /**
     * The method that inform the server of the local player move
     * @param card the card played
     * @param iw_card the image view containing the card played
     * @param gameActivity the acrivity where the game runs
     */
    public void playRemoteCard(final int card, final ImageView iw_card, final GameActivity gameActivity) {
        gameQueue.cancelAll(TAG);
        final Card playedCard = localPlayer.remotePerformMove(card);


        Log.i("card sent online", playedCard.toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, this.matchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                        try {
                            cardsOnTable.add(playedCard);
                            lastCardPlayedBy = 0;
                            if (cardsOnTable.size() == 2 && response.has("card")) {
                                nextPlayerCard = new Card(response.getString("card"));
                                GameController.getInstance().completeRemotePlayCard(card, iw_card, playedCard, localPlayer.getHand().size(), gameActivity);
                            } else {
                                isLocalPlayerTurn = false;
                                GameController.getInstance().completeRemotePlayCard(card, iw_card, playedCard, localPlayer.getHand().size(), gameActivity);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ERROR", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error response: ", error.toString());
                        String message = gameActivity.getString(R.string.general_error);
                        if (!(error instanceof NoConnectionError)) {
                            if (!(error instanceof TimeoutError)) {
                                if (error.networkResponse != null) {
                                    switch (error.networkResponse.statusCode) {
                                        case 401: {
                                            message = gameActivity.getString(R.string.wrong_match);
                                            break;
                                        }
                                        case 403: {
                                            message = gameActivity.getString(R.string.not_your_turn);
                                            break;
                                        }
                                        case 409: {
                                            message = gameActivity.getString(R.string.card_not_valid);
                                            break;
                                        }
                                        case 410: {
                                            message = gameActivity.getString(R.string.opponent_gone);
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                message = gameActivity.getString(R.string.server_off);
                            }
                        } else {
                            message = gameActivity.getString(R.string.connection_lost);
                        }

                        FragmentManager fm = gameActivity.getSupportFragmentManager();
                        RemoteMatchInterruptedDialogFragment playerLostDialogFragment = RemoteMatchInterruptedDialogFragment.newInstance(message);
                        playerLostDialogFragment.show(fm, "test");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "APIKey d1eb6cfd-16fd-4332-92ae-607c950a5c0b";
                headers.put("Authorization", auth);
                headers.put("content-type", "text/plain");
                return headers;
            }


            @Override
            public byte[] getBody() {
                try {
                    return playedCard.toString() == null ? null : playedCard.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
                    }

                    Log.i("response status code", String.valueOf(response.statusCode));
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    je.printStackTrace();
                    return Response.error(new ParseError(je));
                }
            }

        };
        // ripova se dopo 30 sec non ha risposta, se dopo altri 30 sec ancora niente, lancia TimeOutError
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                1f));
        jsObjRequest.setTag(TAG);

        gameQueue.add(jsObjRequest);
    }

    /**
     * The method that inform the server of the local AI move
     * @param gameActivity the acrivity where the game runs
     */
    public void playRemoteCardAI(final GameActivity gameActivity) {
        gameQueue.cancelAll(TAG);
        PlayerAI AIlocalPlayer = (PlayerAI) localPlayer;

        if (cardsOnTable.size() == 1) {
            AIlocalPlayer.setCardOnTable(cardsOnTable.get(0));
        }


        final ArrayList<Object> playedCardAndPosition = AIlocalPlayer.remotePerformMove();
        final Card playedCard = (Card) playedCardAndPosition.get(1);
        final int card = (Integer) playedCardAndPosition.get(0);


        Log.i("card sent online", playedCard.toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, this.matchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response: ", response.toString());
                        try {
                            cardsOnTable.add(playedCard);
                            lastCardPlayedBy = 0;
                            if (cardsOnTable.size() == 2 && response.has("card")) {
                                nextPlayerCard = new Card(response.getString("card"));
                                GameController.getInstance().completeRemotePlayCardAI(card, playedCard, localPlayer.getHand().size(), gameActivity);
                            } else {
                                GameController.getInstance().completeRemotePlayCardAI(card, playedCard, localPlayer.getHand().size(), gameActivity);
                                isLocalPlayerTurn = false;
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ERROR", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error response: ", error.toString());
                        String message = gameActivity.getString(R.string.general_error);
                        if (!(error instanceof NoConnectionError)) {
                            if (!(error instanceof TimeoutError)) {
                                if (error.networkResponse != null) {
                                    switch (error.networkResponse.statusCode) {
                                        case 401: {
                                            message = gameActivity.getString(R.string.wrong_match);
                                            break;
                                        }
                                        case 403: {
                                            message = gameActivity.getString(R.string.not_your_turn);
                                            break;
                                        }
                                        case 409: {
                                            message = gameActivity.getString(R.string.card_not_valid);
                                            break;
                                        }
                                        case 410: {
                                            message = gameActivity.getString(R.string.opponent_gone);
                                            break;
                                        }
                                        default: {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                message = gameActivity.getString(R.string.server_off);
                            }
                        } else {
                            message = gameActivity.getString(R.string.connection_lost);
                        }

                        FragmentManager fm = gameActivity.getSupportFragmentManager();
                        RemoteMatchInterruptedDialogFragment playerLostDialogFragment = RemoteMatchInterruptedDialogFragment.newInstance(message);
                        playerLostDialogFragment.show(fm, "test");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "APIKey d1eb6cfd-16fd-4332-92ae-607c950a5c0b";
                headers.put("Authorization", auth);
                headers.put("content-type", "text/plain");
                return headers;
            }


            @Override
            public byte[] getBody() {
                try {
                    return playedCard.toString() == null ? null : playedCard.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        response = new NetworkResponse(response.statusCode, responseData, response.headers, response.notModified);
                    }
                    Log.i("response status code", String.valueOf(response.statusCode));
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    je.printStackTrace();
                    return Response.error(new ParseError(je));
                }
            }

        };
        // ripova se dopo 30 sec non ha risposta, se dopo altri 30 sec ancora niente, lancia TimeOutError
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                1f));
        jsObjRequest.setTag(TAG);

        gameQueue.add(jsObjRequest);

    }

    /**
     * The method charged to declare the current round winner
     * @return the current round winner
     */
    public int declareRoundWinner() {


        int firstCardPlayedBy = (lastCardPlayedBy + 1) % 2;


        Card cardFirstPlayer = cardsOnTable.get(0);
        Card cardSecondPlayer = cardsOnTable.get(1);

        if (cardFirstPlayer.getSuit() == cardSecondPlayer.getSuit()) { // if the suit of the two cards is the same
            if (cardFirstPlayer.getValue().getRank() < cardSecondPlayer.getValue().getRank()) { //the one with the best card in terms of rank wins the round

                if (firstCardPlayedBy == 0) {
                    isLocalPlayerTurn = true;
                    localPlayer.claimWonRoundPrize(cardsOnTable);
                } else {
                    isLocalPlayerTurn = false;
                    for (Card c :cardsOnTable){
                        opponentPile.add(c);
                    }
                }
                cardsOnTable.clear();

                return firstCardPlayedBy;

            } else {

                if (lastCardPlayedBy == 0) {
                    isLocalPlayerTurn = true;
                    localPlayer.claimWonRoundPrize(cardsOnTable);
                } else {
                    isLocalPlayerTurn = false;
                    for (Card c :cardsOnTable){
                        opponentPile.add(c);
                    }
                }
                cardsOnTable.clear();
                return lastCardPlayedBy;

            }
        } else {
            if (cardSecondPlayer.getSuit() == trump.getSuit()) { // if only the second player  played a trump, he wins the round

                if (lastCardPlayedBy == 0) {
                    isLocalPlayerTurn = true;
                    localPlayer.claimWonRoundPrize(cardsOnTable);
                } else {
                    isLocalPlayerTurn = false;
                    for (Card c :cardsOnTable){
                        opponentPile.add(c);
                    }
                }
                cardsOnTable.clear();
                return lastCardPlayedBy;
            } else {// any other case is won by the first player

                if (firstCardPlayedBy == 0) {
                    isLocalPlayerTurn = true;
                    localPlayer.claimWonRoundPrize(cardsOnTable);
                } else {
                    isLocalPlayerTurn = false;
                    for (Card c :cardsOnTable){
                        opponentPile.add(c);
                    }
                }
                cardsOnTable.clear();
                return firstCardPlayedBy;
            }
        }

    }

    /**
     * This method is charged to distribute cards for a remote match
     */
    public void distributeCards() {
        remainingCardsInDeck -= 2;
        remaingCardsInRemoPlayerHand++;
        localPlayer.getHand().add(nextPlayerCard);

        if (remainingCardsInDeck == 0 && localPlayer instanceof PlayerAI && nextPlayerCard != trump) {
            Log.i("AAAAAAAAAAAA", "GIVEN TRUMP TO AI " + trump.toString());
            ((PlayerAI) localPlayer).possibleOpponentsCards.add(trump);
        }

    }

    /**
     * This method is charged to communicate to the server the game termination
     * @param reason the reason of the game termination
     */
    public void endRemoteGame(final String reason) {
        Log.i("leave reason", reason);

            gameQueue.cancelAll(TAG);
            gameQueue.stop();


        URL url = null;
        try {
            url = new URL(matchUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty(
                    "Content-Type", "text/plain");
            String auth = "APIKey d1eb6cfd-16fd-4332-92ae-607c950a5c0b";
            httpCon.setRequestProperty("Authorization", auth);

            httpCon.setRequestMethod("DELETE");
            OutputStreamWriter wr = new OutputStreamWriter(httpCon.getOutputStream());
            wr.write(reason);
            wr.flush();


            int statusResponse = httpCon.getResponseCode();

            Log.i("MNSDKJASLDAS", String.valueOf(statusResponse));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
