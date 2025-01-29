package bingo;

import org.junit.jupiter.api.Test;
import ribbons.Ribbon;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BingoResultTest {

    @Test
    void getPointResultShouldReturnTenPoints() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.SPOTTED, 2);
        assertEquals(10, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnFiftyPoints() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        assertEquals(50, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnZeroPoints() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        assertEquals(0, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnOneHundredAndFiftyPoints() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.SPOTTED, 6);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 4);
        result.addRibbonResult(Ribbon.CAUSED_FLOODING, 1);
        assertEquals(150, result.getPointResult());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForZeroRibbonResults() {
        BingoResult result = new BingoResult(false);
        assertEquals("Ribbon Bingo result: 0 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForOneRibbonResult() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        assertEquals("Ribbon Bingo result: Main gun hit: 110 * 1 points = 110 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForTwoRibbonResults() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        assertEquals("Ribbon Bingo result: Main gun hit: 110 * 1 points + Secondary hit: 30 * 1 points = 140 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForThreeRibbonResults() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        assertEquals("Ribbon Bingo result: Main gun hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 220 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResults() {
        BingoResult result = new BingoResult(false);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals("Ribbon Bingo result: Set on fire: 11 * 20 points + Main gun hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 440 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResultsWithModifierEnabled() {
        BingoResult result = new BingoResult(true);
        result.addRibbonResult(Ribbon.MAIN_GUN_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals("Ribbon Bingo result: Main gun hit: 110 * 3 points + Set on fire: 11 * 20 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 660 points", result.toString());
    }
}
