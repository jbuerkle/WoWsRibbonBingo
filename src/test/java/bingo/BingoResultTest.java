package bingo;

import achievements.Achievement;
import org.junit.jupiter.api.Test;
import ribbons.Ribbon;
import ships.MainArmamentType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoResultTest {

    @Test
    void getPointResultShouldReturnTwentyPoints() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.SPOTTED, 2);
        assertEquals(20, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnFiftyPoints() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        assertEquals(50, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnZeroPoints() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        assertEquals(0, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnTwoHundredAndTwentyPoints() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.SPOTTED, 6);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 4);
        result.addRibbonResult(Ribbon.CAUSED_FLOODING, 1);
        assertEquals(220, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnEightHundredPoints() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.DESTROYED, 5);
        result.addAchievementResult(Achievement.KRAKEN_UNLEASHED, 1);
        result.addAchievementResult(Achievement.DEVASTATING_STRIKE, 2);
        assertEquals(800, result.getPointResult());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForZeroRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        assertEquals("Ribbon Bingo result: 0 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForOneRibbonResult() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        assertEquals("Ribbon Bingo result: Main gun hit: 110 * 1 points = 110 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForTwoRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        assertEquals(
                "Ribbon Bingo result: Main gun hit: 110 * 1 points + Secondary hit: 30 * 1 points = 140 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForThreeRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        assertEquals(
                "Ribbon Bingo result: Bomb hit: 40 * 3 points + Main gun hit: 110 * 1 points + Secondary hit: 30 * 1 points = 260 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Main gun hit: 110 * 1 points + Secondary hit: 30 * 1 points = 480 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsAndTwoAchievementResults() {
        BingoResult result = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        result.addAchievementResult(Achievement.CLOSE_QUARTERS_EXPERT, 1);
        result.addAchievementResult(Achievement.FIRST_BLOOD, 1);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Main gun hit: 110 * 1 points + First Blood: 50 points + Secondary hit: 30 * 1 points + Close Quarters Expert: 25 points = 555 points",
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
                "Ribbon Bingo result: Main gun hit: 110 * 3 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 3 points + Secondary hit: 30 * 1 points = 700 points",
                result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithAircraftAsMainArmamentType() {
        BingoResult result = new BingoResult(MainArmamentType.AIRCRAFT);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 10);
        result.addRibbonResult(Ribbon.SPOTTED, 10);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals(
                "Ribbon Bingo result: Set on fire: 11 * 20 points + Torpedo hit: 10 * 15 points + Bomb hit: 40 * 3 points + Spotted: 10 * 5 points = 540 points",
                result.toString());
    }
}
