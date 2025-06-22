package bingo.game.results;

import bingo.achievements.Achievement;
import bingo.ribbons.Ribbon;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoResultTest {

    @Test
    void getPointValueShouldReturnCorrectValueForOneRibbonResult() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.SPOTTED, 2);
        assertEquals(60, result.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOverwrittenAchievementResult() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addAchievementResult(Achievement.CLOSE_QUARTERS_EXPERT, 2);
        result.addAchievementResult(Achievement.CLOSE_QUARTERS_EXPERT, 1);
        assertEquals(25, result.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForOverwrittenRibbonResult() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.INCAPACITATION, 3);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        assertEquals(50, result.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForMultipleRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.SPOTTED, 6);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 4);
        result.addRibbonResult(Ribbon.CAUSED_FLOODING, 1);
        assertEquals(380, result.getPointValue());
    }

    @Test
    void getPointValueShouldReturnCorrectValueForMixedResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.DESTROYED, 5);
        result.addAchievementResult(Achievement.KRAKEN_UNLEASHED, 1);
        result.addAchievementResult(Achievement.DEVASTATING_STRIKE, 1);
        assertEquals(800, result.getPointValue());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextWhenAddingResultsWithAmountZero() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 0);
        result.addAchievementResult(Achievement.DEVASTATING_STRIKE, 0);
        assertEquals("Ribbon Bingo result: 0 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForZeroRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        assertEquals("Ribbon Bingo result: 0 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForOneRibbonResult() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        assertEquals("Ribbon Bingo result: 110 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForTwoRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        assertEquals(
                "Ribbon Bingo result: Main gun hit: 110 points + Secondary hit: 30 points = 140 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForThreeRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        assertEquals(
                "Ribbon Bingo result: Bomb hit: 40 * 3 points + Main gun hit: 110 points + Secondary hit: 30 points = 260 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Main gun hit: 110 points + Secondary hit: 30 points = 480 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsAndTwoAchievementResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        result.addAchievementResult(Achievement.CLOSE_QUARTERS_EXPERT, 1);
        result.addAchievementResult(Achievement.FIRST_BLOOD, 1);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Main gun hit: 110 points + First Blood: 50 points + Secondary hit: 30 points + Close Quarters Expert: 25 points = 555 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithMediumCaliberGunsAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 13);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 13 * 20 points + Main gun hit: 110 * 2 points + Bomb hit: 40 * 3 points + Secondary hit: 30 points = 630 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithLargeCaliberGunsAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.LARGE_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Main gun hit: 110 * 3 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Secondary hit: 30 points = 700 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithExtraLargeCaliberGunsAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.EXTRA_LARGE_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Main gun hit: 110 * 4 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Secondary hit: 30 points = 810 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithTorpedoesAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.TORPEDOES);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 20);
        result.addRibbonResult(Ribbon.SPOTTED, 10);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Torpedo hit: 20 * 40 points + Spotted: 10 * 30 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points = 1440 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithAircraftAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.AIRCRAFT);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 20);
        result.addRibbonResult(Ribbon.SPOTTED, 10);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Torpedo hit: 20 * 20 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Spotted: 10 * 10 points = 840 points",
                result.toString());
    }
}
