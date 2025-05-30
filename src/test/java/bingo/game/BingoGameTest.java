package bingo.game;

import bingo.game.results.BingoResult;
import bingo.restrictions.ShipRestriction;
import bingo.restrictions.impl.BannedMainArmamentType;
import bingo.rules.RetryRule;
import bingo.ships.MainArmamentType;
import bingo.ships.Ship;
import bingo.tokens.TokenCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BingoGameTest {
    private static final int START_LEVEL = 1;
    private static final int MAX_LEVEL = 7;
    private static final String END_OF_CHALLENGE_CONFIRMED =
            "\n\nEnd of challenge confirmed. Changes are no longer allowed.";
    private static final String LEVEL_SEVEN_CONGRATULATIONS =
            ". Requirement of level 7: 1800 points ✅ Unlocked reward: 128 subs \uD83C\uDF81 This is the highest reward you can get. Congratulations! \uD83C\uDF8A Total reward: 128 + (unused extra lives: 2) * 6 = 140 subs \uD83C\uDF81";
    private static final String LEVEL_ONE_GAME_OVER =
            ". Requirement of level 1: 300 points ❌ Active retry rules: None ❌ The challenge is over and you lose any unlocked rewards. Your reward for participating: 1 sub \uD83C\uDF81";
    private static final String LEVEL_ONE_IMBALANCED_MATCHMAKING =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Imbalanced matchmaking (rule 8a or 8b) ✅ ";
    private static final String LEVEL_ONE_UNFAIR_DISADVANTAGE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Unfair disadvantage (rule 8c) ✅ ";
    private static final String LEVEL_ONE_EXTRA_LIFE =
            ". Requirement of level 1: 300 points ❌ Active retry rules: Extra life (rule 8d) ✅ ";
    private static final String LEVEL_ONE_VOLUNTARY_END =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub \uD83C\uDF81";
    private static final String LEVEL_ONE_VOLUNTARY_END_WITH_EXTRA_LIFE =
            "Challenge ended voluntarily on level 1. Your reward from the previous level: 1 sub \uD83C\uDF81 Total reward: 1 + (unused extra lives: 1) * 6 = 7 subs \uD83C\uDF81";
    private static final String LEVEL_ONE_REQUIREMENT = "Requirement of level 1: 300 points";
    private static final String LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION =
            "Requirement of level 1: 300 points. Ships with aircraft as main armament are banned from use in the current level.";
    private static final String LEVEL_ONE_TRANSITION_TO_TWO =
            ". Requirement of level 1: 300 points ✅ Unlocked reward: 2 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 2: 500 points";
    private static final String LEVEL_TWO_VOLUNTARY_END =
            "Challenge ended voluntarily on level 2. Your reward from the previous level: 2 subs \uD83C\uDF81";
    private static final String LEVEL_TWO_REQUIREMENT = "Requirement of level 2: 500 points";
    private static final String LEVEL_TWO_TRANSITION_TO_THREE =
            ". Requirement of level 2: 500 points ✅ Unlocked reward: 4 subs \uD83C\uDF81 Token counter: Dummy token text. ➡️ Requirement of level 3: 700 points";
    private static final String DUMMY_TOKEN_TEXT = "Token counter: Dummy token text.";
    private static final String DUMMY_RESULT_TEXT = "Ribbon Bingo result: Dummy result text";
    private static final ShipRestriction SHIP_RESTRICTION = new BannedMainArmamentType(MainArmamentType.AIRCRAFT);

    @Mock
    private TokenCounter mockedTokenCounter;
    @Mock
    private BingoResult mockedBingoResult;

    private List<RetryRule> activeRetryRules;
    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        bingoGame = new BingoGame(mockedTokenCounter);
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
                | 0 | 0 | 2^0 = 1 sub \uD83C\uDF81 |
                | 1 | 300 | 2^1 = 2 subs \uD83C\uDF81 |
                | 2 | 500 | 2^2 = 4 subs \uD83C\uDF81 |
                | 3 | 700 | 2^3 = 8 subs \uD83C\uDF81 |
                | 4 | 900 | 2^4 = 16 subs \uD83C\uDF81 |
                | 5 | 1200 | 2^5 = 32 subs \uD83C\uDF81 |
                | 6 | 1500 | 2^6 = 64 subs \uD83C\uDF81 |
                | 7 | 1800 | 2^7 = 128 subs \uD83C\uDF81 |
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
        assertEquals(LEVEL_ONE_REQUIREMENT_WITH_SHIP_RESTRICTION, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnGameOverWhenSubmittedResultIsInsufficient() {
        mockBingoResultToString();
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_GAME_OVER.concat(END_OF_CHALLENGE_CONFIRMED), bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedDueToImbalancedMatchmaking() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockInsufficientBingoResult();
        activeRetryRules.add(RetryRule.IMBALANCED_MATCHMAKING);
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_IMBALANCED_MATCHMAKING + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedDueToAnUnfairDisadvantage() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockInsufficientBingoResult();
        activeRetryRules.add(RetryRule.UNFAIR_DISADVANTAGE);
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_UNFAIR_DISADVANTAGE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnRetryAllowedBecauseOfAnExtraLife() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockTokenCounterHasExtraLife();
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_EXTRA_LIFE + DUMMY_TOKEN_TEXT, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelTwoNextWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_ONE_TRANSITION_TO_TWO, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWhenNoResultWasSubmitted() {
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnSecondResultBarWithoutShipRestrictionWhenNoResultWasSubmitted() {
        mockSufficientBingoResult();
        bingoGame.setShipRestriction(SHIP_RESTRICTION);
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void toStringMethodShouldReturnLevelThreeNextWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterToString();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_TWO_TRANSITION_TO_THREE, bingoGame.toString());
    }

    @Test
    void toStringMethodShouldReturnCongratulationsForLevelSevenWhenSubmittedResultIsSufficient() {
        mockBingoResultToString();
        mockTokenCounterHasExtraLife();
        mockExtraLivesInTokenCounterAre(2);
        mockSufficientBingoResult();
        for (int level = START_LEVEL; level < MAX_LEVEL; level++) {
            assertSubmitBingoResultIsSuccessful();
            assertConfirmCurrentResultIsSuccessful();
        }
        assertSubmitBingoResultIsSuccessful();
        assertEquals(DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS, bingoGame.toString());
        assertConfirmCurrentResultIsSuccessful();
        assertEquals(
                DUMMY_RESULT_TEXT + LEVEL_SEVEN_CONGRATULATIONS.concat(END_OF_CHALLENGE_CONFIRMED),
                bingoGame.toString());
        verify(mockedTokenCounter, times(MAX_LEVEL - 1)).calculateMatchResult(true, true, activeRetryRules);
        verify(mockedTokenCounter, times(1)).calculateMatchResult(true, false, activeRetryRules);
        verify(mockedTokenCounter, times(MAX_LEVEL)).confirmMatchResult();
    }

    @Test
    void endChallengeShouldNotBePossibleWhenAnyResultIsSubmitted() {
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertEndChallengeIsNotSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_TWO_VOLUNTARY_END, bingoGame.toString());
    }

    @Test
    void endChallengeShouldBePossibleWhenNoResultWasSubmitted() {
        assertEndChallengeIsSuccessful();
        assertEquals(LEVEL_ONE_VOLUNTARY_END, bingoGame.toString());
    }

    @Test
    void endChallengeShouldConvertExtraLivesToSubs() {
        mockTokenCounterHasExtraLife();
        mockExtraLivesInTokenCounterAre(1);
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
        mockSufficientBingoResult();
        assertEndChallengeIsSuccessful();
        assertSubmitBingoResultIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertToStringMethodReturnsSecondResultBar();
    }

    @Test
    void submitBingoResultShouldBePossibleIfPreviousResultIsNotConfirmed() {
        mockInsufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
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
        mockSufficientBingoResult();
        assertSubmitBingoResultIsSuccessful();
        assertResetCurrentLevelIsSuccessful();
        assertToStringMethodReturnsFirstResultBar();
        verify(mockedTokenCounter).cancelMatchResult();
    }

    @Test
    void noOtherActionsShouldBeAllowedWhenEndOfChallengeIsAlreadyConfirmed() {
        assertEndChallengeIsSuccessful();
        assertConfirmCurrentResultIsSuccessful();
        assertSubmitBingoResultIsNotSuccessful();
        assertConfirmCurrentResultIsNotSuccessful();
        assertResetCurrentLevelIsNotSuccessful();
        assertEndChallengeIsNotSuccessful();
    }

    private void mockBingoResultToString() {
        when(mockedBingoResult.toString()).thenReturn(DUMMY_RESULT_TEXT);
    }

    private void mockInsufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(30L);
    }

    private void mockSufficientBingoResult() {
        when(mockedBingoResult.getPointValue()).thenReturn(3000L);
    }

    private void mockTokenCounterToString() {
        when(mockedTokenCounter.toString()).thenReturn(DUMMY_TOKEN_TEXT);
    }

    private void mockTokenCounterHasExtraLife() {
        when(mockedTokenCounter.hasExtraLife()).thenReturn(true);
    }

    private void mockExtraLivesInTokenCounterAre(int extraLives) {
        when(mockedTokenCounter.getCurrentExtraLives()).thenReturn(extraLives);
    }

    private void assertSubmitBingoResultIsSuccessful() {
        assertTrue(bingoGame.submitBingoResult(mockedBingoResult, activeRetryRules));
    }

    private void assertSubmitBingoResultIsNotSuccessful() {
        assertFalse(bingoGame.submitBingoResult(mockedBingoResult, activeRetryRules));
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
        assertEquals(LEVEL_ONE_REQUIREMENT, bingoGame.toString());
    }

    private void assertToStringMethodReturnsSecondResultBar() {
        assertEquals(LEVEL_TWO_REQUIREMENT, bingoGame.toString());
    }
}
