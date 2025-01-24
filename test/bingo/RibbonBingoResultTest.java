package bingo;

import org.junit.jupiter.api.Test;
import ribbons.Ribbon;
import ribbons.RibbonResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonBingoResultTest {

    @Test
    void getPointResultShouldReturnTenPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SPOTTED, 2);
        assertEquals(10, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnFiftyPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        assertEquals(50, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnZeroPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.INCAPACITATION, 5);
        result.addRibbonResult(Ribbon.INCAPACITATION, 0);
        assertEquals(0, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnOneHundredAndFiftyPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SPOTTED, 6);
        result.addRibbonResult(Ribbon.TORPEDO_HIT, 4);
        result.addRibbonResult(Ribbon.CAUSED_FLOODING, 1);
        assertEquals(150, result.getPointResult());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForZeroRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        assertEquals("Ribbon Bingo result: 0 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForOneRibbonResult() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SHELL_HIT, 110);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points = 110 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForTwoRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SHELL_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points + Secondary hit: 30 * 1 points = 140 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForThreeRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SHELL_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 220 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        result.addRibbonResult(Ribbon.SHELL_HIT, 110);
        result.addRibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(Ribbon.SET_ON_FIRE, 11);
        assertEquals("Ribbon Bingo result: Set on fire: 11 * 20 points + Shell hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 440 points", result.toString());
    }
}
