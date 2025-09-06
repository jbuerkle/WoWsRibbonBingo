package bingo.game.modifiers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChallengeModifierTest {

    @Test
    void getDisplayNameShouldReturnCorrectText() {
        assertEquals("Random ship restrictions", ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.getDisplayName());
        assertEquals("Increased difficulty", ChallengeModifier.INCREASED_DIFFICULTY.getDisplayName());
    }

    @Test
    void getBonusModifierShouldReturnCorrectValue() {
        assertEquals(0.4, ChallengeModifier.RANDOM_SHIP_RESTRICTIONS.getBonusModifier());
        assertEquals(0.2, ChallengeModifier.INCREASED_DIFFICULTY.getBonusModifier());
    }

    @Test
    void getAllChallengeModifiersListedAsStringShouldReturnLongString() {
        String expectedString = """
                - Random ship restrictions: All participating streamers get random ship restrictions, as described in [the section below](#optional-ship-restrictions), in exchange for +40% additional rewards.
                - Increased difficulty: The point requirements for each level increase by 20%, in exchange for +20% additional rewards.
                - Double difficulty increase: The point requirements for each level increase by another 20%, in exchange for +20% additional rewards. Duo/trio streamer challenge only.
                - No help: Supporters cannot join your division, in exchange for +20% additional rewards. Solo streamer challenge only.
                - No giving up: You cannot end the challenge early, in exchange for +20% additional rewards. This does not affect your ability to pause the challenge.
                - No safety net: You do not gain any extra lives, in exchange for +50% additional rewards.
                """;
        assertEquals(expectedString, ChallengeModifier.getAllChallengeModifiersListedAsString());
    }
}
