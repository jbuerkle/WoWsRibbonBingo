package bingo.game.results;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BingoResultBarsTest {
    private BingoResultBars bingoResultBars;

    @BeforeEach
    void setup() {
        setupBingoResultBarsWithPointRequirementModifier(1);
    }

    @Test
    void getPointRequirementShouldReturnCorrectAmount() {
        assertEquals(0, bingoResultBars.getPointRequirementOfLevel(0));
        assertEquals(300, bingoResultBars.getPointRequirementOfLevel(1));
        assertEquals(500, bingoResultBars.getPointRequirementOfLevel(2));
        assertEquals(700, bingoResultBars.getPointRequirementOfLevel(3));
        assertEquals(900, bingoResultBars.getPointRequirementOfLevel(4));
        assertEquals(1200, bingoResultBars.getPointRequirementOfLevel(5));
        assertEquals(1500, bingoResultBars.getPointRequirementOfLevel(6));
        assertEquals(1800, bingoResultBars.getPointRequirementOfLevel(7));
    }

    @Test
    void getPointRequirementShouldReturnCorrectAmountWithPointValueModifier() {
        setupBingoResultBarsWithPointRequirementModifier(1.5);
        assertEquals(0, bingoResultBars.getPointRequirementOfLevel(0));
        assertEquals(450, bingoResultBars.getPointRequirementOfLevel(1));
        assertEquals(750, bingoResultBars.getPointRequirementOfLevel(2));
        assertEquals(1050, bingoResultBars.getPointRequirementOfLevel(3));
    }

    @Test
    void getPointRequirementShouldThrowIndexOutOfBoundsExceptionForNonExistentLevel() {
        assertThrows(IndexOutOfBoundsException.class, () -> bingoResultBars.getPointRequirementOfLevel(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> bingoResultBars.getPointRequirementOfLevel(8));
    }

    @Test
    void getNumberOfSubsAsRewardShouldReturnCorrectAmount() {
        assertEquals(1, bingoResultBars.getNumberOfSubsAsRewardForLevel(0));
        assertEquals(2, bingoResultBars.getNumberOfSubsAsRewardForLevel(1));
        assertEquals(4, bingoResultBars.getNumberOfSubsAsRewardForLevel(2));
        assertEquals(8, bingoResultBars.getNumberOfSubsAsRewardForLevel(3));
        assertEquals(16, bingoResultBars.getNumberOfSubsAsRewardForLevel(4));
        assertEquals(32, bingoResultBars.getNumberOfSubsAsRewardForLevel(5));
        assertEquals(64, bingoResultBars.getNumberOfSubsAsRewardForLevel(6));
        assertEquals(128, bingoResultBars.getNumberOfSubsAsRewardForLevel(7));
    }

    @Test
    void getNumberOfSubsAsStringShouldReturnCorrectDisplayText() {
        assertEquals("1 sub \uD83C\uDF81", bingoResultBars.getNumberOfSubsAsStringForLevel(0));
        assertEquals("2 subs \uD83C\uDF81", bingoResultBars.getNumberOfSubsAsStringForLevel(1));
        assertEquals("4 subs \uD83C\uDF81", bingoResultBars.getNumberOfSubsAsStringForLevel(2));
        assertEquals("8 subs \uD83C\uDF81", bingoResultBars.getNumberOfSubsAsStringForLevel(3));
    }

    @Test
    void getAllResultBarsAndRewardsInTableFormatShouldReturnLongString() {
        String expectedString = """
                | Level | Points required | Number of subs as reward: 2^(Level) |
                |---|---:|---:|
                | 0 | 0 | 2^0 = 1 sub \uD83C\uDF81 |
                | 1 | 300 | 2^1 = 2 subs \uD83C\uDF81 |
                | 2 | 500 | 2^2 = 4 subs \uD83C\uDF81 |
                | 3 | 700 | 2^3 = 8 subs \uD83C\uDF81 |
                | 4 | 900 | 2^4 = 16 subs \uD83C\uDF81 |
                | 5 | 1200 | 2^5 = 32 subs \uD83C\uDF81 |
                | 6 | 1500 | 2^6 = 64 subs \uD83C\uDF81 |
                | 7 | 1800 | 2^7 = 128 subs \uD83C\uDF81 |
                """;
        assertEquals(expectedString, bingoResultBars.getAllResultBarsAndRewardsInTableFormat());
    }

    private void setupBingoResultBarsWithPointRequirementModifier(double pointRequirementModifier) {
        bingoResultBars = new BingoResultBars(pointRequirementModifier, 7);
    }
}
