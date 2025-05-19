package bingo.game;

import bingo.achievements.Achievement;
import bingo.game.results.BingoResult;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.ribbons.Ribbon;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BingoGameTest {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 1800 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 128 subs. This is the highest reward you can get. Congratulations! Your unused extra lives are converted to 6 subs each, for a total of 134 subs.";
    private static final String LEVEL_ONE_GAME_OVER =
            ". Requirement of level 1: 300 points, which means your result does not meet the point requirement. The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub";
    private static final String LEVEL_ONE_IMBALANCED_MATCHMAKING =
            ". Requirement of level 1: 300 points, which means your result does not meet the point requirement. You are allowed to retry due to imbalanced matchmaking (rule 8a or 8b). You gain 1 token due to imbalanced matchmaking as per rule 9b. You now have 1 token.";
    private static final String LEVEL_ONE_UNFAIR_DISADVANTAGE =
            ". Requirement of level 1: 300 points, which means your result does not meet the point requirement. You are allowed to retry due to an unfair disadvantage (rule 8c). You now have 0 tokens.";
    private static final String LEVEL_ONE_EXTRA_LIFE =
            ". Requirement of level 1: 300 points, which means your result does not meet the point requirement. You are allowed to retry because you have an extra life. You lose 1 extra life. You now have 2 tokens.";
    private static final String LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub. Your unused extra lives are converted to 6 subs each, for a total of 7 subs.";
    private static final String LEVEL_ONE_TRANSITION_TO_TWO =
            ". Requirement of level 1: 300 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 2 subs. You gain 1 token for a successful match as per rule 9a. You now have 1 token. You can choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 2: 500 points";
    private static final String LEVEL_TWO_TRANSITION_TO_THREE =
            ". Requirement of level 2: 500 points, which means your result meets the point requirement, and you unlocked the reward for the current level: 4 subs. You gain 1 token for a successful match as per rule 9a. You now have 2 tokens. You can choose to end the challenge and receive your reward, or continue to the next level. Requirement of level 3: 700 points";
    private static final ShipRestriction SHIP_RESTRICTION = new BannedMainArmamentType(MainArmamentType.AIRCRAFT);

    private List<RetryRule> activeRetryRules;
    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        bingoGame = new BingoGame();
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenNoneIsSet() {
        assertTrue(bingoGame.setShipRestriction(SHIP_RESTRICTION));
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenThePreviousOneIsRemoved() {
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        bingoGame.removeShipRestriction();
        assertTrue(bingoGame.setShipRestriction(SHIP_RESTRICTION));
    }

    @Test
    void setShipRestrictionShouldNotBeSuccessfulWhenOneIsAlreadySet() {
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        assertFalse(bingoGame.setShipRestriction(SHIP_RESTRICTION));
    }

    @Test
    void getShipRestrictionShouldReturnNullWhenNoneIsSet() {
        assertNull(bingoGame.getShipRestriction());
    }

    @Test
    void getShipRestrictionShouldReturnTheOneWhichWasSet() {
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        assertEquals(SHIP_RESTRICTION, bingoGame.getShipRestriction());
    }

    @Test
    void addShipUsedShouldBeSuccessfulWhenShipNamesAreUnique() {
        assertTrue(bingoGame.addShipUsed("A"));
        assertTrue(bingoGame.addShipUsed("B"));
        assertTrue(bingoGame.addShipUsed("C"));
        assertEquals(3, bingoGame.getShipsUsed().size());
    }

    @Test
    void addShipUsedShouldNotBeSuccessfulWhenItWasAlreadyAdded() {
        bingoGame.addShipUsed("D");
        assertFalse(bingoGame.addShipUsed("d"));
        assertEquals(1, bingoGame.getShipsUsed().size());
    }

    @Test
    void getShipsUsedShouldReturnEmptyList() {
        assertTrue(bingoGame.getShipsUsed().isEmpty());
    }

    @Test
    void getShipsUsedShouldReturnListOfShipsInTheOrderTheyWereAdded() {
        bingoGame.addShipUsed("A");
        bingoGame.addShipUsed("B");
        bingoGame.addShipUsed("C");
        List<Ship> shipsUsed = bingoGame.getShipsUsed();
        assertEquals(3, shipsUsed.size());
        Iterator<Ship> shipIterator = shipsUsed.iterator();
        assertEquals("A", shipIterator.next().name());
        assertEquals("B", shipIterator.next().name());
        assertEquals("C", shipIterator.next().name());
    }

    @Test
    void getAllResultBarsAndRewardsInTableFormatShouldReturnLongString() {
        String expectedString = """
                | Level | Points required | Number of subs as reward: 2^(Level) |
                |---|---:|---:|
                | 0 | 0 | 2^0 = 1 sub |
                | 1 | 300 | 2^1 = 2 subs |
                | 2 | 500 | 2^2 = 4 subs |
                | 3 | 700 | 2^3 = 8 subs |
                | 4 | 900 | 2^4 = 16 subs |
                | 5 | 1200 | 2^5 = 32 subs |
                | 6 | 1500 | 2^6 = 64 subs |
                | 7 | 1800 | 2^7 = 128 subs |
                """;
        assertEquals(expectedString, bingoGame.getAllResultBarsAndRewardsInTableFormat());
    }

    @Test
    void toStringMethodShouldReturnFirstResultBarWhenNoResultWasSubmitted() {
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnFirstResultBarWithShipRestrictionWhenNoResultWasSubmitted() {
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        assertEquals(
                "Requirement of level 1: 300 points. Ships with aircraft as main armament are banned from use in the current level.",
                bingoGame.toString());
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
    void toStringMethodShouldReturnRetryAllowedDueToImbalancedMatchmaking() {
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        BingoResult bingoResult = createInsufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_ONE_IMBALANCED_MATCHMAKING, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedDueToAnUnfairDisadvantage() {
        activeRetryRules.add(RetryRule.UNFAIR_DISADVANTAGE);
        BingoResult bingoResult = createInsufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_ONE_UNFAIR_DISADVANTAGE, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedBecauseOfAnExtraLife() {
        addTokensUntilCounterIsAtEight();
        BingoResult bingoResult = createInsufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_ONE_EXTRA_LIFE, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_ONE_TRANSITION_TO_TWO, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWhenNoResultWasSubmitted() {
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWithoutShipRestrictionWhenNoResultWasSubmitted() {
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        BingoResult bingoResult = createSufficientBingoResultForLevelOne();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 137);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 3);
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsSuccessful(bingoResult);
        assertEquals(bingoResult + LEVEL_TWO_TRANSITION_TO_THREE, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.DESTROYED, 12);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 300);
        bingoResult.addAchievementResult(Achievement.KRAKEN_UNLEASHED, 1);
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
    void endChallengeShouldConvertExtraLivesToSubs() {
        addTokensUntilCounterIsAtEight();
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE.concat(END_OF_CHALLENGE_CONFIRMED), bingoGame.toString());
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
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 37);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 2);
        return bingoResult;
    }

    private BingoResult createSufficientBingoResultForLevelOne() {
        BingoResult bingoResult = new BingoResult(MainArmamentType.SMALL_CALIBER_GUNS);
        bingoResult.addRibbonResult(Ribbon.MAIN_GUN_HIT, 237);
        bingoResult.addRibbonResult(Ribbon.SET_ON_FIRE, 12);
        return bingoResult;
    }

    private void addTokensUntilCounterIsAtEight() {
        BingoResult bingoResult = createInsufficientBingoResultForLevelOne();
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        for (int i = 0; i < 8; i++) {
            assertSubmitBingoResultIsSuccessful(bingoResult);
            assertConfirmCurrentResultIsSuccessful();
        }
        activeRetryRules.remove(RetryRule.IMBALANCED_MATCHMAKING);
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
        assertEquals("Requirement of level 1: 300 points", bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals("Requirement of level 2: 500 points", bingoGame.toString());
    }
}
