package it.polimi.ma.group10.trump;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import it.polimi.ma.group10.trump.model.game.Card;
import it.polimi.ma.group10.trump.model.game.enumerations.CardSuit;
import it.polimi.ma.group10.trump.model.game.enumerations.CardValue;
import it.polimi.ma.group10.trump.model.game.Player;

/**
 * This test class tests the Player class methods
 */
@RunWith(Enclosed.class)
public class PlayerTest {

    /**
     * test a player move in different situation of the game
     */
    @RunWith(value = Parameterized.class)
    public static class SingleMoveTest {
        private String configuration;
        private int move;
        private String expected;
        private int playerId;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            //test cases
            return Arrays.asList(new Object[][]{
                    //player0 perform move 0
                    {"0B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..JCKG2B.1CKS3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 0, "1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JC.KG2B.1CKS3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 0},
                    //player1 perform move 1
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JC.KG2B.1CKS3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 1, "1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JCKS.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 1},
                    //player 0 try to perform a move but it's not his turn
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JC.KG2B.1CKS3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 1, "ERROR: It's not player 0 turn", 0},
                    //player 1 try to perform a move but the table contains already 2 cards
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JCKS.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", 1, "ERROR: cannot play a card, table already full.", 1},
                    //player 0 try to play card 2 but in his hand has only 2 cards
                    {"0B..KG2B.1CKS.5S4G6S2C5GKB7B6CHCHBJC3G.1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B3S4S1S2G3BJG", 2, "ERROR: card 3 can't be played; only 2 cards available.", 0},

            });
        }

        public SingleMoveTest(String configuration, int move, String expected, int playerId) {
            this.configuration = configuration;
            this.move = move;
            this.expected = expected;
            this.playerId = playerId;
        }

        @Test
        public void testSingleMove() {
            TestMethods testMethods = TestMethods.getInstance();
            String result = testMethods.testSingleMove(configuration, move, playerId);
            assertEquals("Test failed", expected, result);
        }
    }

    public static class NonParametrizedTests {

        /**
         * Check if the points obtained by the player
         * are equals to the sum of the points of the cards won
         */
        @Test
        public void testClaimWonRoundPrize(){
            Player player = new Player( 0, "testPlayer");

            Card card1 = new Card(CardValue.ACE , CardSuit.BATONS);
            Card card2 = new Card(CardValue.KING , CardSuit.BATONS);

            ArrayList<Card> cardsToAdd = new ArrayList<Card>();
            cardsToAdd.add(card1);
            cardsToAdd.add(card2);

            player.claimWonRoundPrize(cardsToAdd);

            for(Card card:cardsToAdd){
                assertEquals("New Pile is wrong", true , player.getPile().contains(card));
            }

            assertEquals("Wrong points assigned", card1.getValue().getValue() + card2.getValue().getValue() ,player.getPoints() );


    }

    }

}
