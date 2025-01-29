package bingo;

import org.junit.jupiter.api.Test;
import ribbons.Ribbon;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameTest {

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenNoResultWasSubmitted() {
        BingoGame bingoGame = new BingoGame();
        assertFalse(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenSubmittedResultIsInsufficient() {
        BingoGame bingoGame = new BingoGame();
        BingoResult bingoResult = new BingoResult(false);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 37);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 2);
        bingoGame.submitBingoResult(bingoResult);
        assertFalse(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnTrueWhenSubmittedResultIsSufficient() {
        BingoGame bingoGame = new BingoGame();
        BingoResult bingoResult = new BingoResult(false);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 137);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoGame.submitBingoResult(bingoResult);
        assertTrue(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenMaxLevelIsReached() {
        BingoGame bingoGame = new BingoGame();
        BingoResult bingoResult = new BingoResult(false);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 12);
        for (int level = 1; level < 8; level++) {
            bingoGame.submitBingoResult(bingoResult);
            assertTrue(bingoGame.playerCanGoToNextLevel());
            bingoGame.goToNextLevel();
        }
        bingoGame.submitBingoResult(bingoResult);
        assertFalse(bingoGame.playerCanGoToNextLevel());
        bingoGame.goToNextLevel();
    }

    @Test
    void getAllResultBarsAndRewardsInTableFormatShouldReturnLongString() {
        BingoGame bingoGame = new BingoGame();
        String expectedString = """
                | Level | Points required | Number of subs as reward: 2^(Level-1) |
                |---|---:|---:|
                | 1 | 200 | 2^0 = 1 sub |
                | 2 | 400 | 2^1 = 2 subs |
                | 3 | 550 | 2^2 = 4 subs |
                | 4 | 700 | 2^3 = 8 subs |
                | 5 | 850 | 2^4 = 16 subs |
                | 6 | 1000 | 2^5 = 32 subs |
                | 7 | 1100 | 2^6 = 64 subs |
                | 8 | 1200 | 2^7 = 128 subs |
                """;
        assertEquals(expectedString, bingoGame.getAllResultBarsAndRewardsInTableFormat());
    }
}
