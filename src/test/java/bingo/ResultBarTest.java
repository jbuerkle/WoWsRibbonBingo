package bingo;

import org.junit.jupiter.api.Test;
import ribbons.Ribbon;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultBarTest {

    @Test
    void getPointRequirementShouldReturnTwoHundredPointsForEachLevel() {
        assertEquals(200, new ResultBar(1).getPointRequirement());
        assertEquals(400, new ResultBar(2).getPointRequirement());
    }

    @Test
    void getPointRequirementShouldReturnOneHundredAndFiftyPointsForEachLevel() {
        assertEquals(550, new ResultBar(3).getPointRequirement());
        assertEquals(700, new ResultBar(4).getPointRequirement());
        assertEquals(850, new ResultBar(5).getPointRequirement());
        assertEquals(1000, new ResultBar(6).getPointRequirement());
    }

    @Test
    void getPointRequirementShouldReturnOneHundredPointsForEachLevel() {
        assertEquals(1100, new ResultBar(7).getPointRequirement());
        assertEquals(1200, new ResultBar(8).getPointRequirement());
    }

    @Test
    void getNumberOfSubsAsRewardShouldReturnCorrectAmount() {
        assertEquals(1, new ResultBar(1).getNumberOfSubsAsReward());
        assertEquals(2, new ResultBar(2).getNumberOfSubsAsReward());
        assertEquals(4, new ResultBar(3).getNumberOfSubsAsReward());
        assertEquals(8, new ResultBar(4).getNumberOfSubsAsReward());
        assertEquals(16, new ResultBar(5).getNumberOfSubsAsReward());
        assertEquals(32, new ResultBar(6).getNumberOfSubsAsReward());
        assertEquals(64, new ResultBar(7).getNumberOfSubsAsReward());
        assertEquals(128, new ResultBar(8).getNumberOfSubsAsReward());
    }

    @Test
    void getNumberOfSubsAsStringShouldReturnCorrectDisplayText() {
        assertEquals("1 sub", new ResultBar(1).getNumberOfSubsAsString());
        assertEquals("2 subs", new ResultBar(2).getNumberOfSubsAsString());
        assertEquals("4 subs", new ResultBar(3).getNumberOfSubsAsString());
    }
}
