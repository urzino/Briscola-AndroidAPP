package it.polimi.ma.group10.trump;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

/**
 * This test class tests the GameMaster class methods
 */
@RunWith(Enclosed.class)
public class GameMasterTest {

    /**
     * Test game built from a configuration string
     */
    @RunWith(Parameterized.class)
    public static class ConfigurationFromStringTest{
        private String configuration;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            //test cases
            return Arrays.asList(new Object[][]{
                    //Initial state
                    {"0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KB6CHG.7B1G3S..", "0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KB6CHG.7B1G3S.."},
                    //1st player played
                    {"1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C.KBHG.7B1G3S..", "1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C.KBHG.7B1G3S.."},
                    //2nd player played
                    {"1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C3S.KBHG.7B1G..", "1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C3S.KBHG.7B1G.."},
                    //round winner declared
                    {"0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG.7B1G.6C3S.", "0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG.7B1G.6C3S."},
                    //card distributed
                    {"0G1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG2S.7B1G6S.6C3S.", "0G1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG2S.7B1G6S.6C3S."},
                    //empty deck reached
                    {"1B..JBHSHB.JS6GKB.HC1B4BKG4C3S5S7BJG6B2C2G.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "1B..JBHSHB.JS6GKB.HC1B4BKG4C3S5S7BJG6B2C2G.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S"},
                    //2 card for each player, empty deck
                    {"0B..JBHS.JSKB.HC1B4BKG4C3S5S7BJG6B2C2G6GHB.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "0B..JBHS.JSKB.HC1B4BKG4C3S5S7BJG6B2C2G6GHB.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S"},
                    //1 card for each player, empty deck
                    {"0B..JB.KB.HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "0B..JB.KB.HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S"},
                    //end of match before winner declaration
                    {"1B....HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1SJBKB", "1B....HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1SJBKB"},
                    {"0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3G..5C", "ERROR: Configuration string length is 89(87 expected)"},
                    {"0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5B..JCKG2B.1CKS3...", "ERROR: Configuration string doesn't respect the format"},
                    {"0B5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3B5BJG..JCKG2B.1CKS3G..", "ERROR: Mismatch between given trump(BATONS) and last deck card trump(GOLDS)"},



            });
        }

        public ConfigurationFromStringTest(String configuration, String expected){
            this.configuration = configuration;
            this.expected = expected;
        }

        @Test
        public void testDeckCreation() {
            assertEquals("Test failed", expected, TestMethods.getInstance().testConfigurationFromString(configuration));
        }

    }

    @RunWith(Parameterized.class)
    /**
     * test all the possible game state of the application
     */
    public static class GetGameStateTest{
        private String configuration;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            //test cases
            return Arrays.asList(new Object[][]{
                    //Initial state
                    {"0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KB6CHG.7B1G3S..", "PLAY"},
                    //1st player played
                    {"1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C.KBHG.7B1G3S..", "PLAY"},
                    //2nd player played
                    {"1G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G.6C3S.KBHG.7B1G..", "ROUND_OVER"},
                    //round winner declared
                    {"0G2S6S1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG.7B1G.6C3S.", "ROUND_WINNER_DECLARED"},
                    //card distributed
                    {"0G1S4C5S4S3GHC6B7GJG5C4GJB2C7S1BHS3CHB7CKG5G1CJC3B5B2GJSKSKC2B4B6G..KBHG2S.7B1G6S.6C3S.", "PLAY"},
                    //empty deck reached
                    {"1B..JBHSHB.JS6GKB.HC1B4BKG4C3S5S7BJG6B2C2G.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "PLAY"},
                    //2 card for each player, empty deck
                    {"0B..JBHS.JSKB.HC1B4BKG4C3S5S7BJG6B2C2G6GHB.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "PLAY"},
                    //1 card for each player, empty deck
                    {"0B..JB.KB.HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1S", "PLAY"},
                    //end of match before winner declaration
                    {"1B....HC1B4BKG4C3S5S7BJG6B2C2G6GHBHSJS.JC5B1C2BKCHGKS4S6S1G4G6C5C3C3B2S7C5G7S3G7G1SJBKB", "MATCH_OVER"},
                    //invalid: due to difference between cards
                    {"1G..5B3GKG.HB.4C2GHG1S6CJSKS2B4G1B7S7C5S4B6B3B.7BHS3SKCJC4SJB7G1GJG6G1CKB5C6SHC2C3C2S5G", "INVALID_STATE"},
                    //invalid: odd cards inside the deck
                    {"1G2G..5B3GKG.4CHB.HG1S6CJSKS2B4G1B7S7C5S4B6B3B.7BHS3SKCJC4SJB7G1GJG6G1CKB5C6SHC2C3C2S5G", "INVALID_STATE"},
                    //invalid: 1 should play, player 1 has already 1 card less than player one
                    {"1G.5B.3GKG.HB.4C2GHG1S6CJSKS2B4G1B7S7C5S4B6B3B.7BHS3SKCJC4SJB7G1GJG6G1CKB5C6SHC2C3C2S5G", "INVALID_STATE"}
            });
        }

        public GetGameStateTest(String configuration, String expected){
            this.configuration = configuration;
            this.expected = expected;
        }

        @Test
        public void testDeckCreation() {
            assertEquals("Test failed", expected, TestMethods.getInstance().testGetGameState(configuration));
        }

    }

    public static class NonParametrizedTests{

        /**
         * test initial state, the moment before the first player start its first turn
         */
        @Test
        public void testInitialState(){
            TestMethods testMethods = TestMethods.getInstance();
            String result = testMethods.initialStateTest();
            assertEquals("The initial state is wrong" , result , "OK");
        }

    }



}
