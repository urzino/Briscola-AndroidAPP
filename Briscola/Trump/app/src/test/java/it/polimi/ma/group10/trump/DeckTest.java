package it.polimi.ma.group10.trump;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * This test class tests the Deck class methods
 */
@RunWith(Enclosed.class)
public class DeckTest {

    @RunWith(Parameterized.class)
    public static class DeckCreationTest {
        private String deck;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    //Legal creation
                    {"1B3SKGJC", "1B3SKGJC"},
                    //Odd elements in the string
                    {"1B3SKGJCD", "ERROR: String of the deck invalid"},
                    //Too many elements in the string
                    {"1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1B3SKGJC1G", "ERROR: String of the deck invalid"}

            });
        }

        public DeckCreationTest(String deck, String expected) {
            this.deck = deck;
            this.expected = expected;

        }

        @Test
        public void testDeckCreation() {
            assertEquals("Wrong deck creation behaviour", expected, TestMethods.getInstance().testDeckCreation(deck));
        }
    }


    @RunWith(Parameterized.class)
    public static class DeckDrawTest {
        private String deck;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    //Card can be drawn only if the deck is not empty
                    {"1B3SKGJC", "3SKGJC 1B"},
                    //no cards in the deck
                    {"", "ERROR: there are no cards inside the deck"},

            });
        }

        public DeckDrawTest(String deck, String expected) {
            this.deck = deck;
            this.expected = expected;
        }

        @Test
        public void testDeckDraw() {
            assertEquals("Wrong deck draw behaviour", expected, TestMethods.getInstance().testDeckDraw(deck));
        }
    }

    /**
     * Test the drawn of a trump:
     * - the trump should be drawn only after the distribution of the  6th cards (starting from a full deck
     */
    @RunWith(Parameterized.class)
    public static class DeckDrawTrumpTest {
        private String deck;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"5S4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"4G6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"6S2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"2C5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"5GKB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"KB7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"7B6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G7B 7B"}, //the new deck + space + the trump
                    {"6CHCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
                    {"HCHB1GKC5C4B1BHG7C6BJS6G7G4C3C7SJBHS2S3S4S1S2G3BJG5BJCKG2B1CKS3G", "ERROR: attempt to draw the trump in a wrong moment of the game"},
            });
        }

        public DeckDrawTrumpTest(String deck, String expected) {
            this.deck = deck;
            this.expected = expected;
        }


        @Test
        public void testDeckDrawTrump() {
            assertEquals("Wrong trump draw behaviour", expected, TestMethods.getInstance().testDeckDrawTrump(deck));
        }
    }

    /**
     * Test card creation from different input string
     */
    @RunWith(Parameterized.class)
    public static class CardCreationTest {
        private String card;
        private String expected;

        @Parameterized.Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"1B", "1B"},
                    {"4C", "4C"},
                    {"HG", "HG"},
                    {"KS", "KS"},
                    {"1B", "1B"},
                    {"1BC", "ERROR: Card string length too long"},
                    {"KD", "ERROR: D is an illegal suit character"},
                    {"FB", "ERROR: F is an illegal value character"}
            });
        }

        public CardCreationTest(String card, String expected) {
            this.card = card;
            this.expected = expected;
        }

        /**
         * Test the drawn of a card:
         * - Card can be drawn only if the deck is not empty
         */
        @Test
        public void testCardCreation() {
            assertEquals("Wrong card creation", expected, TestMethods.getInstance().testCardCreation(card));
        }
    }
}