package bingo.game;

import bingo.game.results.BingoResult;
import bingo.players.Player;
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
import java.util.Optional;

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
    private static final Ship SHIP_A = new Ship("A");
    private static final Ship SHIP_B = new Ship("B");
    private static final Ship SHIP_C = new Ship("C");
    private static final Player PLAYER = new Player("Dummy"); // FIXME: backward-compatible dummy implementation

    @Mock
    private TokenCounter mockedTokenCounter;
    @Mock
    private BingoResult mockedBingoResult;

    private List<RetryRule> activeRetryRules;
    private BingoGame bingoGame;

    @BeforeEach
    void setup() {
        activeRetryRules = new LinkedList<>();
        bingoGame = new BingoGame(List.of(PLAYER), mockedTokenCounter);
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenNoneIsSet() {
        assertTrue(bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION));
    }

    @Test
    void setShipRestrictionShouldBeSuccessfulWhenThePreviousOneIsRemoved() {
        bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION);
        bingoGame.removeShipRestrictionForPlayer(PLAYER);
        assertTrue(bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION));
    }

    @Test
    void setShipRestrictionShouldNotBeSuccessfulWhenOneIsAlreadySet() {
        bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION);
        assertFalse(bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION));
    }

    @Test
    void getShipRestrictionShouldReturnEmptyOptionalWhenNoneIsSet() {
        assertTrue(bingoGame.getShipRestrictionForPlayer(PLAYER).isEmpty());
    }

    @Test
    void getShipRestrictionShouldReturnTheOneWhichWasSet() {
        bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION);
        Optional<ShipRestriction> returnedShipRestriction = bingoGame.getShipRestrictionForPlayer(PLAYER);
        assertTrue(returnedShipRestriction.isPresent());
        assertEquals(SHIP_RESTRICTION, returnedShipRestriction.get());
    }

    @Test
    void addShipUsedShouldBeSuccessfulWhenShipNamesAreUnique() {
        assertTrue(bingoGame.addShipUsed(SHIP_A));
        assertTrue(bingoGame.addShipUsed(SHIP_B));
        assertTrue(bingoGame.addShipUsed(SHIP_C));
        assertEquals(3, bingoGame.getShipsUsed().size());
    }

    @Test
    void addShipUsedShouldNotBeSuccessfulWhenItWasAlreadyAdded() {
        bingoGame.addShipUsed(new Ship("D"));
        assertFalse(bingoGame.addShipUsed(new Ship("d")));
        assertEquals(1, bingoGame.getShipsUsed().size());
    }

    @Test
    void getShipsUsedShouldReturnEmptyList() {
        assertTrue(bingoGame.getShipsUsed().isEmpty());
    }

    @Test
    void getShipsUsedShouldReturnListOfShipsInTheOrderTheyWereAdded() {
        bingoGame.addShipUsed(SHIP_A);
        bingoGame.addShipUsed(SHIP_B);
        bingoGame.addShipUsed(SHIP_C);
        List<Ship> shipsUsed = bingoGame.getShipsUsed();
        assertEquals(3, shipsUsed.size());
        Iterator<Ship> shipIterator = shipsUsed.iterator();
        assertEquals(SHIP_A, shipIterator.next());
        assertEquals(SHIP_B, shipIterator.next());
        assertEquals(SHIP_C, shipIterator.next());
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
        bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION);
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
        bingoGame.setShipRestrictionForPlayer(PLAYER, SHIP_RESTRICTION);
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
        bingoGame.setActiveRetryRules(activeRetryRules);
        assertTrue(bingoGame.submitBingoResultForPlayer(PLAYER, mockedBingoResult));
    }

    private void assertSubmitBingoResultIsNotSuccessful() {
        bingoGame.setActiveRetryRules(activeRetryRules);
        assertFalse(bingoGame.submitBingoResultForPlayer(PLAYER, mockedBingoResult));
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
