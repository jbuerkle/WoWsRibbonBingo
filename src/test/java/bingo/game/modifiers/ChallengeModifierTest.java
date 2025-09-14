package bingo.game.modifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeModifierTest {

    @Test
    void getDisplayNameShouldReturnCorrectText() {
        assertEquals("Random ship restrictions", ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.getDisplayName());
        assertEquals("Increased difficulty", ChallengeModifier.INCREASED_DIFFICULTY.getDisplayName());
        assertEquals("Double difficulty increase", ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE.getDisplayName());
        assertEquals("No help", ChallengeModifier.NO_HELP.getDisplayName());
        assertEquals("No giving up", ChallengeModifier.NO_GIVING_UP.getDisplayName());
        assertEquals("No safety net", ChallengeModifier.NO_SAFETY_NET.getDisplayName());
    }

    @Test
    void getBonusModifierShouldReturnCorrectValue() {
        assertEquals(0.5, ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.getBonusModifier());
        assertEquals(0.25, ChallengeModifier.INCREASED_DIFFICULTY.getBonusModifier());
        assertEquals(0.25, ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE.getBonusModifier());
        assertEquals(0.25, ChallengeModifier.NO_HELP.getBonusModifier());
        assertEquals(0.25, ChallengeModifier.NO_GIVING_UP.getBonusModifier());
        assertEquals(0.75, ChallengeModifier.NO_SAFETY_NET.getBonusModifier());
    }

    @Test
    void getPointRequirementModifierShouldReturnCorrectValue() {
        assertEquals(0, ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.getPointRequirementModifier());
        assertEquals(0.2, ChallengeModifier.INCREASED_DIFFICULTY.getPointRequirementModifier());
        assertEquals(0.2, ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE.getPointRequirementModifier());
        assertEquals(0, ChallengeModifier.NO_HELP.getPointRequirementModifier());
        assertEquals(0, ChallengeModifier.NO_GIVING_UP.getPointRequirementModifier());
        assertEquals(0, ChallengeModifier.NO_SAFETY_NET.getPointRequirementModifier());
    }

    @Test
    void allowsNumberOfPlayersShouldReturnCorrectValueForSoloStreamerChallenge() {
        int numberOfPlayers = 1;
        assertTrue(ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.INCREASED_DIFFICULTY.allowsNumberOfPlayers(numberOfPlayers));
        assertFalse(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.NO_HELP.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.NO_GIVING_UP.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.NO_SAFETY_NET.allowsNumberOfPlayers(numberOfPlayers));
    }

    @Test
    void allowsNumberOfPlayersShouldReturnCorrectValueForDuoTrioStreamerChallenge() {
        int numberOfPlayers = 2;
        assertTrue(ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.INCREASED_DIFFICULTY.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE.allowsNumberOfPlayers(numberOfPlayers));
        assertFalse(ChallengeModifier.NO_HELP.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.NO_GIVING_UP.allowsNumberOfPlayers(numberOfPlayers));
        assertTrue(ChallengeModifier.NO_SAFETY_NET.allowsNumberOfPlayers(numberOfPlayers));
    }

    @Test
    void getAsTermShouldReturnDisplayNameWithValueOfBonusModifier() {
        assertEquals(
                "Random ship restrictions: 0.5",
                getAsTermAndConvertToString(ChallengeModifier.RANDOM_SHIP_RESTRICTIONS));
        assertEquals("Increased difficulty: 0.25", getAsTermAndConvertToString(ChallengeModifier.INCREASED_DIFFICULTY));
        assertEquals(
                "Double difficulty increase: 0.25",
                getAsTermAndConvertToString(ChallengeModifier.DOUBLE_DIFFICULTY_INCREASE));
        assertEquals("No help: 0.25", getAsTermAndConvertToString(ChallengeModifier.NO_HELP));
        assertEquals("No giving up: 0.25", getAsTermAndConvertToString(ChallengeModifier.NO_GIVING_UP));
        assertEquals("No safety net: 0.75", getAsTermAndConvertToString(ChallengeModifier.NO_SAFETY_NET));
    }

    @Test
    void getAllChallengeModifiersListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Random ship restrictions: All participating streamers get random ship restrictions, as described in [the section below](#optional-ship-restrictions), in exchange for +50% additional rewards.
                - Increased difficulty: The point requirements for each level increase by 20%, in exchange for +25% additional rewards.
                - Double difficulty increase: The point requirements for each level increase by another 20%, in exchange for +25% additional rewards. Duo/trio streamer challenge only.
                - No help: Supporters cannot join your division, in exchange for +25% additional rewards. Solo streamer challenge only.
                - No giving up: You cannot end the challenge early, in exchange for +25% additional rewards. This does not affect your ability to pause the challenge.
                - No safety net: You do not gain any extra lives, in exchange for +75% additional rewards.
                """;
        assertEquals(expectedString, ChallengeModifier.getAllChallengeModifiersListedAsString());
    }

    private String getAsTermAndConvertToString(ChallengeModifier challengeModifier) {
        return challengeModifier.getAsTerm().getAsString();
    }
}
