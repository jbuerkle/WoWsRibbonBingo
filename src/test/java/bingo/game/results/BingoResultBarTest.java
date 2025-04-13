package bingo.game.results;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoResultBarTest {

    @Test
    void getPointRequirementShouldReturnCorrectAmount() {
        assertEquals(0, new BingoResultBar(0).getPointRequirement());
        assertEquals(300, new BingoResultBar(1).getPointRequirement());
        assertEquals(500, new BingoResultBar(2).getPointRequirement());
        assertEquals(700, new BingoResultBar(3).getPointRequirement());
        assertEquals(900, new BingoResultBar(4).getPointRequirement());
        assertEquals(1200, new BingoResultBar(5).getPointRequirement());
        assertEquals(1500, new BingoResultBar(6).getPointRequirement());
        assertEquals(1800, new BingoResultBar(7).getPointRequirement());
    }

    @Test
    void getNumberOfSubsAsRewardShouldReturnCorrectAmount() {
        assertEquals(1, new BingoResultBar(0).getNumberOfSubsAsReward());
        assertEquals(2, new BingoResultBar(1).getNumberOfSubsAsReward());
        assertEquals(4, new BingoResultBar(2).getNumberOfSubsAsReward());
        assertEquals(8, new BingoResultBar(3).getNumberOfSubsAsReward());
        assertEquals(16, new BingoResultBar(4).getNumberOfSubsAsReward());
        assertEquals(32, new BingoResultBar(5).getNumberOfSubsAsReward());
        assertEquals(64, new BingoResultBar(6).getNumberOfSubsAsReward());
        assertEquals(128, new BingoResultBar(7).getNumberOfSubsAsReward());
    }

    @Test
    void getNumberOfSubsAsStringShouldReturnCorrectDisplayText() {
        assertEquals("1 sub", new BingoResultBar(0).getNumberOfSubsAsString());
        assertEquals("2 subs", new BingoResultBar(1).getNumberOfSubsAsString());
        assertEquals("4 subs", new BingoResultBar(2).getNumberOfSubsAsString());
        assertEquals("8 subs", new BingoResultBar(3).getNumberOfSubsAsString());
    }
}
