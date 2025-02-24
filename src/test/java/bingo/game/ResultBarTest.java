package bingo.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultBarTest {

    @Test
    void getPointRequirementShouldReturnZeroPointsForEachLevel() {
        assertEquals(0, new ResultBar(0).getPointRequirement());
    }

    @Test
    void getPointRequirementShouldReturnFourHundredPointsForEachLevel() {
        assertEquals(400, new ResultBar(1).getPointRequirement());
    }

    @Test
    void getPointRequirementShouldReturnTwoHundredPointsForEachLevel() {
        assertEquals(600, new ResultBar(2).getPointRequirement());
        assertEquals(800, new ResultBar(3).getPointRequirement());
    }

    @Test
    void getPointRequirementShouldReturnOneHundredAndFiftyPointsForEachLevel() {
        assertEquals(950, new ResultBar(4).getPointRequirement());
        assertEquals(1100, new ResultBar(5).getPointRequirement());
        assertEquals(1250, new ResultBar(6).getPointRequirement());
        assertEquals(1400, new ResultBar(7).getPointRequirement());
    }

    @Test
    void getNumberOfSubsAsRewardShouldReturnCorrectAmount() {
        assertEquals(1, new ResultBar(0).getNumberOfSubsAsReward());
        assertEquals(2, new ResultBar(1).getNumberOfSubsAsReward());
        assertEquals(4, new ResultBar(2).getNumberOfSubsAsReward());
        assertEquals(8, new ResultBar(3).getNumberOfSubsAsReward());
        assertEquals(16, new ResultBar(4).getNumberOfSubsAsReward());
        assertEquals(32, new ResultBar(5).getNumberOfSubsAsReward());
        assertEquals(64, new ResultBar(6).getNumberOfSubsAsReward());
        assertEquals(128, new ResultBar(7).getNumberOfSubsAsReward());
    }

    @Test
    void getNumberOfSubsAsStringShouldReturnCorrectDisplayText() {
        assertEquals("1 sub", new ResultBar(0).getNumberOfSubsAsString());
        assertEquals("2 subs", new ResultBar(1).getNumberOfSubsAsString());
        assertEquals("4 subs", new ResultBar(2).getNumberOfSubsAsString());
        assertEquals("8 subs", new ResultBar(3).getNumberOfSubsAsString());
    }
}
