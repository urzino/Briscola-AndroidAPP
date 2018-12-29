package it.polimi.ma.group10.trump.model.persistance;

import android.content.Context;

import java.util.ArrayList;

import it.polimi.ma.group10.trump.model.game.Card;
import it.polimi.ma.group10.trump.model.game.GameMaster;
import it.polimi.ma.group10.trump.model.game.GameTable;
import it.polimi.ma.group10.trump.model.game.Player;
import it.polimi.ma.group10.trump.model.game.PlayerAI;
import it.polimi.ma.group10.trump.model.game.RemoteGameMaster;
import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.enumerations.CardValue;
import it.polimi.ma.group10.trump.view.activity.GameActivity;

/**
 * This class is used to keep track of the statistics of the player
 */
public class Stats {

    private static Stats ourInstance;

    private int wins;
    private int losses;
    private int draws;
    private int loads;
    private int trumps;
    private int totalPoints;
    private Context context;

    private Stats(Context context) {
        SharedPreferencePersistence persistance = SharedPreferencePersistence.getInstance(context);

        ArrayList<Integer> stats = persistance.getStatistics();
        this.wins = stats.get(0);
        this.losses = stats.get(1);
        this.draws = stats.get(2);
        this.trumps = stats.get(3);
        this.loads = stats.get(4);
        this.totalPoints = stats.get(5);
    }

    public static Stats getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new Stats(context);
        return ourInstance;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLoads() {
        return loads;
    }

    public void setLoads(int loads) {
        this.loads = loads;
    }

    public int getTrumps() {
        return trumps;
    }

    public void setTrumps(int trumps) {
        this.trumps = trumps;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * This method calculates the total games played by the player
     * @return
     */
    public int totalGames() {
        return wins + losses + draws;
    }

    /**
     * This method calculates the trumps that a player has in average on each game
     * @return
     */
    public float trumpsPerGame() {
        if (totalGames() == 0) return 0;
        float res = (float) trumps / (float) (totalGames());
        return Math.round(res * 10) / 10f;
    }

    /**
     * This method calculates the average number of loads obtained by a player at the end of a match
     * @return
     */
    public float loadsPerGame() {
        if (totalGames() == 0) return 0;
        float res = (float) loads / (float) (totalGames());
        return Math.round(res * 10) / 10f;
    }

    /**
     * This method calculates the average points obtained by a player at the end of a match
     * @return
     */
    public float pointsPerGame() {
        if (totalGames() == 0) return 0;
        float res = (float) totalPoints / (float) totalGames();
        return Math.round(res * 10) / 10f;
    }

    /**
     * This method saves the statistics of the player in memory
     * @param winnerId
     * @param gameActivity
     */
    public void saveStats(int winnerId, GameActivity gameActivity) {
        Player player0;
        CardSuit trumpSuit;
        ArrayList<Card> playerPile;
        if (gameActivity.modality == 0) {
            player0 = GameMaster.getInstance().getPlayers().get(0);
            if (player0 instanceof PlayerAI){return;}
            trumpSuit = GameTable.getInstance().getTrump();
        } else {
            RemoteGameMaster remoteGameMaster = RemoteGameMaster.getInstance();
            trumpSuit = remoteGameMaster.getTrump().getSuit();
            player0 = remoteGameMaster.getLocalPlayer();
        }
        //update stats only if match is played by a real player
        if(player0 instanceof PlayerAI){return;}

        totalPoints += player0.calculatePoints();
        playerPile = player0.getPile();
        if (winnerId == 0) {
            wins++;
        } else if (winnerId == 1) {
            losses++;
        } else {
            draws++;
        }
        analyzePile(playerPile, trumpSuit);
        SharedPreferencePersistence persistance = SharedPreferencePersistence.getInstance(context);
        persistance.saveStatistic(this);
    }

    /**
     * This method calculates the loads and the trumps obtained by a player during the match
     * by analyzing the player's pile at the end of the match
     * @param pile
     * @param trump
     */
    private void analyzePile(ArrayList<Card> pile, CardSuit trump) {
        for (int i = 0; i < pile.size(); i++) {
            CardValue value = pile.get(i).getValue();
            CardSuit suit = pile.get(i).getSuit();
            if (value == CardValue.ACE || value == CardValue.THREE)
                this.loads++;
            if (suit == trump)
                this.trumps++;

        }
    }


}
