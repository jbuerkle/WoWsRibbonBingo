package bingo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ribbons.Ribbon;
import ships.MainArmamentType;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameTest {
    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        bingoGame = new BingoGame();
    }

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenNoResultWasSubmitted() {
        assertFalse(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenSubmittedResultIsInsufficient() {
        submitInsufficientBingoResultForLevelOne();
        assertFalse(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnTrueWhenSubmittedResultIsSufficient() {
        submitSufficientBingoResultForLevelOne();
        assertTrue(bingoGame.playerCanGoToNextLevel());
    }

    @Test
    void playerCanGoToNextLevelShouldReturnFalseWhenMaxLevelIsReached() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
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

    @Test
    void toStringMethodShouldReturnFirstResultBarWhenNoResultWasSubmitted() {
        assertEquals("Requirement of level 1: 200 points", bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnGameOverWhenSubmittedResultIsInsufficient() {
        BingoResult bingoResult = submitInsufficientBingoResultForLevelOne();
        assertToStringMethodReturnsGameOverForLevelOne(bingoResult);
    }

    @Test
    void toStringMethodShouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = submitSufficientBingoResultForLevelOne();
        assertEquals(
                bingoResult +
                        ". Requirement of level 1: 200 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 1 sub. You can now choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 2: 400 points",
                bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWhenNoResultWasSubmitted() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.goToNextLevel();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 137);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 2);
        bingoGame.submitBingoResult(bingoResult);
        bingoGame.goToNextLevel();
        bingoGame.submitBingoResult(bingoResult);
        assertEquals(
                bingoResult +
                        ". Requirement of level 2: 400 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 2 subs. You can now choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 3: 550 points",
                bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelEightWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 12);
        for (int level = 1; level < 9; level++) {
            bingoGame.submitBingoResult(bingoResult);
            bingoGame.goToNextLevel();
        }
        assertEquals(
                bingoResult +
                        ". Requirement of level 8: 1200 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 128 subs. This is the highest reward you can get. Congratulations!",
                bingoGame.toString());
    }

    @Test
    void endChallengeShouldNotAffectToStringMethodWhenSubmittedResultIsInsufficient() {
        BingoResult bingoResult = submitInsufficientBingoResultForLevelOne();
        bingoGame.endChallenge();
        assertToStringMethodReturnsGameOverForLevelOne(bingoResult);
    }

    @Test
    void endChallengeShouldAffectToStringMethodWhenSubmittedResultIsSufficient() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.endChallenge();
        assertEquals("Challenge ended voluntarily on level 1. Your reward: 1 sub", bingoGame.toString());
    }

    @Test
    void submitBingoResultShouldResetChallengeEndedFlag() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.endChallenge();
        BingoResult bingoResult = submitInsufficientBingoResultForLevelOne();
        assertToStringMethodReturnsGameOverForLevelOne(bingoResult);
    }

    @Test
    void goToNextLevelShouldResetChallengeEndedFlag() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.endChallenge();
        bingoGame.goToNextLevel();
        assertToStringMethodReturnsSecondResultBar();
    }

    private BingoResult submitInsufficientBingoResultForLevelOne() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 37);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 2);
        bingoGame.submitBingoResult(bingoResult);
        return bingoResult;
    }

    private BingoResult submitSufficientBingoResultForLevelOne() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 137);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoGame.submitBingoResult(bingoResult);
        return bingoResult;
    }

    private void assertToStringMethodReturnsGameOverForLevelOne(BingoResult bingoResult) {
        assertEquals(
                bingoResult +
                        ". Requirement of level 1: 200 points, which means your result does not meet the point requirement, and the challenge is over. You lose any unlocked rewards.",
                bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals("Requirement of level 2: 400 points", bingoGame.toString());
    }
}
