package bingo;

import org.junit.jupiter.api.Test;
import ribbons.Ribbon;
import ribbons.RibbonResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RibbonBingoResultTest {

    @Test
    void getPointResultShouldReturnTenPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.SPOTTED, 2);
        result.addRibbonResult(resultA);
        assertEquals(10, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnFiftyPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.INCAPACITATION, 0);
        RibbonResult resultB = new RibbonResult(Ribbon.INCAPACITATION, 5);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        assertEquals(50, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnZeroPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.INCAPACITATION, 5);
        RibbonResult resultB = new RibbonResult(Ribbon.INCAPACITATION, 0);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        assertEquals(0, result.getPointResult());
    }

    @Test
    void getPointResultShouldReturnOneHundredAndFiftyPoints() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.SPOTTED, 6);
        RibbonResult resultB = new RibbonResult(Ribbon.TORPEDO_HIT, 4);
        RibbonResult resultC = new RibbonResult(Ribbon.CAUSED_FLOODING, 1);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        result.addRibbonResult(resultC);
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
        RibbonResult resultA = new RibbonResult(Ribbon.SHELL_HIT, 110);
        result.addRibbonResult(resultA);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points = 110 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForTwoRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.SHELL_HIT, 110);
        RibbonResult resultB = new RibbonResult(Ribbon.SECONDARY_HIT, 30);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points + Secondary hit: 30 * 1 points = 140 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForThreeRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.SHELL_HIT, 110);
        RibbonResult resultB = new RibbonResult(Ribbon.SECONDARY_HIT, 30);
        RibbonResult resultC = new RibbonResult(Ribbon.BOMB_HIT, 40);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        result.addRibbonResult(resultC);
        assertEquals("Ribbon Bingo result: Shell hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 220 points", result.toString());
    }

    @Test
    void toStringMethodShouldReturnCorrectDisplayTextForFourRibbonResults() {
        RibbonBingoResult result = new RibbonBingoResult();
        RibbonResult resultA = new RibbonResult(Ribbon.SHELL_HIT, 110);
        RibbonResult resultB = new RibbonResult(Ribbon.SECONDARY_HIT, 30);
        RibbonResult resultC = new RibbonResult(Ribbon.BOMB_HIT, 40);
        RibbonResult resultD = new RibbonResult(Ribbon.SET_ON_FIRE, 11);
        result.addRibbonResult(resultA);
        result.addRibbonResult(resultB);
        result.addRibbonResult(resultC);
        result.addRibbonResult(resultD);
        assertEquals("Ribbon Bingo result: Set on fire: 11 * 20 points + Shell hit: 110 * 1 points + Bomb hit: 40 * 2 points + Secondary hit: 30 * 1 points = 440 points", result.toString());
    }
}
