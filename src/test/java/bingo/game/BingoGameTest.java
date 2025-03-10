package bingo.game;

import bingo.game.results.BingoResult;
import bingo.ribbons.Ribbon;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameTest {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 1400 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 128 subs. This is the highest reward you can get. Congratulations! Your unused extra lives are converted to 6 subs each, for a total of 134 subs.";
    public static final String LEVEL_ONE_GAME_OVER =
            ". Requirement of level 1: 400 points, which means your result does not meet the point requirement. The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub";

    private List<RetryRule> activeRetryRules;
    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        bingoGame = new BingoGame();
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
        BingoResult bingoResult = createInsufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_ONE_GAME_OVER, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(bingoResult + LEVEL_ONE_GAME_OVER.concat(END_OF_CHALLENGE_CONFIRMED), bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(
                bingoResult +
                        ". Requirement of level 1: 400 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 2 subs. You gain 1 token for a successful match as per rule 9a. You now have 1 token. You can choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 2: 600 points",
                bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWhenNoResultWasSubmitted() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 137);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 3);
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(
                bingoResult +
                        ". Requirement of level 2: 600 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 4 subs. You gain 1 token for a successful match as per rule 9a. You now have 2 tokens. You can choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 3: 800 points",
                bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 12);
        for (int level = START_LEVEL; level < MAX_LEVEL; level++) {
            assertSubmitBingoResultIsSuccessful(bingoResult);
            assertConfirmCurrentResultIsSuccessful();
        }
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_SEVEN_CONGRATULATIONS, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(
                bingoResult + LEVEL_SEVEN_CONGRATULATIONS.concat(END_OF_CHALLENGE_CONFIRMED),
                bingoGame.toString());
    }

    @Test
    void endChallengeShouldNotBePossibleWhenAnyResultIsSubmitted() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEndChallengeIsNotSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertEndChallengeIsSuccessful();
        assertEquals(
                "Challenge ended voluntarily on level 2. Your reward from the previous level: 2 subs.",
                bingoGame.toString());
    }

    @Test
    void endChallengeShouldBePossibleWhenNoResultWasSubmitted() {
        assertEndChallengeIsSuccessful();
        assertEquals(
                "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub.",
                bingoGame.toString());
    }

    @Test
    void endChallengeTwiceShouldBePossibleButHaveNoAdditionalEffect() {
        assertEndChallengeIsSuccessful();
        endChallengeShouldBePossibleWhenNoResultWasSubmitted();
    }

    @Test
    void submitBingoResultShouldBePossibleIfEndOfChallengeIsNotConfirmed() {
        assertEndChallengeIsSuccessful();
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void submitBingoResultShouldBePossibleIfPreviousResultIsNotConfirmed() {
        BingoResult insufficientBingoResult = createInsufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(insufficientBingoResult);
        BingoResult sufficientBingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(sufficientBingoResult);
    }

    @Test
    void confirmCurrentResultShouldNotBePossibleWhenThereIsNothingToConfirm() {
        assertConfirmCurrentResultIsNotSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldBePossibleWhenNoResultWasSubmitted() {
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldCancelEndOfChallenge() {
        assertEndChallengeIsSuccessful();
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void doResetForCurrentLevelShouldRemovePreviouslySubmittedResult() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void noOtherActionsShouldBeAllowedWhenEndOfChallengeIsAlreadyConfirmed() {
        assertEndChallengeIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsNotSuccessful(bingoResult);
        assertConfirmCurrentResultIsNotSuccessful();
        assertResetCurrentLevelIsNotSuccessful();
        assertEndChallengeIsNotSuccessful();
    }

    private BingoResult createInsufficientBingoResultForLevelOne() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 37);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 2);
        return bingoResult;
    }

    private BingoResult createSufficientBingoResultForLevelOne() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_OR_MEDIUM_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 237);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        return bingoResult;
    }

    private void assertSubmitBingoResultIsSuccessful(BingoResult bingoResult) {
        assertTrue(bingoGame.submitBingoResult(bingoResult, activeRetryRules));
    }

    private void assertSubmitBingoResultIsNotSuccessful(BingoResult bingoResult) {
        assertFalse(bingoGame.submitBingoResult(bingoResult, activeRetryRules));
    }

    private void assertConfirmCurrentResultIsSuccessful() {
        assertTrue(bingoGame.confirmCurrentResult());
    }

    private void assertConfirmCurrentResultIsNotSuccessful() {
        assertFalse(bingoGame.confirmCurrentResult());
    }

    private void assertResetCurrentLevelIsSuccessful() {
        assertTrue(bingoGame.doResetForCurrentLevel());
    }

    private void assertResetCurrentLevelIsNotSuccessful() {
        assertFalse(bingoGame.doResetForCurrentLevel());
    }

    private void assertEndChallengeIsSuccessful() {
        assertTrue(bingoGame.endChallenge());
    }

    private void assertEndChallengeIsNotSuccessful() {
        assertFalse(bingoGame.endChallenge());
    }

    private void assertToStringMethodReturnsFirstResultBar() {
        assertEquals("Requirement of level 1: 400 points", bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals("Requirement of level 2: 600 points", bingoGame.toString());
    }
}
