package bingo.game;

import bingo.ribbons.Ribbon;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameTest {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;

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
        for (int level = START_LEVEL; level < MAX_LEVEL; level++) {
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
                | Level | Points required | Number of subs as reward: 2^(Level) |
                |---|---:|---:|
                | 0 | 0 | 2^0 = 1 sub |
                | 1 | 400 | 2^1 = 2 subs |
                | 2 | 600 | 2^2 = 4 subs |
                | 3 | 800 | 2^3 = 8 subs |
                | 4 | 950 | 2^4 = 16 subs |
                | 5 | 1100 | 2^5 = 32 subs |
                | 6 | 1250 | 2^6 = 64 subs |
                | 7 | 1400 | 2^7 = 128 subs |
                """;
        assertEquals(expectedString, bingoGame.getAllResultBarsAndRewardsInTableFormat());
    }

    @Test
    void toStringMethodShouldReturnFirstResultBarWhenNoResultWasSubmitted() {
        assertToStringMethodReturnsFirstResultBar();
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
                        ". Requirement of level 1: 400 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 2 subs. You can now choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 2: 600 points",
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
                        ". Requirement of level 2: 600 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 4 subs. You can now choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 3: 800 points",
                bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelEightWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 12);
        for (int level = START_LEVEL; level <= MAX_LEVEL; level++) {
            bingoGame.submitBingoResult(bingoResult);
            bingoGame.goToNextLevel();
        }
        assertEquals(
                bingoResult +
                        ". Requirement of level 7: 1400 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 128 subs. This is the highest reward you can get. Congratulations!",
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
        assertEquals("Challenge ended voluntarily on level 1. Your reward: 2 subs", bingoGame.toString());
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

    @Test
    void doResetForCurrentLevelShouldResetChallengeEndedFlag() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.endChallenge();
        bingoGame.doResetForCurrentLevel();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldRemovePreviouslySubmittedResult() {
        submitSufficientBingoResultForLevelOne();
        bingoGame.doResetForCurrentLevel();
        assertToStringMethodReturnsFirstResultBar();
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
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 237);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoGame.submitBingoResult(bingoResult);
        return bingoResult;
    }

    private void assertToStringMethodReturnsGameOverForLevelOne(BingoResult bingoResult) {
        assertEquals(
                bingoResult +
                        ". Requirement of level 1: 400 points, which means your result does not meet the point requirement, and the challenge is over. You lose any unlocked rewards. Your reward for participating: 1 sub",
                bingoGame.toString());
    }

    private void assertToStringMethodReturnsFirstResultBar() {
        assertEquals("Requirement of level 1: 400 points", bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals("Requirement of level 2: 600 points", bingoGame.toString());
    }
}
