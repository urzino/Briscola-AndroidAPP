package it.polimi.ma.group10.trump;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


/**
 * This test class tests the Dealer class methods
 */
@RunWith(Enclosed.class)
public class DealerTest {

    @RunWith(value = Parameterized.class)
    public static class RoundWinnerDeclarationTest{
        private String configuration;
        private String expected;

        //From the a configuration where there are two cards on the table,
        //the current player is the one who played the last card
        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    //different suit, no trump
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.JCKS.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", "0B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHBJCKS.3S4S1S2G3BJG"},
                    //different suit, second player played trump
                    {"1S1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2S.JCKS.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", "1S1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2S..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJGJCKS"},
                    //same suit, second player played higher value
                    {"1S1G5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2SKS.JCKC.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", "1S1G5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2SKS..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJGJCKC"},
                    //same suit, second player played lower value
                    {"1S1G5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2SKS.KCJC.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.3S4S1S2G3BJG", "0S1G5C4B1BHG7C6BJS6G7G4C3C7SJBHS5B2SKS..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHBKCJC.3S4S1S2G3BJG"},

            });
        }

        public RoundWinnerDeclarationTest (String configuration, String expected){
            this.configuration = configuration;
            this.expected = expected;
        }

        @Test
        public void testRoundWinnerDeclaration(){
            TestMethods testMethods = TestMethods.getInstance();
            String result = testMethods.testRoundWinnerDeclaration(configuration);
            assertEquals("Test failed", expected, result);
        }

    }

    @RunWith(value = Parameterized.class)
    public static class CardsDistributionTest{
        private String configuration;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{

                    //player 1 won previous round
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG", "1B5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2BKC.1C3G1G.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG"},
                    //player 0 won previous round
                    {"0B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG", "0B5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B1G.1C3GKC.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG"},
                    //there are already 2 cards on the table
                    {"0B5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B.1GKC.KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG", "ERROR: There are still cards on table"},
                    //player 0 since the loop catch first the error on the first player of the list
                    {"0B5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B1G.1C3GKC.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG", "ERROR: player 0 has 3!=2 cards in its hand"},

            });
        }

        public CardsDistributionTest (String configuration, String expected){
            this.configuration = configuration;
            this.expected = expected;
        }

        @Test
        public void testCardDistribution(){
            TestMethods testMethods = TestMethods.getInstance();
            String result = testMethods.testCardDistribution(configuration);
            assertEquals("Test failed", expected, result);

        }
    }

    @RunWith(value = Parameterized.class)
    public static class MatchWinnerDeclarationTest{

        private String configuration;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"1B1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S5B..KG2B.1C3G.5S4G6S2C5GKB7B6CHCHB.JCKS3S4S1S2G3BJG", "ERROR: the match is not over, cannot declare a winner"},
                    {"0B....1S2S3S4S5S6S7SJSHSKS1B2B3B4B5B6B7BJBHBKB.1G2G3G4G5G6G7GJGHGKG1C2C3C4C5C6C7CJCHC5C", "ERROR: cumulative points at the end of the match are not 120 but 116"},
                    {"0B....1S2S3S4S5S6S7SJSHSKS1B2B3B4B5B6B7BJBHBKB.1G2G3G4G5G6G7GJGHGKG1C2C3C4C5C6C7CJCHCKC", "DRAW"},
                    {"0B....1S2S3S4S5S6S7SJSHSKS1B2B3B4B5B6B7BJBHBKB1C2C.1G2G3G4G5G6G7GJGHGKG3C4C5C6C7CJCHCKC", "WINNER071"},
                    {"0B....3S4S5S6S7SJSHSKS1B2B3B4B5B6B7BJBHBKB.1S2S1G2G3G4G5G6G7GJGHGKG1C2C3C4C5C6C7CJCHCKC", "WINNER171"},


            });
        }

        public MatchWinnerDeclarationTest (String configuration, String expected){
            this.configuration = configuration;
            this.expected = expected;
        }

        @Test
        public void testWinnerDeclaration(){
            TestMethods testMethods = TestMethods.getInstance();
            String result = testMethods.testWinnerDeclaration(configuration);
            assertEquals("Test failed", expected, result);

        }

    }
}
